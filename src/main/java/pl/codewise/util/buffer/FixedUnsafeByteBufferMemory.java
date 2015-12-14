package pl.codewise.util.buffer;

import static pl.codewise.util.buffer.MemoryAccess.*;

class FixedUnsafeByteBufferMemory extends AbstractByteBufferMemory implements BytesWrappingCapableMemory {

    protected byte[] memory;

    FixedUnsafeByteBufferMemory(int size) {
        memory = new byte[size];
        capacity = size;
    }

    FixedUnsafeByteBufferMemory(byte[] buffer) {
        memory = buffer;
        capacity = buffer.length;
    }

    @Override
    public void wrap(byte[] bytes) {
        memory = bytes;
        capacity = bytes.length;
    }

    @Override
    public byte get(int index) {
        checkCapacity(index + Byte.BYTES);
        return getByteUnsafe(memory, index);
    }

    @Override
    public void put(int index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        setByteUnsafe(memory, index, b);
    }

    @Override
    public char getChar(int index) {
        checkCapacity(index + Character.BYTES);
        return getCharUnsafe(memory, index);
    }

    @Override
    public void putChar(int index, char value) {
        ensureCapacity(index + Character.BYTES);
        setCharUnsafe(memory, index, value);
    }

    @Override
    public short getShort(int index) {
        checkCapacity(index + Short.BYTES);
        return getShortUnsafe(memory, index);
    }

    @Override
    public void putShort(int index, short value) {
        ensureCapacity(index + Short.BYTES);
        setShortUnsafe(memory, index, value);
    }

    @Override
    public int getInt(int index) {
        checkCapacity(index + Integer.BYTES);
        return getIntUnsafe(memory, index);
    }

    @Override
    public void putInt(int index, int value) {
        ensureCapacity(index + Integer.BYTES);
        setIntUnsafe(memory, index, value);
    }

    @Override
    public long getLong(int index) {
        checkCapacity(index + Long.BYTES);
        return getLongUnsafe(memory, index);
    }

    @Override
    public void putLong(int index, long value) {
        ensureCapacity(index + Long.BYTES);
        setLongUnsafe(memory, index, value);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(int offset) {
        return memory;
    }
}
