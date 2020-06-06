package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude = {"data"})
@EqualsAndHashCode(exclude = {"data"})
public class McpeLevelChunkData implements NetworkPackage {
    private int chunkX;
    private int chunkZ;
    private int subChunksLength;
    private boolean cachingEnabled;
    private byte[] data;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSigned(buffer, chunkX);
        Varints.encodeSigned(buffer, chunkZ);
        Varints.encodeUnsigned(buffer, subChunksLength);
        buffer.writeBoolean(cachingEnabled);
        // TODO: caching stuff
        McpeUtil.writeByteArray(buffer, data);
    }
}
