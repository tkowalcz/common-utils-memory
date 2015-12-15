package com.codewise.util.memory;

import java.nio.ByteOrder;

public class GrowableUnsafeNativeByteOrderMutableMemoryTest extends GrowableMutableMemoryTestBase<GrowableUnsafeNativeByteOrderMutableMemory> {

    @Override
    protected GrowableUnsafeNativeByteOrderMutableMemory allocateBuffer(int size) {
        return new GrowableUnsafeNativeByteOrderMutableMemory(size);
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }

    @Override
    protected byte getByteDirect(int idx) {
        return memory.memory[idx];
    }

    @Override
    protected void putByteDirect(int idx, byte b) {
        memory.memory[idx] = b;
    }
}