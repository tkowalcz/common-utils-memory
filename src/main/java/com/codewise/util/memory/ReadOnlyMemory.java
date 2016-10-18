package com.codewise.util.memory;

import java.nio.ByteBuffer;

public interface ReadOnlyMemory extends MemoryIterator {

    long capacity();

    byte get(long index);

    char getChar(long index);

    short getShort(long index);

    int getInt(long index);

    long getLong(long index);

    double getDouble(long index);

    default float getFloat(long index) {
        return Float.intBitsToFloat(getInt(index));
    }

    void get(long index, byte[] dst, int offset, int length);

    void get(long index, ByteBuffer buf);

    int compare(long index, ReadOnlyMemory that, long offset, long length);

    default byte get(int index) {
        return get((long) index);
    }

    default char getChar(int index) {
        return getChar((long) index);
    }

    default short getShort(int index) {
        return getShort((long) index);
    }

    default int getInt(int index) {
        return getInt((long) index);
    }

    default long getLong(int index) {
        return getLong((long) index);
    }

    default double getDouble(int index) {
        return getDouble((long) index);
    }

    default void get(int index, byte[] dst, int offset, int length) {
        get((long) index, dst, offset, length);
    }

    default void get(int index, ByteBuffer buf) {
        get((long) index, buf);
    }

    default int compare(int index, ReadOnlyMemory that, int offset, int length) {
        return compare((long) index, that, (long) offset, (long) length);
    }
}
