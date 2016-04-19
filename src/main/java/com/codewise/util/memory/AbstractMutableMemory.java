package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.longBitsToDouble;

public abstract class AbstractMutableMemory implements MutableMemory {

    protected long capacity;

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public double getDouble(long index) {
        return longBitsToDouble(getLong(index));
    }

    @Override
    public void putDouble(long index, double value) {
        putLong(index, doubleToRawLongBits(value));
    }

    @Override
    public void get(long index, byte[] dst, int offset, int length) {
        if (length > 0) {
            checkCapacity(index + 1);
            length = Math.toIntExact(Math.min(capacity - index, length));
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
    public void get(long index, ByteBuffer buf) {
        int length = buf.limit() - buf.position();
        if (length > 0) {
            checkCapacity(index + 1);
            length = Math.toIntExact(Math.min(capacity - index, length));
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
    public void put(long index, byte[] src, int offset, int length) {
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
    public void put(long index, MutableMemory src, long offset, long length) {
        assert src instanceof AbstractMutableMemory;

        if (length > 0) {
            ensureCapacity(index + length);

            AbstractMutableMemory that = (AbstractMutableMemory) src;
            that.checkCapacity(offset + length);

            while (length > 0) {
                byte[] srcPage = that.getMemoryPageAsByteArray(offset);
                int srcPageLength = that.getPageLength(offset);
                int srcPageOffset = that.getPageOffset(offset);
                byte[] dstPage = this.getMemoryPageAsByteArray(index);
                int dstPageLength = this.getPageLength(index);
                int dstPageOffset = this.getPageOffset(index);
                int bytesToCopy = Math.toIntExact(Math.min(Math.min(dstPageLength - dstPageOffset, srcPageLength - srcPageOffset), length));
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
    public int compare(long index, ReadOnlyMemory src, long offset, long length) {
        assert src instanceof AbstractMutableMemory;

        if (length > 0) {
            checkCapacity(index + length);

            AbstractMutableMemory that = (AbstractMutableMemory) src;
            that.checkCapacity(offset + length);

            while (length > 0) {
                byte[] thatPage = that.getMemoryPageAsByteArray(offset);
                int thatPageLength = that.getPageLength(offset);
                int thatPageOffset = that.getPageOffset(offset);

                byte[] thisPage = this.getMemoryPageAsByteArray(index);
                int thisPageLength = this.getPageLength(index);
                int thisPageOffset = this.getPageOffset(index);

                int bytesToCompare = Math.toIntExact(Math.min(Math.min(thisPageLength - thisPageOffset, thatPageLength - thatPageOffset), length));

                int cmp = MemoryUtils.compare(thisPage, thisPageOffset, thatPage, thatPageOffset, bytesToCompare);
                if (cmp == 0) {
                    return 0;
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
    public void iterateOverMemory(MemoryConsumer consumer, long offset, long length) {
        if (length < 0) {
            throw new IllegalArgumentException();
        }

        while (length > 0) {
            int pageOffset = getPageOffset(offset);
            int pageLength = getPageLength(offset);
            byte[] page = getMemoryPageAsByteArray(offset);
            int dataInPage = Math.toIntExact(Math.min(length, (long) (pageLength - pageOffset)));
            if (dataInPage > 0) {
                consumer.accept(page, pageOffset, dataInPage);
                offset += dataInPage;
                length -= dataInPage;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    protected int getPageOffset(long index) {
        return (int) index;
    }

    protected int getPageLength(long index) {
        return (int) capacity;
    }

    protected abstract byte[] getMemoryPageAsByteArray(long offset);

    protected void checkCapacity(long size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferUnderflowException();
        }
    }

    protected void ensureCapacity(long size) {
        if (MemoryAccess.RANGE_CHECKS && size > capacity) {
            throw new BufferOverflowException();
        }
    }

}