package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeResourcePackStack implements NetworkPackage{
    private boolean mustAccept;
    private boolean experimental;
    private String gameVersion;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeBoolean(mustAccept);
        buffer.writeShortLE(0); // TODO: Implement behaviorPackStack
        buffer.writeShortLE(0); // TODO: Implement resourcePackStack
        buffer.writeBoolean(experimental);
        McpeUtil.writeVarintLengthString(buffer, gameVersion);
    }
}
