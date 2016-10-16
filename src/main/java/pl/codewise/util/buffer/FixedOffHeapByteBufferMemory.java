package pl.codewise.util.buffer;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;
import static pl.codewise.util.buffer.MemoryAccess.*;

class FixedOffHeapByteBufferMemory implements ByteBufferMemory {

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
    public int capacity() {
        return (int) capacity;
    }

    @Override
    public byte get(int index) {
        checkCapacity(index + Byte.BYTES);
        return getByteUnsafe(null, addressOffset + index);
    }

    @Override
    public void put(int index, byte b) {
        checkCapacity(index + Byte.BYTES);
        setByteUnsafe(null, addressOffset + index, b);
    }

    @Override
    public char getChar(int index) {
        checkCapacity(index + Character.BYTES);
        return getCharUnsafe(null, addressOffset + index);
    }

    @Override
    public void putChar(int index, char value) {
        checkCapacity(index + Character.BYTES);
        MemoryAccess.setCharUnsafe(null, addressOffset + index, value);
    }

    @Override
    public short getShort(int index) {
        checkCapacity(index + Short.BYTES);
        return getShortUnsafe(null, addressOffset + index);
    }

    @Override
    public void putShort(int index, short value) {
        checkCapacity(index + Short.BYTES);
        setShortUnsafe(null, addressOffset + index, value);
    }

    @Override
    public int getInt(int index) {
        checkCapacity(index + Integer.BYTES);
        return getIntUnsafe(null, addressOffset + index);
    }

    @Override
    public void putInt(int index, int value) {
        checkCapacity(index + Integer.BYTES);
        setIntUnsafe(null, addressOffset + index, value);
    }

    @Override
    public long getLong(int index) {
        checkCapacity(index + Long.BYTES);
        return getLongUnsafe(null, addressOffset + index);
    }

    @Override
    public void putLong(int index, long value) {
        checkCapacity(index + Long.BYTES);
        setLongUnsafe(null, addressOffset + index, value);
    }

    @Override
    public double getDouble(int index) {
        return longBitsToDouble(getLong(index));
    }

    @Override
    public void putDouble(int index, double value) {
        putLong(index, doubleToRawLongBits(value));
    }

    @Override
    public void get(int index, byte[] dst, int offset, int length) {
        checkCapacity(index + length);
        copyUnsafeToBytes(addressOffset, index, dst, offset, length);
    }

    @Override
    public void get(int index, ByteBuffer buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(int index, byte[] src, int offset, int length) {
        if (length > 0) {
            checkCapacity(index + length);
            copyBytesToUnsafe(addressOffset, index, src, offset, length);
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void put(int index, ByteBufferMemory src, int offset, int length) {
        throw new UnsupportedOperationException();

    }

    @Override
    public int compare(int index, ReadOnlyMemory src, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer, int offset, int length) {
        throw new UnsupportedOperationException();
    }

    private void checkCapacity(long size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferUnderflowException();
        }
    }
}
