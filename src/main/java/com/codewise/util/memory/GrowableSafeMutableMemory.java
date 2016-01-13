package com.codewise.util.memory;

public class GrowableSafeMutableMemory extends FixedSafeMutableMemory {

    protected GrowableSafeMutableMemory(int size) {
        super(size);
    }

    protected GrowableSafeMutableMemory(byte[] buffer) {
        super(buffer);
    }

    @Override
    protected void ensureCapacity(long size) {
        if (size > capacity) {
            int newCapacity = Math.toIntExact(((size - 1) | (GrowableUnsafeMutableMemory.CAPACITY_INCREMENT_GRANULARITY - 1)) + 1L);
            byte[] newMemory = new byte[newCapacity];
            System.arraycopy(memory, 0, newMemory, 0, (int) capacity);
            memory = newMemory;
            capacity = newCapacity;
        }
    }
}
