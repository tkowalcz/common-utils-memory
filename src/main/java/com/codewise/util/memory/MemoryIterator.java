package com.codewise.util.memory;

@FunctionalInterface
public interface MemoryIterator {

    void iterateOverMemory(MemoryConsumer consumer, int offset, int length);
}
