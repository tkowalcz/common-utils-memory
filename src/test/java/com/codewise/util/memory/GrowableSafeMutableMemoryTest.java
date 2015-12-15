package com.codewise.util.memory;

public class GrowableSafeMutableMemoryTest extends GrowableMutableMemoryTestBase<GrowableSafeMutableMemory> {

    @Override
    protected GrowableSafeMutableMemory allocateBuffer(int size) {
        return new GrowableSafeMutableMemory(size);
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