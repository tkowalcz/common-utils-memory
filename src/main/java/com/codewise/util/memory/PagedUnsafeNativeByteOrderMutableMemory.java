package com.codewise.util.memory;

import com.codewise.util.lowlevel.Bits;
import com.codewise.util.lowlevel.MemoryAccess;
import com.google.common.base.Preconditions;
import sun.misc.Unsafe;

import static com.codewise.util.lowlevel.MemoryAccess.*;

public class PagedUnsafeNativeByteOrderMutableMemory extends AbstractPagedMutableMemory {

    public static final int ARRAY_OBJECT_INDEX_SCALE_BITS;

    static {
        Preconditions.checkArgument(isPositiveAndPowerOf2(Unsafe.ARRAY_OBJECT_INDEX_SCALE));
        ARRAY_OBJECT_INDEX_SCALE_BITS = Integer.numberOfTrailingZeros(Unsafe.ARRAY_OBJECT_INDEX_SCALE);
    }

    private final long pageIndexMask;
    private final int pageIndexScaleBits;

    protected PagedUnsafeNativeByteOrderMutableMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        super(pageCntGrow, pageSizeBits, initialCapacity);
        pageIndexMask = ~((long) pageAddrMask);
        pageIndexScaleBits = pageSizeBits - ARRAY_OBJECT_INDEX_SCALE_BITS;
    }

    protected PagedUnsafeNativeByteOrderMutableMemory() {
        super();
        pageIndexMask = ~((long) pageAddrMask);
        pageIndexScaleBits = pageSizeBits - ARRAY_OBJECT_INDEX_SCALE_BITS;
    }

    protected PagedUnsafeNativeByteOrderMutableMemory(int size) {
        super(size);
        pageIndexMask = ~((long) pageAddrMask);
        pageIndexScaleBits = pageSizeBits - ARRAY_OBJECT_INDEX_SCALE_BITS;
    }

    private long toScaledMemoryPageIndex(int index) {
        return (index & pageIndexMask) >>> pageIndexScaleBits;
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(int offset) {
        return (byte[]) getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(offset));
    }

    @Override
    public byte get(int index) {
        checkCapacity(index + Byte.BYTES);
        Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index));
        return getByteUnsafe(page, ARRAY_BYTE_BASE_OFFSET + (index & pageAddrMask));
    }

    @Override
    public void put(int index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index));
        setByteUnsafe(page, ARRAY_BYTE_BASE_OFFSET + (index & pageAddrMask), b);
    }

    @Override
    public char getChar(int index) {
        checkCapacity(index + Character.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            return getNativeByteOrderCharUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            byte b0 = getByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr);
            byte b1 = getByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);
            return Bits.asChar(b0, b1);
        }
    }

    @Override
    public void putChar(int index, char value) {
        ensureCapacity(index + Character.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            setNativeByteOrderCharUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            setByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr, Bits.getByte0(value));
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            setByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET, Bits.getByte1(value));
        }
    }

    @Override
    public short getShort(int index) {
        checkCapacity(index + Short.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            return MemoryAccess.getNativeByteOrderShortUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            byte b0 = getByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr);
            byte b1 = getByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);
            return Bits.asShort(b0, b1);
        }
    }

    @Override
    public void putShort(int index, short value) {
        ensureCapacity(index + Short.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long pageOffset = ARRAY_OBJECT_BASE_OFFSET + toScaledMemoryPageIndex(index);
        if (pageAddr < pageAddrMask) {
            Object page = getObjectUnsafe(memory, pageOffset);
            setNativeByteOrderShortUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            Object page0 = getObjectUnsafe(memory, pageOffset);
            setByteUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddr, Bits.getByte0(value));
            Object page1 = getObjectUnsafe(memory, pageOffset + ARRAY_OBJECT_INDEX_SCALE);
            setByteUnsafe(page1, ARRAY_BYTE_BASE_OFFSET, Bits.getByte1(value));
        }
    }

    @Override
    public int getInt(int index) {
        int nextIndex = index + Integer.BYTES;
        checkCapacity(nextIndex);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            return getNativeByteOrderIntUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            Object page0 = getObjectUnsafe(memory, memoryOffset);
            Object page1 = getObjectUnsafe(memory, memoryOffset + ARRAY_OBJECT_INDEX_SCALE);

            int i0 = getNativeByteOrderIntUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddrMask - 3);
            int i1 = getNativeByteOrderIntUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);

            return Bits.asInt(i0, i1, (pageAddr & 0x3) << 3);
        }
    }

    @Override
    public void putInt(int index, int value) {
        int nextIndex = index + Integer.BYTES;
        ensureCapacity(nextIndex);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            setNativeByteOrderIntUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            long pageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddr;
            long lastPageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddrMask;

            Object page = getObjectUnsafe(memory, memoryOffset);

            setByteUnsafe(page, pageOffset++, Bits.getByte0(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte1(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte2(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset, Bits.getByte3(value));
        }
    }

    @Override
    public long getLong(int index) {
        int nextIndex = index + Long.BYTES;
        checkCapacity(nextIndex);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            return getNativeByteOrderLongUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            Object page0 = getObjectUnsafe(memory, memoryOffset);
            Object page1 = getObjectUnsafe(memory, memoryOffset + ARRAY_OBJECT_INDEX_SCALE);

            long l0 = getNativeByteOrderLongUnsafe(page0, ARRAY_BYTE_BASE_OFFSET + pageAddrMask - 7);
            long l1 = getNativeByteOrderLongUnsafe(page1, ARRAY_BYTE_BASE_OFFSET);

            int l1Bits = (pageAddr & 0x7) << 3;
            return Bits.asLong(l0, l1, l1Bits);
        }
    }

    @Override
    public void putLong(int index, long value) {
        int nextIndex = index + Long.BYTES;
        ensureCapacity(nextIndex);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        long memoryOffset = toScaledMemoryPageIndex(index);
        long lastByteMemoryOffset = toScaledMemoryPageIndex(nextIndex - 1);
        if (memoryOffset == lastByteMemoryOffset) {
            Object page = getObjectUnsafe(memory, ARRAY_OBJECT_BASE_OFFSET + memoryOffset);
            setNativeByteOrderLongUnsafe(page, ARRAY_BYTE_BASE_OFFSET + pageAddr, value);
        } else {
            memoryOffset += ARRAY_OBJECT_BASE_OFFSET;
            long pageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddr;
            long lastPageOffset = ARRAY_BYTE_BASE_OFFSET + pageAddrMask;

            Object page = getObjectUnsafe(memory, memoryOffset);

            setByteUnsafe(page, pageOffset++, Bits.getByte0(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte1(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte2(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte3(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte4(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte5(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset++, Bits.getByte6(value));
            if (pageOffset > lastPageOffset) {
                memoryOffset += ARRAY_OBJECT_INDEX_SCALE;
                page = getObjectUnsafe(memory, memoryOffset);
                pageOffset = ARRAY_BYTE_BASE_OFFSET;
            }
            setByteUnsafe(page, pageOffset, Bits.getByte7(value));
        }
    }
}
