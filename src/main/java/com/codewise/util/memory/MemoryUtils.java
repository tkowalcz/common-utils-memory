package com.codewise.util.memory;

import com.codewise.util.lowlevel.MemoryAccess;

public class MemoryUtils {

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
