package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

public abstract class AbstractPagedMutableMemory extends AbstractMutableMemory {

    protected final int pageCntGrowMask;
    protected final int pageSizeBits;
    protected final int pageSize;
    protected final long pageAddrMask;

    protected final long maxPageOffset;
    protected final long lastPageOffset;

    protected byte[][] memory;

    protected int pageCount;
    protected int pageCapacity;

    protected AbstractPagedMutableMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        Preconditions.checkArgument(isPositiveAndPowerOf2(pageCntGrow));

        pageCntGrowMask = pageCntGrow - 1;

        this.pageSizeBits = pageSizeBits;
        pageSize = 1 << pageSizeBits;
        pageAddrMask = pageSize - 1;

        maxPageOffset = MemoryAccess.ARRAY_BYTE_BASE_OFFSET + pageSize;
        lastPageOffset = maxPageOffset - 1;

        capacity = initialCapacity > 0 ? ((initialCapacity - 1) | pageAddrMask) + 1 : 0;
        pageCount = Math.toIntExact(this.capacity >>> pageSizeBits);
        pageCapacity = ((pageCount - 1) | pageCntGrowMask) + 1;
        byte[][] pages = new byte[pageCapacity][];
        for (int idx = 0; idx < pageCount; idx++) {
            pages[idx] = new byte[pageSize];
        }

        memory = pages;
    }

    protected AbstractPagedMutableMemory() {
        this(64, 10, 0);
    }

    protected AbstractPagedMutableMemory(int size) {
        this(64, 10, size);
    }

    @Override
    protected int getPageOffset(long index) {
        return (int) (index & pageAddrMask);
    }

    @Override
    protected int getPageLength(long index) {
        return pageSize;
    }

    @Override
    protected void ensureCapacity(long requiredCapacity) {
        if (requiredCapacity > capacity) {
            requiredCapacity = ((requiredCapacity - 1L) | pageAddrMask) + 1L;
            int requiredPageCount = Math.toIntExact(requiredCapacity >>> pageSizeBits);
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
