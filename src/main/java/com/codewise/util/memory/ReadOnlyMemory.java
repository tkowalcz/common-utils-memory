package com.codewise.util.memory;

import java.nio.ByteBuffer;

public interface ReadOnlyMemory extends MemoryIterator {

    int capacity();

    byte get(int index);

    char getChar(int index);

    short getShort(int index);

    int getInt(int index);

    long getLong(int index);

    double getDouble(int index);

    void get(int index, byte[] dst, int offset, int length);

    void get(int index, ByteBuffer buf);

    int compare(int index, ReadOnlyMemory that, int offset, int length);
}
