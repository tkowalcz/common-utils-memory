package com.codewise.util.memory;

@FunctionalInterface
public interface MemoryIterator {

    /**
     * Allows for invocations like
     * <code>
     * class MyClass {
     *     void method1() {
     *          ...
     *          memory.iterateOverMemory(this, MyClass::memConsumer, pos, length);
     *          ...
     *     }
     *
     *     void memConsumer(byte[] bytes, int offset, int length) {
     *         ...
     *     }
     * }
     * </code>
     * which won't create new object for every invocation of iterateOverMemory()
     * @param consumerInstance
     * @param consumerMethod
     * @param offset
     * @param length
     * @param <C>
     */
    <C> void iterateOverMemory(C consumerInstance, StaticMemoryConsumer<C> consumerMethod, long offset, long length);

    default <C> void iterateOverMemory(C consumerInstance, StaticMemoryConsumer<C> consumerMethod, long offset, long length, byte[] tempArray) {
        iterateOverMemory(consumerInstance, consumerMethod, offset, length);
    }

    @Deprecated
    default void iterateOverMemory(MemoryConsumer consumer, long offset, long length, byte[] tempArray) {
        iterateOverMemory(consumer, MemoryConsumer::accept, offset, length, tempArray);
    }

    @Deprecated
    default void iterateOverMemory(MemoryConsumer consumer, long offset, long length) {
        iterateOverMemory(consumer, MemoryConsumer::accept, offset, length);
    }

    @Deprecated
    default void iterateOverMemory(MemoryConsumer consumer, int offset, int length) {
        iterateOverMemory(consumer, MemoryConsumer::accept, (long) offset, (long) length);
    }
}
