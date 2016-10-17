package com.codewise.util.memory;

import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

public class FixedOffHeapByteBufferMemoryTest extends FixedOffHeapMutableMemoryTestBase<FixedOffHeapByteBufferMemory> {

    @Override
    protected FixedOffHeapByteBufferMemory wrapByteArray(ByteBuffer memoryBuffer) {
        return new FixedOffHeapByteBufferMemory(((DirectBuffer)memoryBuffer).address(), memoryBuffer.capacity());
    }
}