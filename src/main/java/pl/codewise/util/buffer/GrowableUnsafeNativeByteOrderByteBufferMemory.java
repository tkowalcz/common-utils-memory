package pl.codewise.util.buffer;

public class GrowableUnsafeNativeByteOrderByteBufferMemory extends FixedUnsafeNativeByteOrderByteBufferMemory {

    public static final int CAPACITY_INCREMENT_GRANULARITY = 0x100;

    public GrowableUnsafeNativeByteOrderByteBufferMemory(int size) {
        super(size);
    }

    public GrowableUnsafeNativeByteOrderByteBufferMemory(byte[] buffer) {
        super(buffer);
    }

    @Override
    protected void ensureCapacity(int size) {
        if (size > capacity) {
            int newCapacity = ((size - 1) | (CAPACITY_INCREMENT_GRANULARITY - 1)) + 1;
            byte[] newMemory = new byte[newCapacity];
            System.arraycopy(memory, 0, newMemory, 0, capacity);
            memory = newMemory;
            capacity = newCapacity;
        }
    }
}
