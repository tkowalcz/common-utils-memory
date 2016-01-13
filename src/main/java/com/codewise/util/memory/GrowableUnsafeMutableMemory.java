package com.codewise.util.memory;

public class GrowableUnsafeMutableMemory extends FixedUnsafeMutableMemory {

    public static final int CAPACITY_INCREMENT_GRANULARITY = 0x100;

    protected GrowableUnsafeMutableMemory(int size) {
        super(size);
    }

    protected GrowableUnsafeMutableMemory(byte[] buffer) {
        super(buffer);
    }

    @Override
    protected void ensureCapacity(long size) {
        if (size > capacity) {
            int newCapacity = Math.toIntExact( ((size - 1) | (CAPACITY_INCREMENT_GRANULARITY - 1)) + 1L);
            byte[] newMemory = new byte[newCapacity];
            System.arraycopy(memory, 0, newMemory, 0, (int) capacity);
            memory = newMemory;
            capacity = newCapacity;
        }
    }
}
