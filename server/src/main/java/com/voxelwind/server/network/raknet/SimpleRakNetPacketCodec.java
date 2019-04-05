package com.voxelwind.server.network.raknet;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.PacketRegistry;
import com.voxelwind.server.network.PacketType;
import com.voxelwind.server.network.raknet.datagrams.RakNetDatagramFlags;
import com.voxelwind.server.network.raknet.enveloped.DirectAddressedRakNetPacket;
import com.voxelwind.server.network.raknet.packets.AckPacket;
import com.voxelwind.server.network.raknet.packets.NakPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SimpleRakNetPacketCodec extends MessageToMessageCodec<DatagramPacket, DirectAddressedRakNetPacket> {
    private static final int USER_ID_START = 0x80;
    private static final byte[] QUERY_SIGNATURE = new byte[]{(byte) 0xfe, (byte) 0xfd};

    @Override
    protected void encode(ChannelHandlerContext ctx, DirectAddressedRakNetPacket pkg, List<Object> list) throws Exception {
        // Certain RakNet packets do not require special encapsulation. This encoder tries to handle them.
        try {
            ByteBuf buf = PacketRegistry.tryEncode(pkg.content());
            list.add(new DatagramPacket(buf, pkg.recipient(), pkg.sender()));
        } finally {
            pkg.release();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> list) throws Exception {
        // Certain RakNet packets do not require special encapsulation. This encoder tries to handle them.
        ByteBuf buf = packet.content();
        if (buf.readableBytes() == 0) {
            // not interested
            return;
        }
        buf.markReaderIndex();

        if (buf.readableBytes() > 2) {
            byte[] prefix = new byte[2];
            buf.readBytes(prefix);
            if (Arrays.equals(prefix, QUERY_SIGNATURE)) {
                // Query packet detected.
                NetworkPackage netPackage = PacketRegistry.tryDecode(buf.readSlice(buf.readableBytes()), PacketType.QUERY);
                if (netPackage != null) {
                    list.add(new DirectAddressedRakNetPacket(netPackage, packet.recipient(), packet.sender()));
                }
                return;
            }
            buf.resetReaderIndex();
        }

        int id = buf.getUnsignedByte(0);
        if (id < USER_ID_START) { // User data
            // We can decode a packet immediately.
            NetworkPackage netPackage = PacketRegistry.tryDecode(buf, PacketType.RAKNET);
            if (netPackage != null) {
                list.add(new DirectAddressedRakNetPacket(netPackage, packet.recipient(), packet.sender()));
            }
        } else {
            // We can decode some datagrams directly.
            RakNetDatagramFlags flags = new RakNetDatagramFlags(buf.readByte());
            if (flags.isValid()) {
                if (flags.isAck()) {
                    // ACK
                    AckPacket ackPacket = new AckPacket();
                    ackPacket.decode(buf);
                    list.add(new DirectAddressedRakNetPacket(ackPacket, packet.recipient(), packet.sender()));
                } else if (flags.isNak()) {
                    // NAK
                    NakPacket nakPacket = new NakPacket();
                    nakPacket.decode(buf);
                    list.add(new DirectAddressedRakNetPacket(nakPacket, packet.recipient(), packet.sender()));
                } else {
                    list.add(packet.retain()); // needs further processing
                }
            }
            buf.resetReaderIndex();
        }
    }
}
