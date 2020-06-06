package com.voxelwind.nbt.tags;

import java.util.Arrays;
import java.util.Objects;

public class LongArrayTag implements Tag<long[]> {
    private final String name;
    private final long[] value;

    public LongArrayTag(String name, long[] value) {
        this.name = name;
        this.value = Objects.requireNonNull(value, "value").clone();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long[] getValue() {
        return value.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongArrayTag that = (LongArrayTag) o;
        return Objects.equals(name, that.name) &&
                Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        String append = "";
        if (name != null && !name.equals("")) {
            append = "(\"" + this.getName() + "\")";
        }

        return "TAG_Long_Array" + append + ": [" + value.length + " bytes]";
    }
}
