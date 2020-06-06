package com.voxelwind.nbt.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NetworkDataInputStream extends SwappedDataInputStream {

    public NetworkDataInputStream(InputStream stream) {
        super(stream);
    }

    @Override
    public int readInt() throws IOException {
        return Varints.decodeSigned(stream);
    }

    @Override
    public long readLong() throws IOException {
        return Varints.decodeUnsigned(stream);
    }

    @Override
    public String readUTF() throws IOException {
        int length = (int) Varints.decodeUnsigned(stream);
        byte[] bytes = new byte[length];
        readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
