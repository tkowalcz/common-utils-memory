package pl.codewise.util.buffer;

import static pl.codewise.util.buffer.GrowableUnsafeByteBufferMemory.CAPACITY_INCREMENT_GRANULARITY;

class GrowableSafeByteBufferMemory extends FixedSafeByteBufferMemory {

    GrowableSafeByteBufferMemory(int size) {
        super(size);
    }

    GrowableSafeByteBufferMemory(byte[] buffer) {
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
