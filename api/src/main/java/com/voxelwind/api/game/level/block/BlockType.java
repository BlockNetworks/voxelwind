package com.voxelwind.api.game.level.block;

import com.voxelwind.api.game.item.ItemStack;
import com.voxelwind.api.game.item.ItemType;
import com.voxelwind.api.server.Server;

import java.util.Collection;
import java.util.Optional;

/**
 * This interface specifies a kind of block.
 */
public interface BlockType extends ItemType {
    default boolean isBlock() {
        return true;
    }

    Optional<BlockData> createBlockDataFor(short metadata);

    boolean isDiggable();
    boolean isTransparent();

    int emitsLight();
    int filtersLight();

    Class<? extends BlockData> getBlockDataClass();
}
