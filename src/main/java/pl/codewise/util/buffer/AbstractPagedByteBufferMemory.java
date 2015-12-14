package pl.codewise.util.buffer;

import com.google.common.base.Preconditions;

public abstract class AbstractPagedByteBufferMemory extends AbstractByteBufferMemory {

    protected final int pageCntGrowMask;
    protected final int pageSizeBits;
    protected final int pageSize;
    protected final int pageAddrMask;

    protected final long maxPageOffset;
    protected final long lastPageOffset;

    protected byte[][] memory;

    protected int pageCount;
    protected int pageCapacity;

    AbstractPagedByteBufferMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        Preconditions.checkArgument(isPositiveAndPowerOf2(pageCntGrow));

        pageCntGrowMask = pageCntGrow - 1;

        this.pageSizeBits = pageSizeBits;
        pageSize = 1 << pageSizeBits;
        pageAddrMask = pageSize - 1;

        maxPageOffset = MemoryAccess.ARRAY_BYTE_BASE_OFFSET + pageSize;
        lastPageOffset = maxPageOffset - 1;

        capacity = initialCapacity > 0 ? ((initialCapacity - 1) | pageAddrMask) + 1 : 0;
        pageCount = this.capacity >>> pageSizeBits;
        pageCapacity = ((pageCount - 1) | pageCntGrowMask) + 1;
        byte[][] pages = new byte[pageCapacity][];
        for (int idx = 0; idx < pageCount; idx++) {
            pages[idx] = new byte[pageSize];
        }

        memory = pages;
    }

    AbstractPagedByteBufferMemory() {
        this(64, 10, 0);
    }

    AbstractPagedByteBufferMemory(int size) {
        this(64, 10, size);
    }

    @Override
    protected int getPageOffset(int index) {
        return index & pageAddrMask;
    }

    @Override
    protected int getPageLength(int index) {
        return pageSize;
    }

    @Override
    protected void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity > capacity) {
            requiredCapacity = ((requiredCapacity - 1) | pageAddrMask) + 1;
            int requiredPageCount = requiredCapacity >>> pageSizeBits;
            int requiredPageCapacity = ((requiredPageCount - 1) | pageCntGrowMask) + 1;
            if (requiredPageCapacity > pageCapacity) {
                byte[][] newPages = new byte[requiredPageCapacity][];
                System.arraycopy(memory, 0, newPages, 0, pageCount);
                memory = newPages;
                pageCapacity = requiredPageCapacity;
            }
            while (pageCount < requiredPageCount) {
                memory[pageCount++] = new byte[pageSize];
            }
            capacity = requiredCapacity;
        }
    }

    public static boolean isPositiveAndPowerOf2(int x) {
        return (x > 0) && ((x & (x - 1)) == 0);
    }
}
