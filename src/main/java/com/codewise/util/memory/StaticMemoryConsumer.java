package com.codewise.util.memory;

@FunctionalInterface
public interface StaticMemoryConsumer<C> {

    void accept(C consumerInstance, byte[] memory, int offset, int length);
}
