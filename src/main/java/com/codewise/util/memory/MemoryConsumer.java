package com.codewise.util.memory;

@FunctionalInterface
public interface MemoryConsumer {

    void accept(byte[] memory, int offset, int length);
}
