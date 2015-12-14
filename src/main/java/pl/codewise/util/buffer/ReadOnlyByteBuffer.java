package pl.codewise.util.buffer;

import java.nio.ByteBuffer;

public interface ReadOnlyByteBuffer<B extends ReadOnlyByteBuffer<B>> extends ByteBufferBase<B>, ReadOnlyMemory {

    byte get();

    byte get(int index);

    B get(byte[] dst, int offset, int length);

    default B get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    B get(ByteBuffer dst);

    char getChar();

    char getChar(int index);

    short getShort();

    short getShort(int index);

    int getInt();

    int getInt(int index);

    long getLong();

    long getLong(int index);

    double getDouble();

    double getDouble(int index);

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
     * @param index index of first byte of interest
     * @param length number of bytes of interest
     */
    void iterateOverMemory(MemoryConsumer consumer, int index, int length);

    ReadOnlyByteBuffer<?> wrap(ReadOnlyMemory memory);

    default B duplicate() {
        return (B) this.uninitializedBufferFactory().get().duplicateOf(this);
    }

    B sliceMe();

    default byte[] array() {
        return Buffers.toArray(this);
    }
}
