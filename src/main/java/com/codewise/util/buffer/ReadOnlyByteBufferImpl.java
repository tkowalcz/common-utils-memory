package com.codewise.util.buffer;

import com.codewise.util.memory.MemoryConsumer;
import com.codewise.util.memory.MutableMemory;
import com.codewise.util.memory.ReadOnlyMemory;

import java.nio.ByteBuffer;
import java.util.function.Supplier;

class ReadOnlyByteBufferImpl<B extends ReadOnlyByteBuffer<B>> extends AbstractByteBuffer<B> implements ReadOnlyByteBuffer<B> {

    ReadOnlyByteBufferImpl() {
    }

    ReadOnlyByteBufferImpl(MutableMemory memory) {
        super(memory);
    }

    @Override
    public ReadOnlyByteBuffer<?> wrap(ReadOnlyMemory memory) {
        MutableMemory memoryToWrap = memory instanceof MutableMemory ? (MutableMemory) memory : new ReadOnlyMemoryAdapter(memory);
        wrapMemory(memoryToWrap);
        return this;
    }

    @Override
    public byte get() {
        getRangeCheck(position, Byte.BYTES);
        byte result = memory.get(getOffset());
        position += Byte.BYTES;
        return result;
    }

    @Override
    public byte get(int index) {
        getRangeCheck(index, Byte.BYTES);
        return memory.get(getOffset(index));
    }

    @Override
    public B get(byte[] dst, int offset, int length) {
        getRangeCheck(position, length);
        memory.get(getOffset(), dst, offset, length);
        position += length;
        return (B) ((Object) this);
    }

    @Override
    public B get(ByteBuffer dst) {
        int length = dst.remaining();
        getRangeCheck(position, length);
        memory.get(getOffset(), dst);
        position += length;
        return (B) ((Object) this);
    }

    @Override
    public char getChar() {
        getRangeCheck(position, Character.BYTES);
        char result = memory.getChar(getOffset());
        position += Character.BYTES;
        return result;
    }

    @Override
    public char getChar(int index) {
        getRangeCheck(index, Character.BYTES);
        return memory.getChar(getOffset(index));
    }

    @Override
    public short getShort() {
        getRangeCheck(position, Short.BYTES);
        short result = memory.getShort(getOffset());
        position += Short.BYTES;
        return result;
    }

    @Override
    public short getShort(int index) {
        getRangeCheck(index, Short.BYTES);
        return memory.getShort(getOffset(index));
    }

    @Override
    public int getInt() {
        getRangeCheck(position, Integer.BYTES);
        int result = memory.getInt(getOffset());
        position += Integer.BYTES;
        return result;
    }

    @Override
    public int getInt(int index) {
        getRangeCheck(index, Integer.BYTES);
        return memory.getInt(getOffset(index));
    }

    @Override
    public long getLong() {
        getRangeCheck(position, Long.BYTES);
        long result = memory.getLong(getOffset());
        position += Long.BYTES;
        return result;
    }

    @Override
    public long getLong(int index) {
        getRangeCheck(index, Long.BYTES);
        return memory.getLong(getOffset(index));
    }

    @Override
    public double getDouble() {
        getRangeCheck(position, Double.BYTES);
        double result = memory.getDouble(getOffset());
        position += Double.BYTES;
        return result;
    }

    @Override
    public double getDouble(int index) {
        getRangeCheck(index, Double.BYTES);
        return memory.getDouble(getOffset(index));
    }

    public void get(int index, byte[] dst, int offset, int length) {
        memory.get(index, dst, offset, length);
    }

    @Override
    public void get(int index, ByteBuffer buf) {
        memory.get(index, buf);
    }

    public int compare(int index, ReadOnlyMemory that, int offset, int length) {
        if (that instanceof AbstractByteBuffer) {
            that = ((AbstractByteBuffer) that).memory;
        }
        return memory.compare(index, that, offset, length);
    }

    @Override
    public Supplier<B> uninitializedBufferFactory() {
        //noinspection unchecked
        return () -> (B) new ReadOnlyByteBufferImpl();
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer, int index, int length) {
        if (index + length > capacity()) {
            throw new IllegalArgumentException();
        }

        memory.iterateOverMemory(consumer, getOffset(index), length);
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer) {
        int position = position();
        iterateOverMemory(consumer, position, remaining());
    }

    @Override
    public int hashCode() {
        int h = 1;
        if (initialized) {
            int p = position();
            for (int i = limit() - 1; i >= p; i--) {
                h = 31 * h + (int) get(i);
            }
        }
        return h;
    }
}

class ReadOnlyMemoryAdapter implements MutableMemory {

    private final ReadOnlyMemory delegate;

    public ReadOnlyMemoryAdapter(ReadOnlyMemory delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(int index, byte b) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void putChar(int index, char value) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void putShort(int index, short value) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void putInt(int index, int value) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void putLong(int index, long value) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void putDouble(int index, double value) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void put(int index, byte[] src, int offset, int length) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public void put(int index, MutableMemory src, int offset, int length) {
        throw new UnsupportedOperationException("Backing memory is read-only");
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public byte get(int index) {
        return delegate.get(index);
    }

    @Override
    public char getChar(int index) {
        return delegate.getChar(index);
    }

    @Override
    public short getShort(int index) {
        return delegate.getShort(index);
    }

    @Override
    public int getInt(int index) {
        return delegate.getInt(index);
    }

    @Override
    public long getLong(int index) {
        return delegate.getLong(index);
    }

    @Override
    public double getDouble(int index) {
        return delegate.getDouble(index);
    }

    @Override
    public void get(int index, byte[] dst, int offset, int length) {
        delegate.get(index, dst, offset, length);
    }

    @Override
    public void get(int index, ByteBuffer buf) {
        delegate.get(index, buf);
    }

    @Override
    public int compare(int index, ReadOnlyMemory that, int offset, int length) {
        return delegate.compare(index, that, offset, length);
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer, int offset, int length) {
        delegate.iterateOverMemory(consumer, offset, length);
    }
}