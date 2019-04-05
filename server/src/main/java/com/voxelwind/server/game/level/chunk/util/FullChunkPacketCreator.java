package com.voxelwind.server.game.level.chunk.util;

import com.voxelwind.server.network.mcpe.packets.McpeFullChunkData;

public interface FullChunkPacketCreator {
    McpeFullChunkData toFullChunkData();
}
