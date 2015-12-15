package com.codewise.util.memory;

public class FixedSafeMutableMemoryTest extends FixedMutableMemoryTestBase<FixedSafeMutableMemory> {

    @Override
    protected FixedSafeMutableMemory wrapByteArray(byte[] memoryBuffer) {
        return new FixedSafeMutableMemory(memoryBuffer);
    }
}
