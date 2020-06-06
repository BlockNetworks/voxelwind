package com.voxelwind.nbt.io.util;

import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.util.NetworkDataOutputStream;
import com.voxelwind.nbt.util.SwappedDataOutputStream;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class NBTWriters {
    public static NBTWriter createLittleEndianWriter(OutputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTWriter(new SwappedDataOutputStream(stream));
    }

    public static NBTWriter createBigEndianWriter(OutputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTWriter(new DataOutputStream(stream));
    }

    public static NBTWriter createNetworkWriter(OutputStream stream) {
        Objects.requireNonNull(stream, "stream");
        return new NBTWriter(new NetworkDataOutputStream(stream));
    }
}
