package pl.codewise.util.buffer;

public class FixedUnsafeNativeByteOrderByteBufferMemory extends AbstractByteBufferMemory implements BytesWrappingCapableMemory {

    protected byte[] memory;

    public FixedUnsafeNativeByteOrderByteBufferMemory(int size) {
        memory = new byte[size];
        capacity = size;
    }

    public FixedUnsafeNativeByteOrderByteBufferMemory(byte[] buffer) {
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
        return MemoryAccess.getByteUnsafe(memory, index);
    }

    @Override
    public void put(int index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        MemoryAccess.setByteUnsafe(memory, index, b);
    }

    @Override
    public char getChar(int index) {
        checkCapacity(index + Character.BYTES);
        return MemoryAccess.getNativeByteOrderCharUnsafe(memory, index);
    }

    @Override
    public void putChar(int index, char value) {
        ensureCapacity(index + Character.BYTES);
        MemoryAccess.setNativeByteOrderCharUnsafe(memory, index, value);
    }

    @Override
    public short getShort(int index) {
        checkCapacity(index + Short.BYTES);
        return MemoryAccess.getNativeByteOrderShortUnsafe(memory, index);
    }

    @Override
    public void putShort(int index, short value) {
        ensureCapacity(index + Short.BYTES);
        MemoryAccess.setNativeByteOrderShortUnsafe(memory, index, value);
    }

    @Override
    public int getInt(int index) {
        checkCapacity(index + Integer.BYTES);
        return MemoryAccess.getNativeByteOrderIntUnsafe(memory, index);
    }

    @Override
    public void putInt(int index, int value) {
        ensureCapacity(index + Integer.BYTES);
        MemoryAccess.setNativeByteOrderIntUnsafe(memory, index, value);
    }

    @Override
    public long getLong(int index) {
        checkCapacity(index + Long.BYTES);
        return MemoryAccess.getNativeByteOrderLongUnsafe(memory, index);
    }

    @Override
    public void putLong(int index, long value) {
        ensureCapacity(index + Long.BYTES);
        MemoryAccess.setNativeByteOrderLongUnsafe(memory, index, value);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(int offset) {
        return memory;
    }
}
