package com.codewise.util.memory;

public class GrowableSafeMutableMemory extends FixedSafeMutableMemory {

    protected GrowableSafeMutableMemory(int size) {
        super(size);
    }

    protected GrowableSafeMutableMemory(byte[] buffer) {
        super(buffer);
    }

    @Override
    protected void ensureCapacity(int size) {
        if (size > capacity) {
            int newCapacity = ((size - 1) | (GrowableUnsafeMutableMemory.CAPACITY_INCREMENT_GRANULARITY - 1)) + 1;
            byte[] newMemory = new byte[newCapacity];
            System.arraycopy(memory, 0, newMemory, 0, capacity);
            memory = newMemory;
            capacity = newCapacity;
        }
    }
}
