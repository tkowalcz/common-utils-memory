package pl.codewise.util.buffer;

class PagedSafeByteBufferMemory extends AbstractPagedByteBufferMemory {

    PagedSafeByteBufferMemory(int pageCntGrow, int pageSizeBits, int initialCapacity) {
        super(pageCntGrow, pageSizeBits, initialCapacity);
    }

    PagedSafeByteBufferMemory() {
    }

    PagedSafeByteBufferMemory(int size) {
        super(size);
    }

    @Override
    protected byte[] getMemoryPageAsByteArray(int offset) {
        return memory[offset >>> pageSizeBits];
    }

    @Override
    public byte get(int index) {
        checkCapacity(index + Byte.BYTES);
        byte[] page = memory[index >>> pageSizeBits];
        return page[(index & pageAddrMask)];
    }

    @Override
    public void put(int index, byte b) {
        ensureCapacity(index + Byte.BYTES);
        byte[] page = memory[index >>> pageSizeBits];
        page[(index & pageAddrMask)] = b;
    }

    @Override
    public char getChar(int index) {
        checkCapacity(index + Character.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[index >>> pageSizeBits];
            return MemoryAccess.getCharSafe(page, pageAddr);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b0 = page1[0];
            byte b1 = page0[pageAddr];
            return (char) ((b1 << 8) | (b0 & 0xff));
        }
    }

    @Override
    public void putChar(int index, char value) {
        ensureCapacity(index + Character.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[index >>> pageSizeBits];
            MemoryAccess.setCharSafe(page, pageAddr, value);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b1 = (byte) (value >> 8);
            byte b0 = (byte) value;
            page0[pageAddr] = b1;
            page1[0] = b0;
        }
    }

    @Override
    public short getShort(int index) {
        checkCapacity(index + Short.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[index >>> pageSizeBits];
            return MemoryAccess.getShortSafe(page, pageAddr);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b0 = page1[0];
            byte b1 = page0[pageAddr];
            return (short) ((b1 << 8) | (b0 & 0xff));
        }
    }

    @Override
    public void putShort(int index, short value) {
        ensureCapacity(index + Short.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr < pageAddrMask) {
            byte[] page = memory[index >>> pageSizeBits];
            MemoryAccess.setShortSafe(page, pageAddr, value);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page0 = memory[pageIndex];
            byte[] page1 = memory[pageIndex + 1];
            byte b1 = (byte) (value >> 8);
            byte b0 = (byte) value;
            page0[pageAddr] = b1;
            page1[0] = b0;
        }
    }

    @Override
    public int getInt(int index) {
        checkCapacity(index + Integer.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr <= pageAddrMask - 3) {
            byte[] page = memory[index >>> pageSizeBits];
            return MemoryAccess.getIntSafe(page, pageAddr);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page = memory[pageIndex++];

            byte b3 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b2 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b1 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex]; pageAddr = 0; }
            byte b0 = page[pageAddr];

            return (b3 & 0xff) << 24 |
                   (b2 & 0xff) << 16 |
                   (b1 & 0xff) << 8 |
                    b0 & 0xff;
        }
    }

    @Override
    public void putInt(int index, int value) {
        ensureCapacity(index + Integer.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr <= pageAddrMask - 3) {
            byte[] page = memory[index >>> pageSizeBits];
            MemoryAccess.setIntSafe(page, pageAddr, value);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page = memory[pageIndex++];

            page[pageAddr++] = (byte) (value >>> 24); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 16); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 8); if (pageAddr > pageAddrMask) { page = memory[pageIndex]; pageAddr = 0; }
            page[pageAddr] = (byte) value;
        }
    }

    @Override
    public long getLong(int index) {
        checkCapacity(index + Long.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr <= pageAddrMask - 7) {
            byte[] page = memory[index >>> pageSizeBits];
            return MemoryAccess.getLongSafe(page, pageAddr);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page = memory[pageIndex++];

            byte b7 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b6 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b5 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b4 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b3 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b2 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            byte b1 = page[pageAddr++]; if (pageAddr > pageAddrMask) { page = memory[pageIndex]; pageAddr = 0; }
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
    public void putLong(int index, long value) {
        ensureCapacity(index + Long.BYTES);
        int pageAddrMask = this.pageAddrMask;
        int pageAddr = index & pageAddrMask;
        if (pageAddr <= pageAddrMask - 7) {
            byte[] page = memory[index >>> pageSizeBits];
            MemoryAccess.setLongSafe(page, pageAddr, value);
        } else {
            int pageIndex = index >>> pageSizeBits;
            byte[] page = memory[pageIndex++];

            page[pageAddr++] = (byte) (value >>> 56); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 48); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 40); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 32); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 24); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 16); if (pageAddr > pageAddrMask) { page = memory[pageIndex++]; pageAddr = 0; }
            page[pageAddr++] = (byte) (value >>> 8); if (pageAddr > pageAddrMask) { page = memory[pageIndex]; pageAddr = 0; }
            page[pageAddr] = (byte) value;
        }
    }
}
