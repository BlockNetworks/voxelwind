package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class McpeResourcePackClientResponse implements NetworkPackage {
    private Status responseStatus;
    @Getter
    private final List<String> packIds = new ArrayList<>();


    @Override
    public void decode(ByteBuf buffer) {
        responseStatus = Status.values()[buffer.readByte()];
        int idCount = buffer.readShortLE();
        for (int i = 0; i < idCount; i++) {
            packIds.add(McpeUtil.readVarintLengthString(buffer));
        }
    }

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    public enum Status {
        NONE,
        REFUSED,
        SEND_PACKS,
        HAVE_ALL_PACKS,
        COMPLETED
    }
}
