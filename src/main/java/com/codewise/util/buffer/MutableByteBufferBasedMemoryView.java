package com.codewise.util.buffer;

import com.codewise.util.memory.MutableMemory;
import com.codewise.util.memory.ReadOnlyMemory;
import com.codewise.util.memory.StaticMemoryConsumer;

import java.nio.ByteBuffer;

class MutableByteBufferBasedMemoryView implements MutableMemory {
    private final MutableByteBuffer delegate;

    public MutableByteBufferBasedMemoryView(MutableByteBuffer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(long index, byte b) {
        delegate.put(index, b);
    }

    @Override
    public void putChar(long index, char value) {
        delegate.putChar(index, value);
    }

    @Override
    public void putShort(long index, short value) {
        delegate.putShort(index, value);
    }

    @Override
    public void putInt(long index, int value) {
        delegate.putInt(index, value);
    }

    @Override
    public void putLong(long index, long value) {
        delegate.putLong(index, value);
    }

    @Override
    public void putDouble(long index, double value) {
        putDouble(index, value);
    }

    @Override
    public void put(long index, byte[] src, int offset, int length) {
        long position = delegate.position();
        long limit = delegate.limit();
        delegate.resetAtPosition(index).put(src, offset, length).position(position).limit(limit);
    }

    @Override
    public void put(long index, MutableMemory src, long offset, long length) {
        long position = delegate.position();
        long limit = delegate.limit();
        delegate.resetAtPosition(index);
        src.iterateOverMemory(delegate::put, offset, length);
        delegate.position(position).limit(limit);
    }

    @Override
    public long capacity() {
        return delegate.remaining();
    }

    @Override
    public byte get(long index) {
        return delegate.get(index);
    }

    @Override
    public char getChar(long index) {
        return delegate.getChar(index);
    }

    @Override
    public short getShort(long index) {
        return delegate.getShort(index);
    }

    @Override
    public int getInt(long index) {
        return delegate.getInt(index);
    }

    @Override
    public long getLong(long index) {
        return delegate.getLong(index);
    }

    @Override
    public double getDouble(long index) {
        return delegate.getDouble(index);
    }

    @Override
    public void get(long index, byte[] dst, int offset, int length) {
        delegate.get(index, dst, offset, length);
    }

    @Override
    public void get(long index, ByteBuffer buf) {
        delegate.get(index, buf);
    }

    @Override
    public int compare(long index, ReadOnlyMemory that, long offset, long length) {
        return delegate.compare(index, that, offset, length);
    }

    @Override
    public <C> void iterateOverMemory(C consumerInstance, StaticMemoryConsumer<C> consumerMethod, long offset, long length) {
        delegate.iterateOverMemory(consumerInstance, consumerMethod, offset, length);
    }

    @Override
    public <C> void iterateOverMemory(C consumerInstance, StaticMemoryConsumer<C> consumerMethod, long offset, long length, byte[] tempArray) {
        delegate.iterateOverMemory(consumerInstance, consumerMethod, offset, length, tempArray);
    }
}
