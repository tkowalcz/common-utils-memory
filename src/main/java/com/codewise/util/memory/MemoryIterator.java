package com.codewise.util.memory;

@FunctionalInterface
public interface MemoryIterator {

    void iterateOverMemory(MemoryConsumer consumer, long offset, long length);

    default void iterateOverMemory(MemoryConsumer consumer, int offset, int length) {
        iterateOverMemory(consumer, (long) offset, (long) length);
    }
}
