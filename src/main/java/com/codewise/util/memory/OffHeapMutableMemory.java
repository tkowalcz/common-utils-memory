package com.codewise.util.memory;

public interface OffHeapMutableMemory extends MutableMemory {

    int TEMP_BUFFER_MAX_SIZE = Integer.getInteger("com.codewise.offHeapBufferTempArraySize", 4096);
    boolean THREAD_LOCAL_TEMP_BUFFER = Boolean.getBoolean("com.codewise.ThreadLocalTempArray");
    ThreadLocal<byte[]> threadLocalTempBuffer = ThreadLocal.withInitial(() -> new byte[TEMP_BUFFER_MAX_SIZE]);

    long addressOffset();

    void wrap(long address, long capacity);

    default void iterateOverMemory(MemoryConsumer consumer, long offset, long length) {
        byte[] tempArray = THREAD_LOCAL_TEMP_BUFFER ? threadLocalTempBuffer.get() : new byte[Math.min((int) Math.min(length, Integer.MAX_VALUE), TEMP_BUFFER_MAX_SIZE)];
        iterateOverMemory(consumer, offset, length, tempArray);
    }
}