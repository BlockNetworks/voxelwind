package com.voxelwind.server.game.level.block;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.voxelwind.api.game.level.block.BlockType;
import com.voxelwind.api.game.level.block.BlockTypes;
import com.voxelwind.server.game.level.block.behaviors.*;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BlockBehaviors {
    private static final Map<BlockType, BlockBehavior> SPECIAL_BEHAVIORS;

    static {
        SPECIAL_BEHAVIORS = ImmutableMap.<BlockType, BlockBehavior>builder()
                .put(BlockTypes.DIRT, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.GRASS_BLOCK, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.GRASS_PATH, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.MYCELIUM, DirtBlockBehavior.INSTANCE)
                .put(BlockTypes.FARMLAND, FarmlandBlockBehavior.INSTANCE)
                .put(BlockTypes.STONE, DroppableBySpecificToolsBlockBehavior.ALL_PICKAXES)
                .put(BlockTypes.COAL_ORE, DroppableBySpecificToolsBlockBehavior.ALL_PICKAXES)
                .put(BlockTypes.COAL_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_PICKAXES)
                .put(BlockTypes.IRON_ORE, DroppableBySpecificToolsBlockBehavior.ALL_STONE_PICKAXES)
                .put(BlockTypes.IRON_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_STONE_PICKAXES)
                .put(BlockTypes.GOLD_ORE, DroppableBySpecificToolsBlockBehavior.ALL_IRON_PICKAXES)
                .put(BlockTypes.GOLD_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_IRON_PICKAXES)
                .put(BlockTypes.LAPIS_LAZULI_ORE, LapisLazuliOreBlockBehavior.INSTANCE)
                .put(BlockTypes.LAPIS_LAZULI_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_STONE_PICKAXES)
                .put(BlockTypes.DIAMOND_ORE, DiamondOreBlockBehavior.INSTANCE)
                .put(BlockTypes.DIAMOND_BLOCK, DroppableBySpecificToolsBlockBehavior.ALL_IRON_PICKAXES)
                .put(BlockTypes.LEAVES, DroppableBySpecificToolsBlockBehavior.SHEARS_ONLY) // TODO: Handle this better.
                .put(BlockTypes.COBWEB, CobwebBlockBehavior.INSTANCE)
                .build();
    }

    public static BlockBehavior getBlockBehavior(BlockType type) {
        Preconditions.checkNotNull(type, "type");
        BlockBehavior behavior = SPECIAL_BEHAVIORS.get(type);
        if (behavior == null) {
            return SimpleBlockBehavior.INSTANCE;
        }
        return behavior;
    }
}
