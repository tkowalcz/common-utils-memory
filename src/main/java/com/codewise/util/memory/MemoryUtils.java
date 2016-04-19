package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;
import com.google.common.base.Preconditions;

public class MemoryUtils {
/*
    public static int compare(MemoryIterator iterator1, long offset1,
                              MemoryIterator iterator2, long offset2, long length) {
        if (length > 0) {
            MemoryFragments mem1 = new MemoryFragments(iterator1, offset1, length);
            MemoryFragments mem2 = new MemoryFragments(iterator2, offset2, length);
            return compare(mem1, mem2, length);
        } else if (length == 0) {
            return 0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static int compare(MemoryFragments mem1, MemoryFragments mem2, long length) {
        if (length > 0) {
            Preconditions.checkArgument(mem1.length() == mem2.length());

            int mem1Idx = 0;
            int mem2Idx = 0;

            MemoryFragment frag1;
            byte[] buf1 = null;
            int len1 = 0;
            int ofs1 = 0;

            MemoryFragment frag2;
            byte[] buf2 = null;
            int len2 = 0;
            int ofs2 = 0;

            while (length > 0) {
                if (len1 == 0) {
                    frag1 = mem1.get(mem1Idx++);
                    buf1 = frag1.getBuffer();
                    len1 = frag1.getLength();
                    ofs1 = frag1.getOffset();
                }
                if (len2 == 0) {
                    frag2 = mem2.get(mem2Idx++);
                    buf2 = frag2.getBuffer();
                    len2 = frag2.getLength();
                    ofs2 = frag2.getOffset();
                }

                int bytesToCompare = Math.toIntExact(Math.min(Math.min(len1 - ofs1, len2 - ofs2), length));

                int cmp = compare(buf1, ofs1, buf2, ofs2, bytesToCompare);
                if (cmp != 0) {
                    return cmp;
                }

                len1 -= bytesToCompare;
                ofs1 += bytesToCompare;
                len2 -= bytesToCompare;
                ofs2 += bytesToCompare;

                length -= bytesToCompare;
            }

            return 0;
        } else if (length == 0) {
            return 0;
        } else {
            throw new IllegalArgumentException();
        }
    }
*/

    public static int compare(byte[] memory1, int offset1, byte[] memory2, int offset2, int length) {
        if (length == 0) {
            return 0;
        } else {
            if (MemoryAccess.UNSAFE_MEMORY_ACCESS) {
                long ptr1 = offset1 + MemoryAccess.ARRAY_BYTE_BASE_OFFSET;
                long ptr2 = offset2 + MemoryAccess.ARRAY_BYTE_BASE_OFFSET;

                for (int idx = length >>> 3; idx > 0; idx--) {
                    long l1 = MemoryAccess.getLongUnsafe(memory1, ptr1);
                    long l2 = MemoryAccess.getLongUnsafe(memory2, ptr2);
                    int cmp = Long.compareUnsigned(l1, l2);
                    if (cmp != 0) {
                        return cmp;
                    }
                    ptr1 += 8;
                    ptr2 += 8;
                }
                for (int idx = length & 0x7; idx > 0; idx--) {
                    byte b1 = MemoryAccess.getByteUnsafe(memory1, ptr1);
                    byte b2 = MemoryAccess.getByteUnsafe(memory2, ptr2);
                    int cmp = Integer.compare((int) b1 & 0xFF, (int) b2 & 0xFF);
                    if (cmp != 0) {
                        return cmp;
                    }
                    ptr1++;
                    ptr2++;
                }
            } else {
                for (int idx = length; idx > 0; idx--) {
                    int cmp = Integer.compare((int) memory1[offset1++] & 0xFF, (int) memory2[offset2++] & 0xFF);
                    if (cmp != 0) {
                        return cmp;
                    }
                }
            }
            return 0;
        }
    }
}
