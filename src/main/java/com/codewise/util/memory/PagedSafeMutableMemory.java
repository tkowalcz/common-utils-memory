package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

import static java.lang.Math.toIntExact;

public class PagedSafeMutableMemory extends AbstractPagedMutableMemory {

    protected PagedSafeMutableMemory(int pageCntGrow, int pageSizeBits, long initialCapacity) {
        super(pageCntGrow, pageSizeBits, initialCapacity);
    }

    protected PagedSafeMutableMemory() {
    }

    protected PagedSafeMutableMemory(long size) {
        super(size);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(long offset) {
        return memory[toIntExact(offset >>> pageSizeBits)];
    }

    @Override
    public byte get(long index) {
        checkCapacity(index + Byte.BYTES);
        byte[] page = memory[toIntExact(index >>> pageSizeBits)];
        return page[(int) (index & pageAddrMask)];
    }

    @Override
    public void put(long index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        byte[] page = memory[toIntExact(index >>> pageSizeBits)];
        page[(int) (index & pageAddrMask)] = b;
    }

    @Override
    public char getChar(long index) {
        checkCapacity(index + Character.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            return MemoryAccess.getCharSafe(page, pageAddr);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b0 = page1[0];
            byte b1 = page0[pageAddr];
            return (char) ((b1 << 8) | (b0 & 0xff));
        }
    }

    @Override
    public void putChar(long index, char value) {
        ensureCapacity(index + Character.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            MemoryAccess.setCharSafe(page, pageAddr, value);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b1 = (byte) (value >> 8);
            byte b0 = (byte) value;
            page0[pageAddr] = b1;
            page1[0] = b0;
        }
    }

    @Override
    public short getShort(long index) {
        checkCapacity(index + Short.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            return MemoryAccess.getShortSafe(page, pageAddr);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b0 = page1[0];
            byte b1 = page0[pageAddr];
            return (short) ((b1 << 8) | (b0 & 0xff));
        }
    }

    @Override
    public void putShort(long index, short value) {
        ensureCapacity(index + Short.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            MemoryAccess.setShortSafe(page, pageAddr, value);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b1 = (byte) (value >> 8);
            byte b0 = (byte) value;
            page0[pageAddr] = b1;
            page1[0] = b0;
        }
    }

    @Override
    public int getInt(long index) {
        checkCapacity(index + Integer.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr <= pageAddrMask - 3) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            return MemoryAccess.getIntSafe(page, pageAddr);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page = memory[pageIndex++];

            byte b3 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b2 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b1 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex];
                pageAddr = 0;
            }
            byte b0 = page[pageAddr];

            return (b3 & 0xff) << 24 |
                    (b2 & 0xff) << 16 |
                    (b1 & 0xff) << 8 |
                    b0 & 0xff;
        }
    }

    @Override
    public void putInt(long index, int value) {
        ensureCapacity(index + Integer.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr <= pageAddrMask - 3) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            MemoryAccess.setIntSafe(page, pageAddr, value);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page = memory[pageIndex++];

            page[pageAddr++] = (byte) (value >>> 24);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 16);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 8);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex];
                pageAddr = 0;
            }
            page[pageAddr] = (byte) value;
        }
    }

    @Override
    public long getLong(long index) {
        checkCapacity(index + Long.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr <= pageAddrMask - 7) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            return MemoryAccess.getLongSafe(page, pageAddr);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page = memory[pageIndex++];

            byte b7 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b6 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b5 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b4 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b3 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b2 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            byte b1 = page[pageAddr++];
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex];
                pageAddr = 0;
            }
            byte b0 = page[pageAddr];

            return ((long) b7 & 0xff) << 56 |
                    ((long) b6 & 0xff) << 48 |
                    ((long) b5 & 0xff) << 40 |
                    ((long) b4 & 0xff) << 32 |
                    ((long) b3 & 0xff) << 24 |
                    ((long) b2 & 0xff) << 16 |
                    ((long) b1 & 0xff) << 8 |
                    ((long) b0 & 0xff);
        }
    }

    @Override
    public void putLong(long index, long value) {
        ensureCapacity(index + Long.BYTES);
        long pageAddrMask = this.pageAddrMask;
        int pageAddr = (int) (index & pageAddrMask);
        if (pageAddr <= pageAddrMask - 7) {
            byte[] page = memory[toIntExact(index >>> pageSizeBits)];
            MemoryAccess.setLongSafe(page, pageAddr, value);
        } else {
            int pageIndex = toIntExact(index >>> pageSizeBits);
            byte[] page = memory[pageIndex++];

            page[pageAddr++] = (byte) (value >>> 56);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 48);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 40);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 32);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 24);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 16);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex++];
                pageAddr = 0;
            }
            page[pageAddr++] = (byte) (value >>> 8);
            if (pageAddr > pageAddrMask) {
                page = memory[pageIndex];
                pageAddr = 0;
            }
            page[pageAddr] = (byte) value;
        }
    }
}
