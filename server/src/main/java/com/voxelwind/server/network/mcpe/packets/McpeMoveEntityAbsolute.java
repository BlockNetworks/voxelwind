package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class McpeMoveEntityAbsolute implements NetworkPackage {
    private long runtimeEntityId;
    private Vector3f position;
    private Rotation rotation;
    private boolean onGround;
    private boolean teleported;

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = Varints.decodeUnsigned(buffer);
        int flags = buffer.readUnsignedByte();
        onGround = (flags & Flag.GROUND.ordinal()) != 0;
        teleported = (flags & Flag.TELEPORT.ordinal()) != 0;
        position = McpeUtil.readVector3f(buffer);
        rotation = McpeUtil.readByteRotation(buffer);
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeUnsigned(buffer, runtimeEntityId);
        int flags = 0;
        if (onGround) {
            flags |= Flag.GROUND.ordinal();
        }
        if (teleported) {
            flags |= Flag.TELEPORT.ordinal();
        }
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeByteRotation(buffer, rotation);
    }

    public enum Flag {
        GROUND,
        TELEPORT
    }
}
