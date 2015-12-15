package com.codewise.util.lowlevel;

public class Bits {

    // ---------------------------
    // -- byte order correction

    public static int correctBites(int x) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Integer.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static long correctBites(long x) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Long.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static short correctBites(short x) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Short.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static char correctBites(char x) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Character.reverseBytes(x);
        } else {
            return x;
        }
    }

    // ----------------------
    // -- long

    public static long asLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (((long) b7) << 56) | ((b6 & 0xffL) << 48) | ((b5 & 0xffL) << 40) | ((b4 & 0xffL) << 32) | ((b3 & 0xffL) << 24) | ((b2 & 0xffL) << 16) | ((b1 & 0xffL) << 8) | (b0 & 0xffL);
        } else {
            return (((long) b0) << 56) | ((b1 & 0xffL) << 48) | ((b2 & 0xffL) << 40) | ((b3 & 0xffL) << 32) | ((b4 & 0xffL) << 24) | ((b5 & 0xffL) << 16) | ((b6 & 0xffL) << 8) | (b7 & 0xffL);
        }
    }

    public static long asLong(long l0, long l1, int l1Bits) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (l0 >>> l1Bits) | (l1 << (64 - l1Bits));
        } else {
            return (l0 << l1Bits) | (l1 >>> (64 - l1Bits));
        }
    }

    public static byte getByte0(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) v;
        } else {
            return (byte) (v >> 56);
        }
    }

    public static byte getByte1(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 8);
        } else {
            return (byte) (v >> 48);
        }
    }

    public static byte getByte2(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 16);
        } else {
            return (byte) (v >> 40);
        }
    }

    public static byte getByte3(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 24);
        } else {
            return (byte) (v >> 32);
        }
    }

    public static byte getByte4(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 32);
        } else {
            return (byte) (v >> 24);
        }
    }

    public static byte getByte5(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 40);
        } else {
            return (byte) (v >> 16);
        }
    }

    public static byte getByte6(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 48);
        } else {
            return (byte) (v >> 8);
        }
    }

    public static byte getByte7(long v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 56);
        } else {
            return (byte) v;
        }
    }

    // ----------------------
    // -- int

    public static int asInt(byte b0, byte b1, byte b2, byte b3) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (b3 << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | (b0 & 0xff);
        } else {
            return (b0 << 24) | ((b1 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b3 & 0xff);
        }
    }

    public static int asInt(int i0, int i1, int i1Bits) {
        /*
        for memory content:
        i0 bytes          i1 bytes
        [... aa 00 11 22] [33 bb cc dd ...]

        where
            i1Bits = (x & 0x03) << 3 => 8

        for little endian CPU:
            we want result: 33221100

            i0 = UNSAFE.getInt(page0, pageSize-4)  = 221100aa
            i1 = UNSAFE.getInt(page1, 0)           = ddccbb33
            res = 33221100 => (i0 >> 8) | (i1 << 24) => (i0 >>> i1Bits) | (i1 << (32 - i1Bits))

        big endian CPU:
            we want result: 00112233

            i0 = UNSAFE.getInt(page0, pageSize-4)  = aa001122
            i1 = UNSAFE.getInt(page1, 0)           = 33bbccdd
            res = 00112233 => (i0 << 8) | (i1 >> 24) => (i0 << i1Bits) | (i1 >>> (32 - i1Bits))
        */
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (i0 >>> i1Bits) | (i1 << (32 - i1Bits));
        } else {
            return (i0 << i1Bits) | (i1 >>> (32 - i1Bits));
        }
    }

    public static byte getByte0(int v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) v;
        } else {
            return (byte) (v >> 24);
        }
    }

    public static byte getByte1(int v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 8);
        } else {
            return (byte) (v >> 16);
        }
    }

    public static byte getByte2(int v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 16);
        } else {
            return (byte) (v >> 8);
        }
    }

    public static byte getByte3(int v) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 24);
        } else {
            return (byte) v;
        }
    }

    // ----------------------
    // -- short

    public static short asShort(byte b0, byte b1) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (short) ((b1 << 8) | (b0 & 0xff));
        } else {
            return (short) ((b0 << 8) | (b1 & 0xff));
        }
    }

    public static byte getByte0(short s) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) s;
        } else {
            return (byte) (s >> 8);
        }
    }

    public static byte getByte1(short s) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (s >> 8);
        } else {
            return (byte) s;
        }
    }

    // ----------------------
    // -- char

    public static char asChar(byte b0, byte b1) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (char) ((b1 << 8) | (b0 & 0xff));
        } else {
            return (char) ((b0 << 8) | (b1 & 0xff));
        }
    }

    public static byte getByte0(char c) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) c;
        } else {
            return (byte) (c >> 8);
        }
    }

    public static byte getByte1(char c) {
        if (MemoryAccess.LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (c >> 8);
        } else {
            return (byte) c;
        }
    }
}
