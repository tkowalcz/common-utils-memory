package com.codewise.util.memory;

import sun.nio.ch.DirectBuffer;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static com.codewise.util.lowlevel.MemoryAccess.*;

public class FixedOffHeapNativeByteOrderMutableMemory implements OffHeapMutableMemory {

    private long capacity;
    private long addressOffset;

    protected FixedOffHeapNativeByteOrderMutableMemory(long addressOffset, long capacity) {
        this.addressOffset = addressOffset;
        this.capacity = capacity;
    }

    @Override
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
        return getNativeByteOrderCharUnsafe(null, addressOffset + index);
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        setNativeByteOrderCharUnsafe(null, addressOffset + index, value);
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        return getNativeByteOrderShortUnsafe(null, addressOffset + index);
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        setNativeByteOrderShortUnsafe(null, addressOffset + index, value);
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        return getNativeByteOrderIntUnsafe(null, addressOffset + index);
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        setNativeByteOrderIntUnsafe(null, addressOffset + index, value);
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        return getNativeByteOrderLongUnsafe(null, addressOffset + index);
    }

    @Override
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
        setNativeByteOrderLongUnsafe(null, addressOffset + index, value);
    }

    @Override
    public double getDouble(long index) {
        checkCapacity(index + Double.BYTES);
        return getNativeByteOrderDoubleUnsafe(null, addressOffset + index);
    }

    @Override
    public void putDouble(long index, double value) {
        ensureCapacity(index + Double.BYTES);
        setNativeByteOrderDoubleUnsafe(null, addressOffset + index, value);
    }

    @Override
    public float getFloat(long index) {
        checkCapacity(index + Float.BYTES);
        return getNativeByteOrderFloatUnsafe(null, addressOffset + index);
    }

    @Override
    public void putFloat(long index, float value) {
        ensureCapacity(index + Float.BYTES);
        setNativeByteOrderFloatUnsafe(null, addressOffset + index, value);
    }

    @Override
    public void get(long index, byte[] dst, int offset, int length) {
        if (length > 0) {
            checkCapacity(index + 1);
            length = Math.toIntExact(Math.min(capacity - index, length));
            copyMemoryUnsafe(null, addressOffset + index, dst, ARRAY_BYTE_BASE_OFFSET + offset, length);
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void get(long index, ByteBuffer buf) {
        checkCapacity(index + buf.remaining());
        if (buf.isDirect()) {
            copyMemoryUnsafe(null, addressOffset + index, ((DirectBuffer) buf).address(), buf.position(), buf.remaining());
        } else {
            copyMemoryUnsafe(null, addressOffset + index, buf.array(), ARRAY_BYTE_BASE_OFFSET + buf.position(), buf.remaining());
        }
    }

    @Override
    public void put(long index, byte[] src, int offset, int length) {
        ensureCapacity(index + length);
        copyMemoryUnsafe(src, ARRAY_BYTE_BASE_OFFSET + offset, null, addressOffset + index, length);
    }

    @Override
    public void put(long index, ReadOnlyMemory src, long offset, long length) {
        ensureCapacity(index + length);
        if (src instanceof OffHeapMutableMemory) {
            copyMemoryUnsafe(((OffHeapMutableMemory) src).addressOffset() + offset, addressOffset + index, length);
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
    public <C> void iterateOverMemory(C consumerInstance, StaticMemoryConsumer<C> consumerMethod, long offset, long length, byte[] tempArray) {
        while (length > 0) {
            int toCopy = Math.min((int) Math.min(length, Integer.MAX_VALUE), tempArray.length);
            get(offset, tempArray, 0, toCopy);
            consumerMethod.accept(consumerInstance, tempArray, 0, toCopy);
            length -= toCopy;
            offset += toCopy;
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

    private void checkCapacity(long size) {
        if (RANGE_CHECKS && size > capacity) {
            throw new BufferUnderflowException();
        }
    }

    protected void ensureCapacity(long size) {
        if (RANGE_CHECKS && size > capacity) {
            throw new BufferOverflowException();
        }
    }

    @Override
    public long addressOffset() {
        return addressOffset;
    }
}
