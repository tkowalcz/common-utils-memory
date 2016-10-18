package com.codewise.util.memory;

public interface MutableMemory extends ReadOnlyMemory {

    void put(long index, byte b);

    void putChar(long index, char value);

    void putShort(long index, short value);

    void putInt(long index, int value);

    void putLong(long index, long value);

    default void putFloat(long index, float value) {
        putInt(index, Float.floatToIntBits(value));
    }

    void putDouble(long index, double value);

    void put(long index, byte[] src, int offset, int length);

    void put(long index, MutableMemory src, long offset, long length);

    default void put(int index, byte b) {
        put((long) index, b);
    }

    default void putChar(int index, char value) {
        putChar((long) index, value);
    }

    default void putShort(int index, short value) {
        putShort((long) index, value);
    }

    default void putInt(int index, int value) {
        putInt((long) index, value);
    }

    default void putLong(int index, long value) {
        putLong((long) index, value);
    }

    default void putDouble(int index, double value) {
        putDouble((long) index, value);
    }

    default void put(int index, byte[] src, int offset, int length) {
        put((long) index, src, offset, length);
    }

    default void put(int index, MutableMemory src, int offset, int length) {
        put((long) index, src, (long) offset, (long) length);
    }
}
