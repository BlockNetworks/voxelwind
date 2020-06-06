package com.voxelwind.server.network.mcpe.packets;

import com.voxelwind.nbt.io.NBTReader;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.io.util.NBTReaders;
import com.voxelwind.nbt.io.util.NBTWriters;
import com.voxelwind.nbt.tags.CompoundTag;
import com.voxelwind.server.VoxelwindServer;
import com.voxelwind.server.network.NetworkPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.io.InputStream;

public class McpeBiomeDefinitionList implements NetworkPackage {
    private static CompoundTag nbt;

    static {
        InputStream stream = VoxelwindServer.class.getClassLoader().getResourceAsStream("biome_definitions.dat");
        if (stream == null) {
            throw new AssertionError("Biome data table not found");
        }

        try (NBTReader nbtStream = NBTReaders.createNetworkReader(stream)) {
            nbt = (CompoundTag) nbtStream.readTag();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        try (NBTWriter stream = NBTWriters.createNetworkWriter(new ByteBufOutputStream(buffer))) {
            stream.write(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
