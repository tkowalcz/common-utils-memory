package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;
import com.google.common.base.Preconditions;
import sun.misc.Unsafe;

import static com.codewise.util.lowlevel.MemoryAccess.*;

public class PagedUnsafeMutableMemory extends AbstractPagedMutableMemory {

    public static final int ARRAY_OBJECT_INDEX_SCALE_BITS;

    static {
        Preconditions.checkArgument(isPositiveAndPowerOf2(Unsafe.ARRAY_OBJECT_INDEX_SCALE));
        ARRAY_OBJECT_INDEX_SCALE_BITS = Integer.numberOfTrailingZeros(Unsafe.ARRAY_OBJECT_INDEX_SCALE);
    }

    private final long pageIndexMask;
    private final int pageIndexScaleBits;

    protected PagedUnsafeMutableMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        super(pageCntGrow, pageSizeBits, initialCapacity);
        pageIndexMask = ~pageAddrMask;
        pageIndexScaleBits = pageSizeBits - ARRAY_OBJECT_INDEX_SCALE_BITS;
    }

    protected PagedUnsafeMutableMemory() {
        super();
        pageIndexMask = ~pageAddrMask;
        pageIndexScaleBits = pageSizeBits - ARRAY_OBJECT_INDEX_SCALE_BITS;
    }

    protected PagedUnsafeMutableMemory(int size) {
        super(size);
        pageIndexMask = ~pageAddrMask;
        pageIndexScaleBits = pageSizeBits - ARRAY_OBJECT_INDEX_SCALE_BITS;
    }

    private long toScaledMemoryPageIndex(long index) {
        return (index & pageIndexMask) >>> pageIndexScaleBits;
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(long offset) {
        return (byte[]) getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(offset));
    }

    @Override
    public byte get(long index) {
        checkCapacity(index + Byte.BYTES);
        Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index));
        return getByteUnsafe(page, ARRAY_BYTE_BASE_OFFSET + (index & pageAddrMask));
    }

    @Override
    public void put(long index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index));
        setByteUnsafe(page, ARRAY_BYTE_BASE_OFFSET + (index & pageAddrMask), b);
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            return getCharUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            byte b1 = getByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr);
            byte b0 = getByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);
            return (char) ((b1 << 8) | (b0 & 0xff));
        }
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            setCharUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            byte b1 = (byte) (value >> 8);
            byte b0 = (byte) value;
            setByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr, b1);
            setByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET, b0);
        }
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            return MemoryAccess.getShortUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            byte b1 = getByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr);
            byte b0 = getByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);
            return (short) ((b1 << 8) | (b0 & 0xff));
        }
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            setShortUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            byte b1 = (byte) (value >> 8);
            byte b0 = (byte) value;
            setByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr, b1);
            setByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET, b0);
        }
    }

    @Override
    public int getInt(long index) {
        long nextIndex = index + Integer.BYTES;
        checkCapacity(nextIndex);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            return getIntUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            /*
            for pageAddr & 0x03 == 1 and memory content:
            page0             page1
            [... aa 00 11 22] [33 bb cc dd ...]

            we want result to be:
                00112233

            for little endian CPU:
                msw = UNSAFE.getInt(page0, pageSize-4)  = 221100aa
                lsw = UNSAFE.getInt(page1, 0)           = ddccbb33
                res = (swap(msw) << 8) | (swap(lsw) >> 24) => (swap(msw) << lswBits) | (swap(lsw) >>> (32 - lswBits))

            big endian CPU:
                msw = UNSAFE.getInt(page0, pageSize-4)  = aa001122
                lsw = UNSAFE.getInt(page1, 0)           = 33bbccdd
                res = 00112233 => (msw << 8) | (lsw >> 24) => (msw << lswBits) | (lsw >>> (32 - lswBits))

            where
                lswBits = (pageAddr & 0x03) << 3
            */
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            Object page0 = getObjectUnsafe(memory, memoryOffset);
            Object page1 = getObjectUnsafe(memory, memoryOffset + ARRAY_OBJECT_INDEX_SCALE);

            int msw = getIntUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddrMask - 3);
            int lsw = getIntUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);

            int lswBits = (int) (pageAddr & 0x3) << 3;
            return (msw << lswBits) | (lsw >>> (32 - lswBits));
        }
    }

    @Override
    public void putInt(long index, int value) {
        long nextIndex = index + Integer.BYTES;
        ensureCapacity(nextIndex);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            setIntUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            long pageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddr;
            long lastPageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddrMask;

            Object page = getObjectUnsafe(memory, memoryOffset);

            setByteUnsafe(page, pageOffset++, (byte) (value >>> 24));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 16));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 8));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset, (byte) value);
        }
    }

    @Override
    public long getLong(long index) {
        long nextIndex = index + Long.BYTES;
        checkCapacity(nextIndex);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            return getLongUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            Object page0 = getObjectUnsafe(memory, memoryOffset);
            Object page1 = getObjectUnsafe(memory, memoryOffset + ARRAY_OBJECT_INDEX_SCALE);

            long msw = getLongUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddrMask - 7);
            long lsw = getLongUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);

            int lswBits = (int) (pageAddr & 0x7) << 3;
            return (msw << lswBits) | (lsw >>> (64 - lswBits));
        }
    }

    @Override
    public void putLong(long index, long value) {
        long nextIndex = index + Long.BYTES;
        ensureCapacity(nextIndex);
        long pageAddrMask = this.pageAddrMask;
        long pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            setLongUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            long pageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddr;
            long lastPageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddrMask;

            Object page = getObjectUnsafe(memory, memoryOffset);

            setByteUnsafe(page, pageOffset++, (byte) (value >>> 56));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 48));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 40));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 32));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 24));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 16));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, (byte) (value >>> 8));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset, (byte) value);
        }
    }
}
