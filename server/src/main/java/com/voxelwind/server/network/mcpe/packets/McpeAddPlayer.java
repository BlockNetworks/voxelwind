package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.util.Rotation;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.permissions.PermissionLevel;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.mcpe.util.WorldFlag;
import com.voxelwind.server.network.mcpe.util.EntityLink;
import com.voxelwind.server.network.mcpe.util.metadata.MetadataDictionary;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class McpeAddPlayer implements NetworkPackage {
    private UUID uuid;
    private String username;
    private long entityId;
    private long runtimeEntityId;
    private String platformChatId;
    private Vector3f position;
    private Vector3f velocity;
    private Rotation rotation;
    private ItemStack held;
    private final MetadataDictionary metadata = new MetadataDictionary();
    private int flags;
    private int commandPermission;
    private int worldFlags;
    private PermissionLevel permissionLevel;
    private int customFlags;
    private long userId;
    private final List<EntityLink> entityLinks = new ArrayList<>();
    private String deviceId;

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        McpeUtil.writeUuid(buffer, uuid);
        McpeUtil.writeVarintLengthString(buffer, username);
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsigned(buffer, runtimeEntityId);
        McpeUtil.writeVarintLengthString(buffer, platformChatId);
        McpeUtil.writeVector3f(buffer, position);
        McpeUtil.writeVector3f(buffer, velocity);
        McpeUtil.writeRotation(buffer, rotation);
        McpeUtil.writeItemStack(buffer, held);
        metadata.writeTo(buffer);
        Varints.encodeUnsigned(buffer, flags);
        Varints.encodeUnsigned(buffer, commandPermission);
        Varints.encodeUnsigned(buffer, worldFlags);
        Varints.encodeUnsigned(buffer, permissionLevel.ordinal());
        Varints.encodeUnsigned(buffer, customFlags);
        buffer.writeLongLE(userId);
        Varints.encodeUnsigned(buffer, entityLinks.size());
        for(EntityLink link : entityLinks) {
            McpeUtil.writeEntityLink(buffer, link);
        }
        McpeUtil.writeVarintLengthString(buffer, deviceId);
    }

    public void setWorldFlags(WorldFlag flag, boolean value) {
        if (value) {
            worldFlags |= flag.getVal();
        } else {
            worldFlags &= ~flag.getVal();
        }
    }
}
