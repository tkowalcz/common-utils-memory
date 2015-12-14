package pl.codewise.util.buffer;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;

public abstract class AbstractByteBufferMemory implements ByteBufferMemory {

    protected int capacity;

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public double getDouble(int index) {
        return longBitsToDouble(getLong(index));
    }

    @Override
    public void putDouble(int index, double value) {
        putLong(index, doubleToRawLongBits(value));
    }

    @Override
    public void get(int index, byte[] dst, int offset, int length) {
        if (length > 0) {
            checkCapacity(index + 1);
            length = Math.min(capacity - index, length);
            while (length > 0) {
                byte[] srcPage = getMemoryPageAsByteArray(index);
                int srcPageLength = getPageLength(index);
                int srcPageOffset = getPageOffset(index);
                int bytesToCopy = Math.min(srcPageLength - srcPageOffset, length);
                System.arraycopy(srcPage, srcPageOffset, dst, offset, bytesToCopy);
                index += bytesToCopy;
                offset += bytesToCopy;
                length -= bytesToCopy;
            }
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void get(int index, ByteBuffer buf) {
        int length = buf.limit() - buf.position();
        if (length > 0) {
            checkCapacity(index + 1);
            length = Math.min(capacity - index, length);
            while (length > 0) {
                byte[] srcPage = getMemoryPageAsByteArray(index);
                int srcPageLength = getPageLength(index);
                int srcPageOffset = getPageOffset(index);
                int bytesToCopy = Math.min(srcPageLength - srcPageOffset, length);
                buf.put(srcPage, srcPageOffset, bytesToCopy);
                index += bytesToCopy;
                length -= bytesToCopy;
            }
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void put(int index, byte[] src, int offset, int length) {
        if (length > 0) {
            ensureCapacity(index + length);
            while (length > 0) {
                byte[] dstPage = this.getMemoryPageAsByteArray(index);
                int dstPageLength = this.getPageLength(index);
                int dstPageOffset = this.getPageOffset(index);
                int bytesToCopy = Math.min(dstPageLength - dstPageOffset, length);
                System.arraycopy(src, offset, dstPage, dstPageOffset, bytesToCopy);
                index += bytesToCopy;
                offset += bytesToCopy;
                length -= bytesToCopy;
            }
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void put(int index, ByteBufferMemory src, int offset, int length) {
        assert src instanceof AbstractByteBufferMemory;

        if (length > 0) {
            ensureCapacity(index + length);

            AbstractByteBufferMemory that = (AbstractByteBufferMemory) src;
            that.checkCapacity(offset + length);

            while (length > 0) {
                byte[] srcPage = that.getMemoryPageAsByteArray(offset);
                int srcPageLength = that.getPageLength(offset);
                int srcPageOffset = that.getPageOffset(offset);
                byte[] dstPage = this.getMemoryPageAsByteArray(index);
                int dstPageLength = this.getPageLength(index);
                int dstPageOffset = this.getPageOffset(index);
                int bytesToCopy = Math.min(Math.min(dstPageLength - dstPageOffset, srcPageLength - srcPageOffset), length);
                System.arraycopy(srcPage, srcPageOffset, dstPage, dstPageOffset, bytesToCopy);
                index += bytesToCopy;
                offset += bytesToCopy;
                length -= bytesToCopy;
            }
        } else if (length < 0) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int compare(int index, ReadOnlyMemory src, int offset, int length) {
        assert src instanceof AbstractByteBufferMemory;

        if (length > 0) {
            checkCapacity(index + length);

            AbstractByteBufferMemory that = (AbstractByteBufferMemory) src;
            that.checkCapacity(offset + length);

            while (length > 0) {
                byte[] thatPage = that.getMemoryPageAsByteArray(offset);
                int thatPageLength = that.getPageLength(offset);
                int thatPageOffset = that.getPageOffset(offset);

                byte[] thisPage = this.getMemoryPageAsByteArray(index);
                int thisPageLength = this.getPageLength(index);
                int thisPageOffset = this.getPageOffset(index);

                int bytesToCompare = Math.min(Math.min(thisPageLength - thisPageOffset, thatPageLength - thatPageOffset), length);

                if (MemoryAccess.UNSAFE_MEMORY_ACCESS) {
                    long thisPagePtr = thisPageOffset + MemoryAccess.ARRAY_BYTE_BASE_OFFSET;
                    long thatPagePtr = thatPageOffset + MemoryAccess.ARRAY_BYTE_BASE_OFFSET;

                    for (int idx = bytesToCompare >>> 3; idx > 0; idx--) {
                        long thisL = MemoryAccess.getLongUnsafe(thisPage, thisPagePtr);
                        long thatL = MemoryAccess.getLongUnsafe(thatPage, thatPagePtr);
                        int cmp = Long.compareUnsigned(thisL, thatL);
                        if (cmp != 0) {
                            return cmp;
                        }
                        thisPagePtr += 8;
                        thatPagePtr += 8;
                    }
                    for (int idx = bytesToCompare & 0x7; idx > 0; idx--) {
                        byte thisB = MemoryAccess.getByteUnsafe(thisPage, thisPagePtr);
                        byte thatB = MemoryAccess.getByteUnsafe(thatPage, thatPagePtr);
                        int cmp = Integer.compare((int) thisB & 0xFF, (int) thatB & 0xFF);
                        if (cmp != 0) {
                            return cmp;
                        }
                        thisPagePtr++;
                        thatPagePtr++;
                    }
                } else {
                    for (int idx = bytesToCompare; idx > 0; idx--) {
                        int cmp = Integer.compare((int) thisPage[thisPageOffset++] & 0xFF, (int) thatPage[thatPageOffset++] & 0xFF);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }

                index += bytesToCompare;
                offset += bytesToCompare;
                length -= bytesToCompare;
            }
            return 0;
        } else if (length == 0) {
            return 0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void iterateOverMemory(MemoryConsumer consumer, int offset, int length) {
        if (length < 0) {
            throw new IllegalArgumentException();
        }

        while (length > 0) {
            int pageOffset = getPageOffset(offset);
            int pageLength = getPageLength(offset);
            byte[] page = getMemoryPageAsByteArray(offset);
            int dataInPage = Math.min(length, pageLength - pageOffset);
            if (dataInPage > 0) {
                consumer.accept(page, pageOffset, dataInPage);
                offset += dataInPage;
                length -= dataInPage;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    protected int getPageOffset(int index) {
        return index;
    }

    protected int getPageLength(int index) {
        return capacity;
    }

    protected abstract byte[] getMemoryPageAsByteArray(int offset);

    protected void checkCapacity(int size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferUnderflowException();
        }
    }

    protected void ensureCapacity(int size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferOverflowException();
        }
    }

}