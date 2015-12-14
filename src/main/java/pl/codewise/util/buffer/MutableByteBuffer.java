package pl.codewise.util.buffer;

public interface MutableByteBuffer extends ReadOnlyByteBuffer<MutableByteBuffer> {

    MutableByteBuffer put(byte b);

    MutableByteBuffer put(int index, byte b);

    MutableByteBuffer put(byte[] src, int offset, int length);

    default MutableByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }

    MutableByteBuffer put(ReadOnlyByteBuffer src);

    MutableByteBuffer putChar(char value);

    MutableByteBuffer putChar(int index, char value);

    MutableByteBuffer putShort(short value);

    MutableByteBuffer putShort(int index, short value);

    MutableByteBuffer putInt(int value);

    MutableByteBuffer putInt(int index, int value);

    MutableByteBuffer putLong(long value);

    MutableByteBuffer putLong(int index, long value);

    MutableByteBuffer putDouble(double value);

    MutableByteBuffer putDouble(int index, double value);

    MutableByteBuffer wrap(ByteBufferMemory memory);

    /**
     * Read-only view of this byte buffer - all properties and memory are shared.
     * @return
     */
    default ReadOnlyByteBuffer asReadOnlyBuffer() {
        return this;
    }
}
