package com.codewise.util.memory;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.codewise.util.lowlevel.MemoryAccess;

import static com.codewise.util.lowlevel.MemoryAccess.*;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;

class FixedOffHeapByteBufferMemory implements MutableMemory {

    private long capacity;
    private long addressOffset;

    FixedOffHeapByteBufferMemory(long address, long capacity) {
        this.addressOffset = address;
        this.capacity = capacity;
    }

    public void wrap(long address, long capacity) {
        this.addressOffset = address;
        this.capacity = capacity;
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public byte get(long index) {
        checkCapacity(index + Byte.BYTES);
        return getByteUnsafe(null, addressOffset + index);
    }

    @Override
    public void put(long index, byte b) {
        checkCapacity(index + Byte.BYTES);
        setByteUnsafe(null, addressOffset + index, b);
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        return getCharUnsafe(null, addressOffset + index);
    }

    @Override
    public void putChar(long index, char value) {
        checkCapacity(index + Character.BYTES);
        setCharUnsafe(null, addressOffset + index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return getShortUnsafe(null, addressOffset + index);
    }

    @Override
    public void putShort(long index, short value) {
        checkCapacity(index + Short.BYTES);
        setShortUnsafe(null, addressOffset + index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return getIntUnsafe(null, addressOffset + index);
    }

    @Override
    public void putInt(long index, int value) {
        checkCapacity(index + Integer.BYTES);
        setIntUnsafe(null, addressOffset + index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return getLongUnsafe(null, addressOffset + index);
    }

    @Override
    public void putLong(long index, long value) {
        checkCapacity(index + Long.BYTES);
        setLongUnsafe(null, addressOffset + index, value);
    }

    @Override
    public double getDouble(long index) {
        return longBitsToDouble(getLong(index));
    }

    @Override
    public void putDouble(long index, double value) {
        putLong(index, doubleToRawLongBits(value));
    }

    @Override
    public void get(long index, byte[] dst, int offset, int length) {
        checkCapacity(index + length);
        copyMemoryUnsafe(addressOffset, index, dst, offset, length);
    }

    @Override
    public void get(long index, ByteBuffer buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(long index, byte[] src, int offset, int length) {
        if (length > 0) {
            checkCapacity(index + length);
            copyMemoryUnsafe(addressOffset, index, src, offset, length);
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void put(long index, MutableMemory src, long offset, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compare(long index, ReadOnlyMemory src, long offset, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer, long offset, long length) {
        throw new UnsupportedOperationException();
    }

    private void checkCapacity(long size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferUnderflowException();
        }
    }
}
