package com.voxelwind.server.game.level.manager;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.level.block.VoxelwindBlockStateBuilder;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.McpeUtil;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LevelPaletteManager {
    private static final int RUNTIMEID_TABLE_CAPACITY = 4467;
    private final ArrayList<BlockState> runtimeId2BlockState;
    private final TObjectIntMap<BlockState> blockState2RuntimeId = new TObjectIntHashMap<>(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1);
    private final TIntIntMap legacyId2RuntimeId = new TIntIntHashMap(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1, -1);
    private final ByteBuf cachedPallete;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private int runtimeIdAllocator = 0;
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public LevelPaletteManager() {
        InputStream stream = VoxelwindServer.class.getClassLoader().getResourceAsStream("runtimeid_table.json");
        if (stream == null) {
            throw new AssertionError("Static RuntimeID table not found");
        }
        CollectionType type = VoxelwindServer.MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, RuntimeEntry.class);
        ArrayList<RuntimeEntry> entries;
        try {
            entries = VoxelwindServer.MAPPER.readValue(stream, type);
        } catch (Exception e) {
            throw new AssertionError("Could not load RuntimeID table");
        }

        cachedPallete = Unpooled.buffer();
        Varints.encodeUnsigned(cachedPallete, entries.size());

        runtimeId2BlockState = new ArrayList<>(entries.size());

        for (RuntimeEntry entry : entries) {
            try {
                BlockType blockType = BlockTypes.byId(entry.id > 255 ? 255 - entry.id : entry.id);
                //Metadata metadata = entry.data == 0 ? null : MetadataSerializer.deserializeMetadata(blockType, (short) entry.data);
                BlockState state = new VoxelwindBlockStateBuilder().blockType(blockType).build();
                registerRuntimeId(state, (entry.id << 4) | entry.data);
            } catch (IllegalArgumentException e) {
                // ignore
            }

            McpeUtil.writeVarintLengthString(cachedPallete, entry.name);
            cachedPallete.writeShortLE(entry.data);
        }
    }

    public Optional<BlockState> getBlockState(int runtimeId) {
        r.lock();
        try {
            return Optional.ofNullable(runtimeId2BlockState.get(runtimeId));
        } finally {
            r.unlock();
        }
    }

    public int getOrCreateRuntimeId(BlockState state) {
        Preconditions.checkNotNull(state, "state");
        int runtimeId;
        r.lock();
        try {
            runtimeId = blockState2RuntimeId.get(state);
        } finally {
            r.unlock();
        }

        if (runtimeId == -1) {
            int id = state.getBlockType().getId();
            short meta = MetadataSerializer.serializeMetadata(state);
            runtimeId = registerRuntimeId(state, (id << 4) | meta);
        }
        return runtimeId;
    }

    public int fromLegacy(int id, byte data) {
        int runtimeId;
        if ((runtimeId = legacyId2RuntimeId.get((id << 4) | data)) == -1) {
            throw new IllegalArgumentException("Unknown legacy id");
        }
        return runtimeId;
    }

    private int registerRuntimeId(BlockState state, int legacyId) {
        if (legacyId2RuntimeId.containsKey(legacyId)) {
            throw new IllegalArgumentException("LegacyID already registered");
        }

        int runtimeId;

        w.lock();
        try {
            runtimeId = runtimeIdAllocator++;
            runtimeId2BlockState.add(state);
            blockState2RuntimeId.put(state, runtimeId);
            legacyId2RuntimeId.put(legacyId, runtimeId);
        } finally {
            w.unlock();
        }
        return runtimeId;
    }

    public ByteBuf getCachedPallete() {
        return cachedPallete.retainedSlice();
    }

    @AllArgsConstructor
    private static class RuntimeEntry {
        private final String name;
        private final int id;
        private final int data;
    }
}