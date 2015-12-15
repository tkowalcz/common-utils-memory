package com.codewise.util.memory;

import java.nio.ByteOrder;

public class PagedUnsafeNativeByteOrderMutableMemoryTest extends PagedMutableMemoryTestBase<PagedUnsafeNativeByteOrderMutableMemory> {

    @Override
    protected PagedUnsafeNativeByteOrderMutableMemory newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new PagedUnsafeNativeByteOrderMutableMemory(pageCntGrow, pageSizeBits, initialCapacity);
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }
}