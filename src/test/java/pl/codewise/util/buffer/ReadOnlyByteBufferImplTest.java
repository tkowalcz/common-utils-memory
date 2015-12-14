package pl.codewise.util.buffer;

import org.testng.annotations.Test;

@Test
public class ReadOnlyByteBufferImplTest extends ReadOnlyByteBufferTestBase<ReadOnlyByteBufferImplTest.BufferImpl> {

    @Override
    protected BufferImpl bufferUninitialized() {
        return BufferImpl.uninitialized();
    }

    @Override
    protected BufferImpl bufferForMemory(ByteBufferMemory memory) {
        return BufferImpl.forMemory(memory);
    }

    @Override
    protected BufferImpl bufferForMemoryAndBaseOffset(ByteBufferMemory memory, int baseOffset) {
        return BufferImpl.forMemoryAndBaseOffset(memory, baseOffset);
    }

    @Override
    protected Class<?> bufferClass() {
        return ReadOnlyByteBufferImpl.class;
    }

    public static class BufferImpl extends ReadOnlyByteBufferImpl<BufferImpl> {
        public BufferImpl() {
        }

        public BufferImpl(ByteBufferMemory memory) {
            super(memory);
        }

        public static BufferImpl uninitialized() {
            return new BufferImpl();
        }

        public static BufferImpl forMemory(ByteBufferMemory memory) {
            return new BufferImpl(memory);
        }

        public static BufferImpl forMemoryAndBaseOffset(ByteBufferMemory memory, int baseOffset) {
            return new BufferImpl(memory).withBaseOffset(baseOffset);
        }
    }
}

