package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeMovePlayer implements NetworkPackage {
    private long runtimeEntityId;
    private Vector3f position;
    private Rotation rotation;
    private byte mode;
    private boolean onGround;
    private long ridingEntityId;

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = Varints.decodeUnsigned(buffer);
        position = McpeUtil.readVector3f(buffer);
        rotation = McpeUtil.readByteRotation(buffer);
        mode = buffer.readByte();
        onGround = buffer.readBoolean();
        ridingEntityId = Varints.decodeUnsigned(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, runtimeEntityId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeByteRotation(buffer, rotation);
        buffer.writeByte(mode);
        buffer.writeBoolean(onGround);
        Varints.encodeUnsigned(buffer, ridingEntityId);
    }
}
