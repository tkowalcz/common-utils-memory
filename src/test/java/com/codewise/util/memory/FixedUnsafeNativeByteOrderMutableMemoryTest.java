package com.codewise.util.memory;

import java.nio.ByteOrder;

public class FixedUnsafeNativeByteOrderMutableMemoryTest extends FixedMutableMemoryTestBase<FixedUnsafeNativeByteOrderMutableMemory> {

    @Override
    protected FixedUnsafeNativeByteOrderMutableMemory wrapByteArray(byte[] memoryBuffer) {
        return new FixedUnsafeNativeByteOrderMutableMemory(memoryBuffer);
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }
}