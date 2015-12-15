package com.codewise.util.memory;

public class GrowableUnsafeMutableMemoryTest extends GrowableMutableMemoryTestBase<GrowableUnsafeMutableMemory> {

    @Override
    protected GrowableUnsafeMutableMemory allocateBuffer(int size) {
        return new GrowableUnsafeMutableMemory(size);
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
