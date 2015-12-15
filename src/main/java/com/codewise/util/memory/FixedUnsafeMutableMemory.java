package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

public class FixedUnsafeMutableMemory extends AbstractMutableMemory implements BytesWrappingCapableMemory {

    protected byte[] memory;

    protected FixedUnsafeMutableMemory(int size) {
        memory = new byte[size];
        capacity = size;
    }

    protected FixedUnsafeMutableMemory(byte[] buffer) {
        memory = buffer;
        capacity = buffer.length;
    }

    @Override
    public void wrap(byte[] bytes) {
        memory = bytes;
        capacity = bytes.length;
    }

    @Override
    public byte get(int index) {
        checkCapacity(index + Byte.BYTES);
        return MemoryAccess.getByteUnsafe(memory, index);
    }

    @Override
    public void put(int index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        MemoryAccess.setByteUnsafe(memory, index, b);
    }

    @Override
    public char getChar(int index) {
        checkCapacity(index + Character.BYTES);
        return MemoryAccess.getCharUnsafe(memory, index);
    }

    @Override
    public void putChar(int index, char value) {
        ensureCapacity(index + Character.BYTES);
        MemoryAccess.setCharUnsafe(memory, index, value);
    }

    @Override
    public short getShort(int index) {
        checkCapacity(index + Short.BYTES);
        return MemoryAccess.getShortUnsafe(memory, index);
    }

    @Override
    public void putShort(int index, short value) {
        ensureCapacity(index + Short.BYTES);
        MemoryAccess.setShortUnsafe(memory, index, value);
    }

    @Override
    public int getInt(int index) {
        checkCapacity(index + Integer.BYTES);
        return MemoryAccess.getIntUnsafe(memory, index);
    }

    @Override
    public void putInt(int index, int value) {
        ensureCapacity(index + Integer.BYTES);
        MemoryAccess.setIntUnsafe(memory, index, value);
    }

    @Override
    public long getLong(int index) {
        checkCapacity(index + Long.BYTES);
        return MemoryAccess.getLongUnsafe(memory, index);
    }

    @Override
    public void putLong(int index, long value) {
        ensureCapacity(index + Long.BYTES);
        MemoryAccess.setLongUnsafe(memory, index, value);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(int offset) {
        return memory;
    }
}
