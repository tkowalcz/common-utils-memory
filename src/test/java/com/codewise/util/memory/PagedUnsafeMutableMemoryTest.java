package com.codewise.util.memory;

public class PagedUnsafeMutableMemoryTest extends PagedMutableMemoryTestBase<PagedUnsafeMutableMemory> {

    @Override
    protected PagedUnsafeMutableMemory newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new PagedUnsafeMutableMemory(pageCntGrow, pageSizeBits, initialCapacity);
    }
}
