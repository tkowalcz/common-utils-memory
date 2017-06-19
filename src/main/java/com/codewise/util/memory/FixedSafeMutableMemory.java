package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

public class FixedSafeMutableMemory extends AbstractMutableMemory implements BytesWrappingCapableMemory {

    protected byte[] memory;

    protected FixedSafeMutableMemory(long size) {
        memory = new byte[Math.toIntExact(size)];
        capacity = size;
    }

    protected FixedSafeMutableMemory(byte[] buffer) {
        memory = buffer;
        capacity = buffer.length;
    }

    @Override
    public void wrap(byte[] bytes) {
        memory = bytes;
        capacity = bytes.length;
    }

    @Override
    public byte get(long index) {
        checkCapacity(index + Byte.BYTES);
        return memory[(int) index];
    }

    @Override
    public void put(long index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        memory[(int) index] = b;
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        return MemoryAccess.getCharSafe(memory, (int) index);
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        MemoryAccess.setCharSafe(memory, (int) index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return MemoryAccess.getShortSafe(memory, (int) index);
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        MemoryAccess.setShortSafe(memory, (int) index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return MemoryAccess.getIntSafe(memory, (int) index);
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        MemoryAccess.setIntSafe(memory, (int) index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return MemoryAccess.getLongSafe(memory, (int) index);
    }

    @Override
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
        MemoryAccess.setLongSafe(memory, (int) index, value);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(long offset) {
        return memory;
    }
}
