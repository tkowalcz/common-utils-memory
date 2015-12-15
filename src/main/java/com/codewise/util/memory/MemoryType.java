package com.codewise.util.memory;

import java.util.Optional;
import java.util.function.Function;

public enum MemoryType {
    SAFE {
        @Override
        public MutableMemory allocateFixedMemory(int size) {
            return new FixedSafeMutableMemory(size);
        }

        @Override
        public MutableMemory allocateGrowableMemory(int size) {
            return new GrowableSafeMutableMemory(size);
        }

        @Override
        public MutableMemory allocatePagedMemory() {
            return new PagedSafeMutableMemory();
        }

        @Override
        public MutableMemory allocatePagedMemory(int size) {
            return new PagedSafeMutableMemory(size);
        }

        @Override
        public MutableMemory allocatePagedMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
            return new PagedSafeMutableMemory(pageCntGrow, pageSizeBits, initialCapacity);
        }

        @Override
        public Optional<Function<byte[], MutableMemory>> getByteArrayWrappingFactory() {
            return Constants.FNC_BYTE_ARRAY_TO_FIXED_SAFE;
        }
    },
    UNSAFE {
        @Override
        public MutableMemory allocateFixedMemory(int size) {
            return new FixedUnsafeMutableMemory(size);
        }

        @Override
        public MutableMemory allocateGrowableMemory(int size) {
            return new GrowableUnsafeMutableMemory(size);
        }

        @Override
        public MutableMemory allocatePagedMemory() {
            return new PagedUnsafeMutableMemory();
        }

        @Override
        public MutableMemory allocatePagedMemory(int size) {
            return new PagedUnsafeMutableMemory(size);
        }

        @Override
        public MutableMemory allocatePagedMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
            return new PagedUnsafeMutableMemory(pageCntGrow, pageSizeBits, initialCapacity);
        }

        @Override
        public Optional<Function<byte[], MutableMemory>> getByteArrayWrappingFactory() {
            return Constants.FNC_BYTE_ARRAY_TO_FIXED_UNSAFE;
        }
    },
    UNSAFE_NATIVE_BYTE_ORDER {
        @Override
        public MutableMemory allocateFixedMemory(int size) {
            return new FixedUnsafeNativeByteOrderMutableMemory(size);
        }

        @Override
        public MutableMemory allocateGrowableMemory(int size) {
            return new GrowableUnsafeNativeByteOrderMutableMemory(size);
        }

        @Override
        public MutableMemory allocatePagedMemory() {
            return new PagedUnsafeNativeByteOrderMutableMemory();
        }

        @Override
        public MutableMemory allocatePagedMemory(int size) {
            return new PagedUnsafeNativeByteOrderMutableMemory(size);
        }

        @Override
        public MutableMemory allocatePagedMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
            return new PagedUnsafeNativeByteOrderMutableMemory(pageCntGrow, pageSizeBits, initialCapacity);
        }

        @Override
        public Optional<Function<byte[], MutableMemory>> getByteArrayWrappingFactory() {
            return Constants.FNC_BYTE_ARRAY_TO_FIXED_UNSAFE_NATIVE_BYTE_ORDER;
        }
    };

    public abstract MutableMemory allocateFixedMemory(int size);

    public abstract MutableMemory allocateGrowableMemory(int size);

    public abstract MutableMemory allocatePagedMemory();

    public abstract MutableMemory allocatePagedMemory(int size);

    public abstract MutableMemory allocatePagedMemory(int pageCntGrow, int pageSizeBits, int initialCapacity);

    public Optional<Function<byte[], MutableMemory>> getByteArrayWrappingFactory() {
        return Optional.empty();
    }

    private static class Constants {
        public static final Optional<Function<byte[], MutableMemory>> FNC_BYTE_ARRAY_TO_FIXED_SAFE = Optional.of(FixedSafeMutableMemory::new);
        public static final Optional<Function<byte[], MutableMemory>> FNC_BYTE_ARRAY_TO_FIXED_UNSAFE = Optional.of(FixedUnsafeMutableMemory::new);
        public static final Optional<Function<byte[], MutableMemory>> FNC_BYTE_ARRAY_TO_FIXED_UNSAFE_NATIVE_BYTE_ORDER = Optional.of(FixedUnsafeNativeByteOrderMutableMemory::new);
    }
}
