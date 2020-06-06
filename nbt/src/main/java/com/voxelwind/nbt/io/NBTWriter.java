package com.voxelwind.nbt.io;

import com.voxelwind.nbt.tags.*;
import com.voxelwind.nbt.util.Varints;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NBTWriter implements Closeable {
    private static final int MAXIMUM_DEPTH = 16;
    private final DataOutput output;
    private boolean closed = false;

    public NBTWriter(DataOutput output) {
        this(output, NBTEncoding.NOTCHIAN);
    }

    public NBTWriter(DataOutput output, NBTEncoding encoding) {
        this.output = Objects.requireNonNull(output, "output");
    }

    public void write(Tag<?> tag) throws IOException {
        if (closed) {
            throw new IllegalStateException("closed");
        }
        Objects.requireNonNull(tag, "tag");
        serialize(tag, false, 0);
    }

    private void serialize(Tag<?> tag, boolean skipHeader, int depth) throws IOException {
        if (depth >= MAXIMUM_DEPTH) {
            throw new IllegalArgumentException("Reached depth limit");
        }
        TagType type = TagType.fromClass(tag.getClass());
        if (type == null) {
            throw new IllegalArgumentException("Tag " + tag + " is not valid.");
        }

        if (!skipHeader) {
            output.writeByte(type.ordinal() & 0xFF);
            output.writeUTF(tag.getName());
        }

        switch (type) {
            case END:
                break;
            case BYTE:
                ByteTag bt = (ByteTag) tag;
                output.writeByte(bt.getPrimitiveValue());
                break;
            case SHORT:
                ShortTag st = (ShortTag) tag;
                output.writeShort(st.getPrimitiveValue());
                break;
            case INT:
                IntTag it = (IntTag) tag;
                output.writeInt(it.getPrimitiveValue());
                break;
            case LONG:
                LongTag lt = (LongTag) tag;
                output.writeLong(lt.getPrimitiveValue());
                break;
            case FLOAT:
                FloatTag ft = (FloatTag) tag;
                output.writeFloat(ft.getPrimitiveValue());
                break;
            case DOUBLE:
                DoubleTag dt = (DoubleTag) tag;
                output.writeDouble(dt.getPrimitiveValue());
                break;
            case BYTE_ARRAY:
                ByteArrayTag bat = (ByteArrayTag) tag;
                byte[] bValue = bat.getValue();
                output.writeInt(bValue.length);
                output.write(bValue);
                break;
            case STRING:
                StringTag strt = (StringTag) tag;
                output.writeUTF(strt.getValue());
                break;
            case LIST:
                ListTag<?> listt = (ListTag<?>) tag;
                output.writeByte(TagType.fromClass(listt.getTagClass()).ordinal());
                output.writeInt(listt.getValue().size());
                for (Tag<?> tag1 : listt.getValue()) {
                    serialize(tag1, true, depth+1);
                }
                break;
            case COMPOUND:
                CompoundTag compoundTag = (CompoundTag) tag;
                for (Tag<?> tag1 : compoundTag.getValue().values()) {
                    serialize(tag1, false, depth+1);
                }
                output.writeByte(0);
                break;
            case INT_ARRAY:
                IntArrayTag iat = (IntArrayTag) tag;
                int[] iValue = iat.getValue();
                output.writeInt(iValue.length);
                for (int i : iValue) {
                    output.writeInt(i);
                }
                break;
            case LONG_ARRAY:
                LongArrayTag lat = (LongArrayTag) tag;
                long[] lValue = lat.getValue();
                output.writeLong(lValue.length);
                for (long l : lValue) {
                    output.writeLong(l);
                }
                break;
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (output instanceof Closeable) {
            ((Closeable) output).close();
        }
    }
}
