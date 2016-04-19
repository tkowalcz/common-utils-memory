package com.codewise.util.buffer;

import com.codewise.util.memory.MutableMemory;

public interface MutableByteBuffer extends ReadOnlyByteBuffer<MutableByteBuffer> {

    MutableByteBuffer put(byte b);

    MutableByteBuffer put(long index, byte b);

    default MutableByteBuffer put(int index, byte b) {
        return put((long) index, b);
    }

    MutableByteBuffer put(byte[] src, int offset, int length);

    default MutableByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    MutableByteBuffer put(ReadOnlyByteBuffer src);

    MutableByteBuffer putChar(char value);

    MutableByteBuffer putChar(long index, char value);

    default MutableByteBuffer putChar(int index, char value) {
        return putChar((long) index, value);
    }

    MutableByteBuffer putShort(short value);

    MutableByteBuffer putShort(long index, short value);

    default MutableByteBuffer putShort(int index, short value) {
        return putShort((long) index, value);
    }

    MutableByteBuffer putInt(int value);

    MutableByteBuffer putInt(long index, int value);

    default MutableByteBuffer putInt(int index, int value) {
        return putInt((long) index, value);
    }

    MutableByteBuffer putLong(long value);

    MutableByteBuffer putLong(long index, long value);

    default MutableByteBuffer putLong(int index, long value) {
        return putLong((long) index, value);
    }

    MutableByteBuffer putDouble(double value);

    MutableByteBuffer putDouble(long index, double value);

    default MutableByteBuffer putDouble(int index, double value) {
        return putDouble((long) index, value);
    }

    MutableByteBuffer wrap(MutableMemory memory);
    
    default MutableMemory getMemory() {
        return new MutableByteBufferBasedMemoryView(this);
    }

    /**
     * Read-only view of this byte buffer - all properties and memory are shared.
     *
     * @return
     */
    default ReadOnlyByteBuffer asReadOnlyBuffer() {
        return this;
    }
}
