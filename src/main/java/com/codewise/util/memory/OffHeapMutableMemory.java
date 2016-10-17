package com.codewise.util.memory;

interface OffHeapMutableMemory extends MutableMemory {

    int TEMP_BUFFER_MAX_SIZE = Integer.getInteger("com.codewise.offHeapBufferTempArraySize", 4096);

    long addressOffset();

    default void iterateOverMemory(MemoryConsumer consumer, long offset, long length) {
        byte[] tempArray = new byte[Math.min((int) Math.min(length, Integer.MAX_VALUE), TEMP_BUFFER_MAX_SIZE)];
        iterateOverMemory(consumer, offset, length, tempArray);
    }
}