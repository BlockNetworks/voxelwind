package com.voxelwind.nbt.util;

import javax.annotation.Nonnull;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class NetworkDataOutputStream extends SwappedDataOutputStream {
    public NetworkDataOutputStream(OutputStream stream) {
        super(stream);
    }

    public NetworkDataOutputStream(DataOutputStream stream) {
        super(stream);
    }

    public void writeInt(int value) throws IOException {
        Varints.encodeUnsigned(stream, value);
    }

    public void writeLong(long value) throws IOException {
        Varints.encodeUnsigned(this.stream, value);
    }

    public void writeUTF(@Nonnull String string) throws IOException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        Varints.encodeUnsigned(stream, bytes.length);
        write(bytes);
    }
}