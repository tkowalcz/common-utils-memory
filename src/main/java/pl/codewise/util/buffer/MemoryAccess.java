package pl.codewise.util.buffer;

import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import sun.misc.Unsafe;

final class MemoryAccess {

    public static final boolean RANGE_CHECKS = true;
    public static final boolean UNSAFE_MEMORY_ACCESS = true;

    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            unsafe = null;
        }
        UNSAFE = unsafe;
    }

    public static final long ARRAY_OBJECT_BASE_OFFSET = Unsafe.ARRAY_OBJECT_BASE_OFFSET;
    public static final long ARRAY_OBJECT_INDEX_SCALE = Unsafe.ARRAY_OBJECT_INDEX_SCALE;
    public static final long ARRAY_BYTE_BASE_OFFSET = Unsafe.ARRAY_BYTE_BASE_OFFSET;

    private static final boolean LITTLE_ENDIAN_NATIVE_BYTE_ORDER = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;

    //---------------------
    //-- byte

    public static byte getByteUnsafe(Object memory, int index) {
        return UNSAFE.getByte(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    public static byte getByteUnsafe(Object memory, long offset) {
        return UNSAFE.getByte(memory, offset);
    }

    public static void setByteUnsafe(Object memory, int index, byte value) {
        UNSAFE.putByte(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    public static void setByteUnsafe(Object memory, long offset, byte value) {
        UNSAFE.putByte(memory, offset, value);
    }

    public static void copyBytesToUnsafe(long addressOffset, int index, byte[] src, int offset, int length) {
        UNSAFE.copyMemory(src, ARRAY_BYTE_BASE_OFFSET + offset, null, addressOffset + index, length);
    }

    public static void copyUnsafeToBytes(long addressOffset, int index, byte[] dst, int offset, int length) {
        UNSAFE.copyMemory(null, addressOffset + index, dst, ARRAY_BYTE_BASE_OFFSET + offset, length);
    }

    public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    //---------------------
    //-- char

    public static char getCharUnsafe(Object memory, int index) {
        return correctBites(getNativeByteOrderCharUnsafe(memory, index));
    }

    public static char getCharUnsafe(Object memory, long offset) {
        return correctBites(getNativeByteOrderCharUnsafe(memory, offset));
    }

    public static void setCharUnsafe(Object memory, int index, char value) {
        setNativeByteOrderCharUnsafe(memory, index, correctBites(value));
    }

    public static void setCharUnsafe(Object memory, long offset, char value) {
        setNativeByteOrderCharUnsafe(memory, offset, correctBites(value));
    }

    public static char getNativeByteOrderCharUnsafe(Object memory, int index) {
        return UNSAFE.getChar(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    public static char getNativeByteOrderCharUnsafe(Object memory, long offset) {
        return UNSAFE.getChar(memory, offset);
    }

    public static char getNativeByteOrderCharUnsafe(long offHeapAddress) {
        return UNSAFE.getChar(offHeapAddress);
    }

    public static void setNativeByteOrderCharUnsafe(Object memory, int index, char value) {
        UNSAFE.putChar(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    public static void setNativeByteOrderCharUnsafe(Object memory, long offset, char value) {
        UNSAFE.putChar(memory, offset, value);
    }

    public static void setNativeByteOrderCharUnsafe(long offHeapAddress, char value) {
        UNSAFE.putChar(offHeapAddress, value);
    }

    public static char getCharSafe(byte[] memory, int index) {
        return (char) ((memory[index++] << 8) | (memory[index] & 0xff));
    }

    public static void setCharSafe(byte[] memory, int index, char value) {
        memory[index] = (byte) (value >>> 8);
        memory[index + 1] = (byte) value;
    }

    //---------------------
    //-- short

    public static short getShortUnsafe(Object memory, int index) {
        return correctBites(getNativeByteOrderShortUnsafe(memory, index));
    }

    public static short getShortUnsafe(Object memory, long offset) {
        return correctBites(getNativeByteOrderShortUnsafe(memory, offset));
    }

    public static void setShortUnsafe(Object memory, int index, short value) {
        setNativeByteOrderShortUnsafe(memory, index, correctBites(value));
    }

    public static void setShortUnsafe(Object memory, long offset, short value) {
        setNativeByteOrderShortUnsafe(memory, offset, correctBites(value));
    }

    public static short getNativeByteOrderShortUnsafe(Object memory, int index) {
        return UNSAFE.getShort(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    public static short getNativeByteOrderShortUnsafe(Object memory, long offset) {
        return UNSAFE.getShort(memory, offset);
    }

    public static short getNativeByteOrderShortUnsafe(long offHeapAddress) {
        return UNSAFE.getShort(offHeapAddress);
    }

    public static void setNativeByteOrderShortUnsafe(Object memory, int index, short value) {
        UNSAFE.putShort(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    public static void setNativeByteOrderShortUnsafe(Object memory, long offset, short value) {
        UNSAFE.putShort(memory, offset, value);
    }

    public static void setNativeByteOrderShortUnsafe(long offHeapAddress, short value) {
        UNSAFE.putShort(offHeapAddress, value);
    }

    public static short getShortSafe(byte[] memory, int index) {
        return (short) ((memory[index++] << 8) | (memory[index] & 0xff));
    }

    public static void setShortSafe(byte[] memory, int index, short value) {
        memory[index] = (byte) (value >>> 8);
        memory[index + 1] = (byte) value;
    }

    //---------------------
    //-- int

    public static int getIntUnsafe(Object memory, int index) {
        return correctBites(getNativeByteOrderIntUnsafe(memory, index));
    }

    public static int getIntUnsafe(Object memory, long offset) {
        return correctBites(getNativeByteOrderIntUnsafe(memory, offset));
    }

    public static void setIntUnsafe(Object memory, int index, int value) {
        setNativeByteOrderIntUnsafe(memory, index, correctBites(value));
    }

    public static void setIntUnsafe(Object memory, long offset, int value) {
        setNativeByteOrderIntUnsafe(memory, offset, correctBites(value));
    }

    public static int getNativeByteOrderIntUnsafe(Object memory, int index) {
        return UNSAFE.getInt(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    public static int getNativeByteOrderIntUnsafe(Object memory, long offset) {
        return UNSAFE.getInt(memory, offset);
    }

    public static int getNativeByteOrderIntUnsafe(long offHeapAddress) {
        return UNSAFE.getInt(offHeapAddress);
    }

    public static void setNativeByteOrderIntUnsafe(Object memory, int index, int value) {
        UNSAFE.putInt(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    public static void setNativeByteOrderIntUnsafe(Object memory, long offset, int value) {
        UNSAFE.putInt(memory, offset, value);
    }

    public static void setNativeByteOrderIntUnsafe(long offHeapAddress, int value) {
        UNSAFE.putInt(offHeapAddress, value);
    }

    public static int getIntSafe(byte[] memory, int index) {
        return (memory[index++] & 0xff) << 24 |
                (memory[index++] & 0xff) << 16 |
                (memory[index++] & 0xff) << 8 |
                memory[index] & 0xff;
    }

    public static void setIntSafe(byte[] memory, int index, int value) {
        memory[index] = (byte) (value >>> 24);
        memory[index + 1] = (byte) (value >>> 16);
        memory[index + 2] = (byte) (value >>> 8);
        memory[index + 3] = (byte) value;
    }

    //---------------------
    //-- long

    public static long getLongUnsafe(Object memory, int index) {
        return correctBites(getNativeByteOrderLongUnsafe(memory, index));
    }

    public static long getLongUnsafe(Object memory, long offset) {
        return correctBites(getNativeByteOrderLongUnsafe(memory, offset));
    }

    public static void setLongUnsafe(Object memory, int index, long value) {
        setNativeByteOrderLongUnsafe(memory, index, correctBites(value));
    }

    public static void setLongUnsafe(Object memory, long offset, long value) {
        setNativeByteOrderLongUnsafe(memory, offset, correctBites(value));
    }

    public static long getNativeByteOrderLongUnsafe(Object memory, int index) {
        return UNSAFE.getLong(memory, ARRAY_BYTE_BASE_OFFSET + index);
    }

    public static long getNativeByteOrderLongUnsafe(Object memory, long offset) {
        return UNSAFE.getLong(memory, offset);
    }

    public static long getNativeByteOrderLongUnsafe(long offHeapAddress) {
        return UNSAFE.getLong(offHeapAddress);
    }

    public static void setNativeByteOrderLongUnsafe(Object memory, int index, long value) {
        UNSAFE.putLong(memory, ARRAY_BYTE_BASE_OFFSET + index, value);
    }

    public static void setNativeByteOrderLongUnsafe(Object memory, long offset, long value) {
        UNSAFE.putLong(memory, offset, value);
    }

    public static void setNativeByteOrderLongUnsafe(long offHeapAddress, long value) {
        UNSAFE.putLong(offHeapAddress, value);
    }

    public static long getLongSafe(byte[] memory, int index) {
        return ((long) memory[index++] & 0xff) << 56 |
                ((long) memory[index++] & 0xff) << 48 |
                ((long) memory[index++] & 0xff) << 40 |
                ((long) memory[index++] & 0xff) << 32 |
                ((long) memory[index++] & 0xff) << 24 |
                ((long) memory[index++] & 0xff) << 16 |
                ((long) memory[index++] & 0xff) << 8 |
                (long) memory[index] & 0xff;
    }

    public static void setLongSafe(byte[] memory, int index, long value) {
        memory[index] = (byte) (value >>> 56);
        memory[index + 1] = (byte) (value >>> 48);
        memory[index + 2] = (byte) (value >>> 40);
        memory[index + 3] = (byte) (value >>> 32);
        memory[index + 4] = (byte) (value >>> 24);
        memory[index + 5] = (byte) (value >>> 16);
        memory[index + 6] = (byte) (value >>> 8);
        memory[index + 7] = (byte) value;
    }

    public static Object getObjectUnsafe(Object memory, long offset) {
        return UNSAFE.getObject(memory, offset);
    }

    public static int correctBites(int x) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Integer.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static long correctBites(long x) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Long.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static short correctBites(short x) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Short.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static char correctBites(char x) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return Character.reverseBytes(x);
        } else {
            return x;
        }
    }

    public static long asLong(byte b0, byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (((long) b7) << 56) | ((b6 & 0xffL) << 48) | ((b5 & 0xffL) << 40) | ((b4 & 0xffL) << 32) | ((b3 & 0xffL) << 24) | ((b2 & 0xffL) << 16) | ((b1 & 0xffL) << 8) | (b0 & 0xffL);
        } else {
            return (((long) b0) << 56) | ((b1 & 0xffL) << 48) | ((b2 & 0xffL) << 40) | ((b3 & 0xffL) << 32) | ((b4 & 0xffL) << 24) | ((b5 & 0xffL) << 16) | ((b6 & 0xffL) << 8) | (b7 & 0xffL);
        }
    }

    public static long asLong(long l0, long l1, int l1Bits) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (l0 >>> l1Bits) | (l1 << (64 - l1Bits));
        } else {
            return (l0 << l1Bits) | (l1 >>> (64 - l1Bits));
        }
    }

    public static byte getByte0(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) v;
        } else {
            return (byte) (v >> 56);
        }
    }

    public static byte getByte1(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 8);
        } else {
            return (byte) (v >> 48);
        }
    }

    public static byte getByte2(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 16);
        } else {
            return (byte) (v >> 40);
        }
    }

    public static byte getByte3(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 24);
        } else {
            return (byte) (v >> 32);
        }
    }

    public static byte getByte4(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 32);
        } else {
            return (byte) (v >> 24);
        }
    }

    public static byte getByte5(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 40);
        } else {
            return (byte) (v >> 16);
        }
    }

    public static byte getByte6(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 48);
        } else {
            return (byte) (v >> 8);
        }
    }

    public static byte getByte7(long v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 56);
        } else {
            return (byte) v;
        }
    }

    public static int asInt(byte b0, byte b1, byte b2, byte b3) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
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
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (i0 >>> i1Bits) | (i1 << (32 - i1Bits));
        } else {
            return (i0 << i1Bits) | (i1 >>> (32 - i1Bits));
        }
    }

    public static byte getByte0(int v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) v;
        } else {
            return (byte) (v >> 24);
        }
    }

    public static byte getByte1(int v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 8);
        } else {
            return (byte) (v >> 16);
        }
    }

    public static byte getByte2(int v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 16);
        } else {
            return (byte) (v >> 8);
        }
    }

    public static byte getByte3(int v) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (v >> 24);
        } else {
            return (byte) v;
        }
    }

    public static short asShort(byte b0, byte b1) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (short) ((b1 << 8) | (b0 & 0xff));
        } else {
            return (short) ((b0 << 8) | (b1 & 0xff));
        }
    }

    public static byte getByte0(short s) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) s;
        } else {
            return (byte) (s >> 8);
        }
    }

    public static byte getByte1(short s) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (s >> 8);
        } else {
            return (byte) s;
        }
    }

    public static char asChar(byte b0, byte b1) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (char) ((b1 << 8) | (b0 & 0xff));
        } else {
            return (char) ((b0 << 8) | (b1 & 0xff));
        }
    }

    public static byte getByte0(char c) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) c;
        } else {
            return (byte) (c >> 8);
        }
    }

    public static byte getByte1(char c) {
        if (LITTLE_ENDIAN_NATIVE_BYTE_ORDER) {
            return (byte) (c >> 8);
        } else {
            return (byte) c;
        }
    }

    /**
     * Problem diagnostics helper class - delegates to sun.misc.Unsafe, performing additional checks.
     * In case of seg-fault errors one can use this class to verify if Unsafe calls are all operating on non-null arrays and
     * within array bounds.
     */
    private static class SafeUnsafe {
        private static final Unsafe DELEGATE;

        static {
            Unsafe unsafe;
            try {
                Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe) unsafeField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                unsafe = null;
            }
            DELEGATE = unsafe;
        }

        private void checkPreconditions(Object o, long l, Consumer<Class<?>> forByteArray, Consumer<Class<?>> forTwoDimByteArray, Consumer<Class<?>> forOtherType) {
            Preconditions.checkNotNull(o);
            Class<?> oClass = o.getClass();
            if (oClass.isArray() && oClass.getComponentType() == byte.class) {
                byte[] array = (byte[]) o;
                Preconditions.checkArgument(array.length > 0, "ByteBuffer memory page has length of 0");
                long arrayIndex = l - Unsafe.ARRAY_BYTE_BASE_OFFSET;
                Preconditions.checkArgument(arrayIndex >= 0 && arrayIndex < array.length, "ByteBuffer memory operation index expected to be between 0 and %d, but is: %d", array.length, arrayIndex);
                forByteArray.accept(oClass.getComponentType());
            } else if (oClass.isArray() && oClass.getComponentType().isArray() && oClass.getComponentType().getComponentType() == byte.class) {
                byte[][] array = (byte[][]) o;
                Preconditions.checkArgument(array.length > 0, "ByteBuffer memory pages array has length of 0");
                long arrayIndex = (l - Unsafe.ARRAY_OBJECT_BASE_OFFSET) / Unsafe.ARRAY_OBJECT_INDEX_SCALE;
                Preconditions.checkArgument(arrayIndex >= 0 && arrayIndex < array.length, "ByteBuffers memoty pages operation index expected to be between 0 and %d, but is: %d", array.length, arrayIndex);
                forTwoDimByteArray.accept(oClass.getComponentType());
            } else {
                forOtherType.accept(oClass);
            }
        }

        public int getInt(Object o, long l) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getInt called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getInt called on " + c.getName());
            });
            return DELEGATE.getInt(o, l);
        }

        public void putInt(Object o, long l, int i) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putInt called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putInt called on " + c.getName());
            });
            DELEGATE.putInt(o, l, i);
        }

        public Object getObject(Object o, long l) {
            checkPreconditions(o, l, c -> {
                throw new UnsupportedOperationException("UNSAFE.getObject called on byte[]");
            }, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getObject called on " + c.getName());
            });
            return DELEGATE.getObject(o, l);
        }

        public void putObject(Object o, long l, Object o1) {
            checkPreconditions(o, l, c -> {
                throw new UnsupportedOperationException("UNSAFE.putObject called on byte[][]");
            }, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putObject called on " + c.getName());
            });
            DELEGATE.putObject(o, l, o1);
        }

        public byte getByte(Object o, long l) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getByte called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getByte called on " + c.getName());
            });
            return DELEGATE.getByte(o, l);
        }

        public void putByte(Object o, long l, byte b) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putByte called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putByte called on " + c.getName());
            });
            DELEGATE.putByte(o, l, b);
        }

        public short getShort(Object o, long l) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getShort called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getShort called on " + c.getName());
            });
            return DELEGATE.getShort(o, l);
        }

        public void putShort(Object o, long l, short i) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putShort called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putShort called on " + c.getName());
            });
            DELEGATE.putShort(o, l, i);
        }

        public char getChar(Object o, long l) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getChar called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getChar called on " + c.getName());
            });
            return DELEGATE.getChar(o, l);
        }

        public void putChar(Object o, long l, char c) {
            checkPreconditions(o, l, c1 -> {
            }, c1 -> {
                throw new UnsupportedOperationException("UNSAFE.putChar called on byte[][]");
            }, c1 -> {
                throw new UnsupportedOperationException("UNSAFE.putChar called on " + c1.getName());
            });
            DELEGATE.putChar(o, l, c);
        }

        public long getLong(Object o, long l) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getLong called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.getLong called on " + c.getName());
            });
            return DELEGATE.getLong(o, l);
        }

        public void putLong(Object o, long l, long l1) {
            checkPreconditions(o, l, c -> {
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putLong called on byte[][]");
            }, c -> {
                throw new UnsupportedOperationException("UNSAFE.putLong called on " + c.getName());
            });
            DELEGATE.putLong(o, l, l1);
        }
    }
}
