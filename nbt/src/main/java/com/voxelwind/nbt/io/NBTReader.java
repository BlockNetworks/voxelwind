package com.voxelwind.nbt.io;

import com.voxelwind.nbt.tags.*;
import com.voxelwind.nbt.util.Varints;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.util.*;

public class NBTReader implements Closeable {
    private final DataInput input;
    private boolean closed = false;

    public NBTReader(DataInput input) {
        this(input, NBTEncoding.NOTCHIAN);
    }

    public NBTReader(DataInput input, NBTEncoding encoding) {
        this.input = Objects.requireNonNull(input, "input");
    }

    public Tag<?> readTag() throws IOException {
        return readTag(0);
    }

    private Tag<?> readTag(int depth) throws IOException {
        if (closed) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        }
        int typeId = input.readUnsignedByte();
        TagType type = TagType.fromId(typeId);
        if (type == null) {
            throw new IOException("Invalid encoding ID " + typeId);
        }

        return deserialize(type, false, depth);
    }

    @SuppressWarnings("unchecked")
    private Tag<?> deserialize(TagType type, boolean skipName, int depth) throws IOException {
        if (depth > 16) {
            throw new IllegalArgumentException("NBT compound is too deeply nested");
        }

        String tagName = "";
        if (type != TagType.END && !skipName) {
            tagName = input.readUTF();
        }

        switch (type) {
            case END:
                if (depth == 0) {
                    throw new IllegalArgumentException("Found a TAG_End in root tag!");
                }
                return EndTag.INSTANCE;
            case BYTE:
                byte b = input.readByte();
                return new ByteTag(tagName, b);
            case SHORT:
                short sh = input.readShort();
                return new ShortTag(tagName, sh);
            case INT:
                return new IntTag(tagName, input.readInt());
            case LONG:
                return new LongTag(tagName, input.readLong());
            case FLOAT:
                return new FloatTag(tagName, input.readFloat());
            case DOUBLE:
                return new DoubleTag(tagName, input.readDouble());
            case BYTE_ARRAY:
                int arraySz1 = input.readInt();
                byte[] valueBytesBa = new byte[arraySz1];
                input.readFully(valueBytesBa);
                return new ByteArrayTag(tagName, valueBytesBa);
            case STRING:
                return new StringTag(tagName, input.readUTF());
            case COMPOUND:
                Map<String, Tag<?>> map = new HashMap<>();
                Tag<?> inTag1;
                while ((inTag1 = readTag(depth + 1)) != EndTag.INSTANCE) {
                    map.put(inTag1.getName(), inTag1);
                }
                return new CompoundTag(tagName, map);
            case LIST:
                int inId = input.readUnsignedByte();
                TagType listType = TagType.fromId(inId);
                if (listType == null) {
                    throw new IllegalArgumentException("Found invalid type in TAG_List('" + tagName + "'): " + inId);
                }
                List<Tag<?>> list = new ArrayList<>();
                int listLength = input.readInt();
                for (int i = 0; i < listLength; i++) {
                    list.add(deserialize(listType, true, depth + 1));
                }
                // Unchecked cast is expected
                return new ListTag(tagName, listType.getTagClass(), list);
            case INT_ARRAY:
                int arraySz2 = input.readInt();
                int[] valueBytesInt = new int[arraySz2];
                for (int i = 0; i < arraySz2; i++) {
                    valueBytesInt[i] = input.readInt();
                }
                return new IntArrayTag(tagName, valueBytesInt);
            case LONG_ARRAY:
                int arraySz3 = input.readInt();
                long[] valueBytesLong = new long[arraySz3];
                for (int i = 0; i < arraySz3; i++) {
                    valueBytesLong[i] = input.readLong();
                }
                return new LongArrayTag(tagName, valueBytesLong);
        }

        throw new IllegalArgumentException("Unknown type " + type);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (input instanceof Closeable) {
            ((Closeable) input).close();
        }
    }
}
