package pl.codewise.util.buffer;

public class PagedUnsafeByteBufferMemoryTest extends PagedByteBufferMemoryTestBase<PagedUnsafeByteBufferMemory> {

    @Override
    protected PagedUnsafeByteBufferMemory newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new PagedUnsafeByteBufferMemory(pageCntGrow, pageSizeBits, initialCapacity);
    }
}
