package pl.codewise.util.buffer;

public class PagedSafeByteBufferMemoryTest extends PagedByteBufferMemoryTestBase<PagedSafeByteBufferMemory> {

    @Override
    protected PagedSafeByteBufferMemory newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        return new PagedSafeByteBufferMemory(pageCntGrow, pageSizeBits, initialCapacity);
    }
}
