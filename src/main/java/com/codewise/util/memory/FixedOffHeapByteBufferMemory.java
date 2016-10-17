package com.codewise.util.memory;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.codewise.util.lowlevel.MemoryAccess;
import sun.nio.ch.DirectBuffer;

import static com.codewise.util.lowlevel.MemoryAccess.*;
import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;

class FixedOffHeapByteBufferMemory implements OffHeapMutableMemory {

    private long capacity;
    private long addressOffset;

    public FixedOffHeapByteBufferMemory(long address, long capacity) {
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
        ensureCapacity(index + Byte.BYTES);
        setByteUnsafe(null, addressOffset + index, b);
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        return getCharUnsafe(null, addressOffset + index);
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        setCharUnsafe(null, addressOffset + index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return getShortUnsafe(null, addressOffset + index);
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        setShortUnsafe(null, addressOffset + index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return getIntUnsafe(null, addressOffset + index);
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        setIntUnsafe(null, addressOffset + index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return getLongUnsafe(null, addressOffset + index);
    }

    @Override
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
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
        if (length > 0) {
            checkCapacity(index + 1);
            length = Math.toIntExact(Math.min(capacity - index, length));
            MemoryAccess.copyMemoryUnsafe(null, addressOffset + index, dst, ARRAY_BYTE_BASE_OFFSET + offset, length);
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void get(long index, ByteBuffer buf) {
        checkCapacity(index + buf.remaining());
        if (buf.isDirect()) {
            MemoryAccess.copyMemoryUnsafe(null, addressOffset + index, ((DirectBuffer)buf).address(), buf.position(), buf.remaining());
        } else {
            MemoryAccess.copyMemoryUnsafe(null, addressOffset + index, buf.array(), ARRAY_BYTE_BASE_OFFSET + buf.position(), buf.remaining());
        }
    }

    @Override
    public void put(long index, byte[] src, int offset, int length) {
        if (length > 0) {
            ensureCapacity(index + length);
            copyMemoryUnsafe(null, addressOffset + index, src, ARRAY_BYTE_BASE_OFFSET + offset, length);
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void put(long index, MutableMemory src, long offset, long length) {
        checkCapacity(index + length);

        long i = 0;
        for (; i < length; i += 8) {
            putLong(index + i, src.getLong(offset + i));
        }

        for (; i < offset + length; i++) {
            put(index + i, src.get(offset + i));
        }
    }

    @Override
    public int compare(long index, ReadOnlyMemory that, long offset, long length) {
        int diff = 0;
        while (diff == 0 && length > Long.BYTES) {
            diff = Long.compareUnsigned(getLong(index), that.getLong(offset));
            length -= Long.BYTES;
            offset += Long.BYTES;
            index += Long.BYTES;
        }
        while (diff == 0 && length > Integer.BYTES) {
            diff = Integer.compareUnsigned(getInt(index), that.getInt(offset));
            length -= Integer.BYTES;
            offset += Integer.BYTES;
            index += Integer.BYTES;
        }
        while (diff == 0 && length > Character.BYTES) {
            diff = Character.compare(getChar(index), that.getChar(offset));
            length -= Character.BYTES;
            offset += Character.BYTES;
            index += Character.BYTES;
        }
        if (diff == 0 && length == 1) {
            diff = Byte.compare(get(index), that.get(offset));
        }
        return diff;
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer, long offset, long length, byte[] tempArray) {
        while (length > 0) {
            int toCopy = Math.min((int) Math.min(length, Integer.MAX_VALUE), tempArray.length);
            get(offset, tempArray, 0, toCopy);
            consumer.accept(tempArray, 0, toCopy);
            length -= toCopy;
            offset += toCopy;
        }
    }

    private void checkCapacity(long size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferUnderflowException();
        }
    }

    protected void ensureCapacity(long size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferOverflowException();
        }
    }

    @Override
    public long addressOffset() {
        return addressOffset;
    }
}
