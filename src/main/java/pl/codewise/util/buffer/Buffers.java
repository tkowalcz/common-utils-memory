package pl.codewise.util.buffer;

import java.nio.BufferOverflowException;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class Buffers {

    private static final IntFunction<ByteBufferMemory> FACTORY_FIXED_MEMORY_OF_SIZE;
    private static final Function<byte[], ByteBufferMemory> FACTORY_FIXED_MEMORY_WRAPPING_BYTES;
    private static final IntFunction<ByteBufferMemory> FACTORY_GROWABLE_MEMORY_OF_SIZE;
    private static final Supplier<ByteBufferMemory> FACTORY_PAGED_MEMORY;
    private static final IntFunction<ByteBufferMemory> FACTORY_PAGED_MEMORY_OF_SIZE;
    private static final IntTriFunction<ByteBufferMemory> FACTORY_PAGED_MEMORY_FACTORY_WITH_ALL_PARAMS;

    static {
        if (MemoryAccess.UNSAFE_MEMORY_ACCESS) {
            FACTORY_FIXED_MEMORY_OF_SIZE = FixedUnsafeByteBufferMemory::new;
            FACTORY_FIXED_MEMORY_WRAPPING_BYTES = FixedUnsafeByteBufferMemory::new;
            FACTORY_GROWABLE_MEMORY_OF_SIZE = GrowableUnsafeByteBufferMemory::new;
            FACTORY_PAGED_MEMORY = PagedUnsafeByteBufferMemory::new;
            FACTORY_PAGED_MEMORY_OF_SIZE = PagedUnsafeByteBufferMemory::new;
            FACTORY_PAGED_MEMORY_FACTORY_WITH_ALL_PARAMS = PagedUnsafeByteBufferMemory::new;
        } else {
            FACTORY_FIXED_MEMORY_OF_SIZE = FixedSafeByteBufferMemory::new;
            FACTORY_FIXED_MEMORY_WRAPPING_BYTES = FixedSafeByteBufferMemory::new;
            FACTORY_GROWABLE_MEMORY_OF_SIZE = GrowableSafeByteBufferMemory::new;
            FACTORY_PAGED_MEMORY = PagedSafeByteBufferMemory::new;
            FACTORY_PAGED_MEMORY_OF_SIZE = PagedSafeByteBufferMemory::new;
            FACTORY_PAGED_MEMORY_FACTORY_WITH_ALL_PARAMS = PagedSafeByteBufferMemory::new;
        }
    }

    // ------------------------------
    // -- memory
    // --
    public static ByteBufferMemory allocateFixedMemory(int size) {
        return FACTORY_FIXED_MEMORY_OF_SIZE.apply(size);
    }

    public static ByteBufferMemory allocateGrowableMemory(int size) {
        return FACTORY_GROWABLE_MEMORY_OF_SIZE.apply(size);
    }

    public static ByteBufferMemory allocatePagedMemory() {
        return FACTORY_PAGED_MEMORY.get();
    }

    public static ByteBufferMemory allocatePagedMemory(int size) {
        return FACTORY_PAGED_MEMORY_OF_SIZE.apply(size);
    }

    public static ByteBufferMemory allocatePagedMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return FACTORY_PAGED_MEMORY_FACTORY_WITH_ALL_PARAMS.apply(pageCntGrow, pageSizeBits, initialCapacity);
    }

    // ------------------------------
    // -- buffers
    // --
    public static MutableByteBuffer allocate(int size) {
        return new MutableByteBufferImpl(allocateFixedMemory(size));
    }

    public static MutableByteBuffer allocateGrowable(int size) {
        return new MutableByteBufferImpl(allocateGrowableMemory(size));
    }

    public static MutableByteBuffer allocatePaged() {
        return new MutableByteBufferImpl(allocatePagedMemory());
    }

    public static MutableByteBuffer allocatePaged(int size) {
        return new MutableByteBufferImpl(allocatePagedMemory(size));
    }

    public static MutableByteBuffer allocatePaged(int pageCntGrow, int pageSizeBits) {
        return new MutableByteBufferImpl(allocatePagedMemory(pageCntGrow, pageSizeBits, 0));
    }

    public static MutableByteBuffer allocatePaged(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new MutableByteBufferImpl(allocatePagedMemory(pageCntGrow, pageSizeBits, initialCapacity));
    }

    public static MutableByteBuffer wrap(ByteBufferMemory memory) {
        return new MutableByteBufferImpl(memory);
    }

    public static MutableByteBuffer wrap(byte[] bytes) {
        return new MutableByteBufferImpl(FACTORY_FIXED_MEMORY_WRAPPING_BYTES.apply(bytes));
    }

    public static MutableByteBuffer wrapUsing(MutableByteBuffer buffer, byte[] bytes) {
        if (buffer instanceof MutableByteBufferImpl) {
            MutableByteBufferImpl bufImpl = (MutableByteBufferImpl) buffer;
            ByteBufferMemory memory = bufImpl.memory;
            if (memory instanceof BytesWrappingCapableMemory) {
                ((BytesWrappingCapableMemory) memory).wrap(bytes);
                return buffer;
            }
        }
        throw new IllegalArgumentException();
    }

    public static MutableByteBuffer wrap(byte[] bytes, int offset, int size) {
        if (offset < 0 || offset > bytes.length) {
            throw new IllegalArgumentException();
        }
        if (size < 0 || offset + size > bytes.length) {
            throw new IllegalArgumentException();
        }

        ByteBufferMemory memory = FACTORY_FIXED_MEMORY_WRAPPING_BYTES.apply(bytes);
        MutableByteBuffer result = new MutableByteBufferImpl(memory);
        result.limit(offset + size);
        result.position(offset);
        return result;
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
            ByteBufferMemory memory = bufImpl.memory;
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

    public static void skip(ReadOnlyByteBuffer buffer, int skipLength) {
        buffer.position(buffer.position() + skipLength);
    }

    public static char readChar(ReadOnlyByteBuffer buffer) {
        int ch1 = buffer.get();
        int ch2 = buffer.get();
        if ((ch1 | ch2) < 0) {
            throw new BufferOverflowException();
        }
        return (char) ((ch1 << 8) + (ch2));
    }

    @FunctionalInterface
    private interface IntTriFunction<R> {
        R apply(int param1, int param2, int param3);
    }
}
