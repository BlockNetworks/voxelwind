package com.voxelwind.server.game.level.chunk.util;

import com.voxelwind.server.network.mcpe.packets.McpeLevelChunkData;

public interface FullChunkPacketCreator {
    McpeLevelChunkData toFullChunkData();
}
