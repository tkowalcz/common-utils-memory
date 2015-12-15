package com.codewise.util.buffer;

import com.codewise.util.lowlevel.MemoryAccess;
import com.codewise.util.memory.BytesWrappingCapableMemory;
import com.codewise.util.memory.MemoryType;
import com.codewise.util.memory.MutableMemory;

public class Buffers {

    private static final MemoryType DEFAULT_MEMORY_TYPE = MemoryAccess.UNSAFE_MEMORY_ACCESS ? MemoryType.UNSAFE : MemoryType.SAFE;

    public static MutableByteBuffer allocate(MemoryType memoryType, int size) {
        return new MutableByteBufferImpl(memoryType.allocateFixedMemory(size));
    }

    public static MutableByteBuffer allocateGrowable(MemoryType memoryType, int size) {
        return new MutableByteBufferImpl(memoryType.allocateGrowableMemory(size));
    }

    public static MutableByteBuffer allocatePaged(MemoryType memoryType) {
        return new MutableByteBufferImpl(memoryType.allocatePagedMemory());
    }

    public static MutableByteBuffer allocatePaged(MemoryType memoryType, int size) {
        return new MutableByteBufferImpl(memoryType.allocatePagedMemory(size));
    }

    public static MutableByteBuffer allocatePaged(MemoryType memoryType, int pageCntGrow, int pageSizeBits) {
        return new MutableByteBufferImpl(memoryType.allocatePagedMemory(pageCntGrow, pageSizeBits, 0));
    }

    public static MutableByteBuffer allocatePaged(MemoryType memoryType, int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new MutableByteBufferImpl(memoryType.allocatePagedMemory(pageCntGrow, pageSizeBits, initialCapacity));
    }

    public static MutableByteBuffer allocate(int size) {
        return allocate(DEFAULT_MEMORY_TYPE, size);
    }

    public static MutableByteBuffer allocateGrowable(int size) {
        return allocateGrowable(DEFAULT_MEMORY_TYPE, size);
    }

    public static MutableByteBuffer allocatePaged() {
        return allocatePaged(DEFAULT_MEMORY_TYPE);
    }

    public static MutableByteBuffer allocatePaged(int size) {
        return allocatePaged(DEFAULT_MEMORY_TYPE, size);
    }

    public static MutableByteBuffer allocatePaged(int pageCntGrow, int pageSizeBits) {
        return allocatePaged(DEFAULT_MEMORY_TYPE, pageCntGrow, pageSizeBits);
    }

    public static MutableByteBuffer allocatePaged(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return allocatePaged(DEFAULT_MEMORY_TYPE, pageCntGrow, pageSizeBits, initialCapacity);
    }

    public static MutableByteBuffer wrap(MutableMemory memory) {
        return new MutableByteBufferImpl(memory);
    }

    public static MutableByteBuffer wrap(MemoryType memoryType, byte[] bytes) {
        MutableMemory memory = memoryType.getByteArrayWrappingFactory().orElseThrow(IllegalArgumentException::new).apply(bytes);
        return wrap(memory);
    }

    public static MutableByteBuffer wrap(byte[] bytes) {
        return wrap(DEFAULT_MEMORY_TYPE, bytes);
    }

    public static MutableByteBuffer wrapUsing(MutableByteBuffer buffer, byte[] bytes) {
        if (buffer instanceof MutableByteBufferImpl) {
            MutableByteBufferImpl bufImpl = (MutableByteBufferImpl) buffer;
            MutableMemory memory = bufImpl.memory;
            if (memory instanceof BytesWrappingCapableMemory) {
                ((BytesWrappingCapableMemory) memory).wrap(bytes);
                return buffer;
            }
        }
        throw new IllegalArgumentException();
    }

    public static MutableByteBuffer wrap(MemoryType memoryType, byte[] bytes, int offset, int size) {
        if (offset < 0 || offset > bytes.length) {
            throw new IllegalArgumentException();
        }
        if (size < 0 || offset + size > bytes.length) {
            throw new IllegalArgumentException();
        }

        MutableMemory memory = memoryType.getByteArrayWrappingFactory().orElseThrow(IllegalArgumentException::new).apply(bytes);

        MutableByteBuffer result = new MutableByteBufferImpl(memory);
        result.limit(offset + size);
        result.position(offset);
        return result;
    }

    public static MutableByteBuffer wrap(byte[] bytes, int offset, int size) {
        return wrap(DEFAULT_MEMORY_TYPE, bytes, offset, size);
    }

    public static MutableByteBuffer wrapUsing(MutableByteBuffer buffer, byte[] bytes, int offset, int size) {
        if (offset < 0 || offset > bytes.length) {
            throw new IllegalArgumentException();
        }
        if (size < 0 || offset + size > bytes.length) {
            throw new IllegalArgumentException();
        }

        if (buffer instanceof MutableByteBufferImpl) {
            MutableByteBufferImpl bufImpl = (MutableByteBufferImpl) buffer;
            MutableMemory memory = bufImpl.memory;
            if (memory instanceof BytesWrappingCapableMemory) {
                ((BytesWrappingCapableMemory) memory).wrap(bytes);
                buffer.limit(offset + size);
                buffer.position(offset);
                return buffer;
            }
        }
        throw new IllegalArgumentException();
    }

    public static MutableByteBuffer newMutable() {
        return new MutableByteBufferImpl();
    }

    public static byte[] toArray(ReadOnlyByteBuffer<?> buffer) {
        byte[] result = new byte[buffer.capacity()];
        if (result.length > 0) {
            int pos = buffer.position();
            int limit = buffer.limit();
            buffer.clear().get(result).position(pos).limit(limit);
        }
        return result;
    }

    public static int sumRemaining(ReadOnlyByteBuffer... bufs) {
        int remaining = 0;
        for (ReadOnlyByteBuffer buf : bufs) {
            remaining += buf.remaining();
        }
        return remaining;
    }
}
