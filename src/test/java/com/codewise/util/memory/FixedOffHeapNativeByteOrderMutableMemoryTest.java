package com.codewise.util.memory;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FixedOffHeapNativeByteOrderMutableMemoryTest extends FixedOffHeapMutableMemoryTestBase<FixedOffHeapNativeByteOrderMutableMemory> {

    @Override
    protected FixedOffHeapNativeByteOrderMutableMemory wrapByteArray(ByteBuffer memoryBuffer) {
        return new FixedOffHeapNativeByteOrderMutableMemory(((DirectBuffer)memoryBuffer).address(), memoryBuffer.capacity());
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }
}