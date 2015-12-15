package com.codewise.util.memory;

public class FixedUnsafeMutableMemoryTest extends FixedMutableMemoryTestBase<FixedUnsafeMutableMemory> {

    @Override
    protected FixedUnsafeMutableMemory wrapByteArray(byte[] memoryBuffer) {
        return new FixedUnsafeMutableMemory(memoryBuffer);
    }
}
