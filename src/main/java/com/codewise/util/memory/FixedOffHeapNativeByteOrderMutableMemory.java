package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;
import sun.nio.ch.DirectBuffer;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static com.codewise.util.lowlevel.MemoryAccess.ARRAY_BYTE_BASE_OFFSET;

public class FixedOffHeapNativeByteOrderMutableMemory implements OffHeapMutableMemory {

    private long addressOffset;
    private long capacity;

    protected FixedOffHeapNativeByteOrderMutableMemory(long addressOffset, long capacity) {
        this.addressOffset = addressOffset;
        this.capacity = capacity;
    }

    public void wrap(long addressOffset, long capacity) {
        this.addressOffset = addressOffset;
        this.capacity = capacity;
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public byte get(long index) {
        checkCapacity(index + Byte.BYTES);
        return MemoryAccess.getByteUnsafe(null, addressOffset + index);
    }

    @Override
    public void put(long index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        MemoryAccess.setByteUnsafe(null, addressOffset + index, b);
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        return MemoryAccess.getNativeByteOrderCharUnsafe(null, addressOffset + index);
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        MemoryAccess.setNativeByteOrderCharUnsafe(null, addressOffset + index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return MemoryAccess.getNativeByteOrderShortUnsafe(null, addressOffset + index);
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        MemoryAccess.setNativeByteOrderShortUnsafe(null, addressOffset + index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return MemoryAccess.getNativeByteOrderIntUnsafe(null, addressOffset + index);
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        MemoryAccess.setNativeByteOrderIntUnsafe(null, addressOffset + index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return MemoryAccess.getNativeByteOrderLongUnsafe(null, addressOffset + index);
    }

    @Override
    public double getDouble(long index) {
        checkCapacity(index + Double.BYTES);
        return MemoryAccess.getNativeByteOrderDoubleUnsafe(null, addressOffset + index);
    }

    @Override
    public float getFloat(long index) {
        checkCapacity(index + Float.BYTES);
        return MemoryAccess.getNativeByteOrderFloatUnsafe(null, addressOffset + index);
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
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
        MemoryAccess.setNativeByteOrderLongUnsafe(null, addressOffset + index, value);
    }

    @Override
    public void putDouble(long index, double value) {
        ensureCapacity(index + Double.BYTES);
        MemoryAccess.setNativeByteOrderDoubleUnsafe(null, addressOffset + index, value);
    }

    @Override
    public void putFloat(long index, float value) {
        ensureCapacity(index + Float.BYTES);
        MemoryAccess.setNativeByteOrderFloatUnsafe(null, addressOffset + index, value);
    }

    @Override
    public void put(long index, byte[] src, int offset, int length) {
        ensureCapacity(index + length);
        MemoryAccess.copyMemoryUnsafe(src, ARRAY_BYTE_BASE_OFFSET + offset, null, addressOffset + index, length);
    }

    @Override
    public void put(long index, MutableMemory src, long offset, long length) {
        ensureCapacity(index + length);
        if (src instanceof OffHeapMutableMemory) {
            MemoryAccess.copyMemoryUnsafe(((OffHeapMutableMemory) src).addressOffset() + offset, addressOffset + index, length);
        } else {
            src.iterateOverMemory(new MemoryConsumer() {
                long localIndex = index;
                @Override
                public void accept(byte[] memory, int offset, int length) {
                    put(localIndex, memory, offset, length);
                    localIndex += length;
                }
            }, offset, length);
        }
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

    protected void checkCapacity(long size) {
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
