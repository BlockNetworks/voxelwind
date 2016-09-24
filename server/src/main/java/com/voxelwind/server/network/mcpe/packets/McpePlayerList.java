package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.api.server.Skin;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import com.voxelwind.server.network.raknet.RakNetUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class McpePlayerList implements NetworkPackage {
    private byte type;
    private final List<Record> records = new ArrayList<>();

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        buffer.writeByte(type);
        buffer.writeInt(records.size());
        for (Record record : records) {
            McpeUtil.writeUuid(buffer, record.uuid);
            // 0 is ADD, 1 is REMOVE
            if (type == 0) {
                buffer.writeLong(record.entityId);
                RakNetUtil.writeString(buffer, record.name);
                McpeUtil.writeSkin(buffer, record.skin);
            }
        }
    }

    @Data
    public static class Record {
        private final UUID uuid;
        private long entityId;
        private String name;
        private Skin skin;
    }
}
