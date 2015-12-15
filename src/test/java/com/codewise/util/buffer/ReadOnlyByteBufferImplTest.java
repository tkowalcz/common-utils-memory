package com.codewise.util.buffer;

import com.codewise.util.memory.MutableMemory;
import org.testng.annotations.Test;

@Test
public class ReadOnlyByteBufferImplTest extends ReadOnlyByteBufferTestBase<ReadOnlyByteBufferImplTest.BufferImpl> {

    @Override
    protected BufferImpl bufferUninitialized() {
        return BufferImpl.uninitialized();
    }

    @Override
    protected BufferImpl bufferForMemory(MutableMemory memory) {
        return BufferImpl.forMemory(memory);
    }

    @Override
    protected BufferImpl bufferForMemoryAndBaseOffset(MutableMemory memory, int baseOffset) {
        return BufferImpl.forMemoryAndBaseOffset(memory, baseOffset);
    }

    @Override
    protected Class<?> bufferClass() {
        return ReadOnlyByteBufferImpl.class;
    }

    public static class BufferImpl extends ReadOnlyByteBufferImpl<BufferImpl> {
        public BufferImpl() {
        }

        public BufferImpl(MutableMemory memory) {
            super(memory);
        }

        public static BufferImpl uninitialized() {
            return new BufferImpl();
        }

        public static BufferImpl forMemory(MutableMemory memory) {
            return new BufferImpl(memory);
        }

        public static BufferImpl forMemoryAndBaseOffset(MutableMemory memory, int baseOffset) {
            return new BufferImpl(memory).withBaseOffset(baseOffset);
        }
    }
}

