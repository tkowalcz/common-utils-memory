package pl.codewise.util.buffer;

public interface ByteBufferMemory extends ReadOnlyMemory {

    void put(int index, byte b);

    void putChar(int index, char value);

    void putShort(int index, short value);

    void putInt(int index, int value);

    void putLong(int index, long value);

    void putDouble(int index, double value);

    void put(int index, byte[] src, int offset, int length);

    void put(int index, ByteBufferMemory src, int offset, int length);
}
