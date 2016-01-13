package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

import static com.codewise.util.lowlevel.MemoryAccess.ARRAY_BYTE_BASE_OFFSET;

public class FixedUnsafeNativeByteOrderMutableMemory extends AbstractMutableMemory implements BytesWrappingCapableMemory {

    protected byte[] memory;

    protected FixedUnsafeNativeByteOrderMutableMemory(int size) {
        memory = new byte[size];
        capacity = size;
    }

    protected FixedUnsafeNativeByteOrderMutableMemory(byte[] buffer) {
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
        return MemoryAccess.getNativeByteOrderCharUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        MemoryAccess.setNativeByteOrderCharUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return MemoryAccess.getNativeByteOrderShortUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        MemoryAccess.setNativeByteOrderShortUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return MemoryAccess.getNativeByteOrderIntUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        MemoryAccess.setNativeByteOrderIntUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return MemoryAccess.getNativeByteOrderLongUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    @Override
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
        MemoryAccess.setNativeByteOrderLongUnsafe(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(long offset) {
        return memory;
    }
}
