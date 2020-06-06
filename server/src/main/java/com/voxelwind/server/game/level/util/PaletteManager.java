package com.voxelwind.server.game.level.util;

import com.fasterxml.jackson.core.type.TypeReference;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.voxelwind.api.game.level.block.BlockState;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.util.NBTReaders;
import com.voxelwind.nbt.tags.*;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.game.level.block.VoxelwindBlockStateBuilder;
import com.voxelwind.server.game.serializer.MetadataSerializer;
import com.voxelwind.server.network.mcpe.packets.McpeStartGame;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class PaletteManager {
    private static PaletteManager instance;

    public static final List<McpeStartGame.ItemEntry> ITEM_PALETTE = new ArrayList<>();
    public static ListTag<CompoundTag> BLOCK_PALETTE;

    private static AtomicInteger runtimeIdAllocator = new AtomicInteger();

    private final BiMap<String, Integer> idLegacyMap = HashBiMap.create();
    private final Map<Integer, Integer> legacyRuntimeMap = new HashMap<>();
    private final BiMap<Integer, BlockState> runtimeStateMap = HashBiMap.create();

    static {
        loadBlocks();
        loadItems();

        new PaletteManager().registerVanillaPalette();
    }

    public PaletteManager() {
        instance = this;
    }

    public static PaletteManager get() {
        return instance;
    }

    public Optional<BlockState> getBlockState(int runtimeId) {
        return Optional.ofNullable(runtimeStateMap.get(runtimeId));
    }

    public int getOrCreateRuntimeId(BlockState state) {
        int id = state.getBlockType().getId();
        short meta = MetadataSerializer.serializeMetadata(state);

        int fullId = getFullId(id, meta);
        if(!legacyRuntimeMap.containsKey(fullId)) {
            //log.info("Doesnt contain key: " + fullId + " - " + id);
            return 0;
        }
        int runtimeId = this.legacyRuntimeMap.get(fullId);

        if (runtimeId == -1) {
            throw new RuntimeException("No runtime ID for block " + id + ":" + meta);
        }
        return runtimeId;
    }

    private static int getFullId(int id, int meta) {
        return (id << 8) | (meta & 0xFF);
    }

    public int fromLegacy(int id, byte data) {
        int runtimeId;
        if ((runtimeId = legacyRuntimeMap.get(getFullId(id, data))) == -1) {
            throw new IllegalArgumentException("Unknown legacy id");
        }
        return runtimeId;
    }

    private synchronized int registerBlock(int legacyId, int meta) {
        int runtimeId = runtimeIdAllocator.getAndIncrement();

        BlockType blockType = BlockTypes.forId(legacyId > 255 ? 255 - legacyId : legacyId);
        BlockState block = new VoxelwindBlockStateBuilder().blockType(blockType).build();

        this.runtimeStateMap.put(runtimeId, block);
        this.legacyRuntimeMap.put(getFullId(legacyId, meta), runtimeId);
        return runtimeId;
    }

    private void registerVanillaPalette() {
        for (CompoundTag entry : BLOCK_PALETTE.getValue()) {
            String name = ((CompoundTag) entry.get("block")).get("name").getValue().toString();
            int legacyId = ((ShortTag) entry.get("id")).getPrimitiveValue();
            this.idLegacyMap.putIfAbsent(name, legacyId);

            if (!entry.contains("meta")) {
                runtimeIdAllocator.getAndIncrement();
                continue;
            }
            int[] meta = ((IntArrayTag) entry.get("meta")).getValue();

            int runtimeId = this.registerBlock(legacyId, meta[0]);

            for (int i = 1; i < meta.length; i++) {
                this.legacyRuntimeMap.put(getFullId(legacyId, meta[i]), runtimeId);
            }
        }
    }

    private static void loadBlocks() {
        InputStream stream = VoxelwindServer.class.getClassLoader().getResourceAsStream("runtime_block_states.dat");
        if(stream == null) {
            throw new RuntimeException("Static runtime block state table not found");
        }

        try (NBTReader nbtInputStream = NBTReaders.createLittleEndianReader(stream)) {
            // noinspection unchecked
            BLOCK_PALETTE = (ListTag<CompoundTag>) nbtInputStream.readTag();
        } catch (Exception e) {
            throw new RuntimeException("Error loading blocks: " + e);
        }
    }

    private static void loadItems() {
        InputStream stream = VoxelwindServer.class.getClassLoader().getResourceAsStream("runtime_item_states.json");
        if (stream == null) {
            throw new AssertionError("Static item state table not found");
        }

        List<McpeStartGame.ItemEntry> entries;
        try {
            entries = VoxelwindServer.MAPPER.readValue(stream, new TypeReference<List<McpeStartGame.ItemEntry>>(){});
        } catch (IOException e) {
            throw new RuntimeException("Error lodaing items: " + e);
        }

        entries.forEach(entry -> ITEM_PALETTE.add(new McpeStartGame.ItemEntry(entry.getName(), entry.getId())));
    }
}