package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeTickSync implements NetworkPackage {
    private long requestTimestamp;
    private long responseTimestamp;

    @Override
    public void decode(ByteBuf buffer) {
        requestTimestamp = buffer.readLongLE();
        responseTimestamp = buffer.readLongLE();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeLongLE(requestTimestamp);
        buffer.writeLongLE(responseTimestamp);
    }
}
