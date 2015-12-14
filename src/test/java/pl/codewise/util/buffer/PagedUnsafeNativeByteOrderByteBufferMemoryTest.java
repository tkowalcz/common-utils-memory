package pl.codewise.util.buffer;

import java.nio.ByteOrder;

public class PagedUnsafeNativeByteOrderByteBufferMemoryTest extends PagedByteBufferMemoryTestBase<PagedUnsafeNativeByteOrderByteBufferMemory> {

    @Override
    protected PagedUnsafeNativeByteOrderByteBufferMemory newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new PagedUnsafeNativeByteOrderByteBufferMemory(pageCntGrow, pageSizeBits, initialCapacity);
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }
}