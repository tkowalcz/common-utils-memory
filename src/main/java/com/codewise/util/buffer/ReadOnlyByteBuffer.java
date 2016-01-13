package com.codewise.util.buffer;

import com.codewise.util.memory.MemoryConsumer;
import com.codewise.util.memory.ReadOnlyMemory;

import java.nio.ByteBuffer;

public interface ReadOnlyByteBuffer<B extends ReadOnlyByteBuffer<B>> extends ByteBufferBase<B>, ReadOnlyMemory {

    byte get();

    byte get(long index);

    default byte get(int index) {
        return get((long) index);
    }

    B get(byte[] dst, int offset, int length);

    default B get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    B get(ByteBuffer dst);

    char getChar();

    char getChar(long index);

    default char getChar(int index) {
        return getChar((long) index);
    }

    short getShort();

    short getShort(long index);

    default short getShort(int index) {
        return getShort((long) index);
    }

    int getInt();

    int getInt(long index);

    default int getInt(int index) {
        return getInt((long) index);
    }

    long getLong();

    long getLong(long index);

    default long getLong(int index) {
        return getLong((long) index);
    }

    double getDouble();

    double getDouble(long index);

    default double getDouble(int index) {
        return getDouble((long) index);
    }

    /**
     * Iterate over buffer backing memory from current buffer position to it's current limit.
     * Does not change buffer position.
     *
     * @param consumer buffer memory chunks consumer
     */
    void iterateOverMemory(MemoryConsumer consumer);

    /**
     * Iterate over buffer backing memory from given index, spanning given length of bytes.
     *
     * @param consumer buffer memory chunks consumer
     * @param index    index of first byte of interest
     * @param length   number of bytes of interest
     */
    void iterateOverMemory(MemoryConsumer consumer, long index, long length);

    ReadOnlyByteBuffer<?> wrap(ReadOnlyMemory memory);

    default B duplicate() {
        return (B) this.uninitializedBufferFactory().get().duplicateOf(this);
    }

    B sliceMe();

    default byte[] array() {
        return Buffers.toArray(this);
    }
}
