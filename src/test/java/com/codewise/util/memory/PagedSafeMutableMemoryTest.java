package com.codewise.util.memory;

public class PagedSafeMutableMemoryTest extends PagedMutableMemoryTestBase<PagedSafeMutableMemory> {

    @Override
    protected PagedSafeMutableMemory newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new PagedSafeMutableMemory(pageCntGrow, pageSizeBits, initialCapacity);
    }
}
