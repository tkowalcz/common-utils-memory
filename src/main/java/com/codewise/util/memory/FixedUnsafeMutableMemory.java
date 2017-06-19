package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

import static com.codewise.util.lowlevel.MemoryAccess.ARRAY_BYTE_BASE_OFFSET;

public class FixedUnsafeMutableMemory extends AbstractMutableMemory implements BytesWrappingCapableMemory {

    protected byte[] memory;

    protected FixedUnsafeMutableMemory(long size) {
        memory = new byte[Math.toIntExact(size)];
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
    public byte get(long index) {
        checkCapacity(index + Byte.BYTES);
        return MemoryAccess.getByteUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void put(long index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        MemoryAccess.setByteUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, b);
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        return MemoryAccess.getCharUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        MemoryAccess.setCharUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return MemoryAccess.getShortUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        MemoryAccess.setShortUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return MemoryAccess.getIntUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        MemoryAccess.setIntUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return MemoryAccess.getLongUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
        MemoryAccess.setLongUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(long offset) {
        return memory;
    }
}
