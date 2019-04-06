package com.voxelwind.server.game.level.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IntBitSet {
    private int bitset;

    public IntBitSet() {
        bitset = 0;
    }

    public IntBitSet(int bitset) {
        this.bitset = (byte) bitset;
    }

    public IntBitSet(IntBitSet bitSet) {
        this.bitset = bitSet.bitset;
    }

    public void flip(int index) {
        bitset = BitUtil.flipBit(bitset, index);
    }

    public void set(int index, boolean value) {
        bitset = BitUtil.setBit(bitset, index, value);
    }

    public void set(int bitset) {
        this.bitset = bitset;
    }

    public boolean get(int index) {
        return BitUtil.getBit(bitset, index);
    }

    public long[] getLongs() {
        return new long[]{bitset};
    }

    public int[] getInts() {
        return new int[]{bitset};
    }

    public short[] getShorts() {
        ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(bitset);
        return buffer.asShortBuffer().array();
    }

    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(bitset);
        return buffer.array();
    }

    public void clear() {
        bitset = 0;
    }

    public int get() {
        return bitset;
    }

    private static void checkIndex(int index) {
        if (!(index >= 0 && index < 32)) {
            throw new IndexOutOfBoundsException("Expected value 0-32");
        }
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof IntBitSet)) return false;
        IntBitSet that = (IntBitSet) o;
        return this.bitset == that.get();
    }

    public int hashCode() {
        return bitset;
    }

    public String toString() {
        return "IntBitSet(" +
                "bitset=" + Integer.toUnsignedString(bitset) +
                ")";
    }
}