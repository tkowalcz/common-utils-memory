package com.codewise.util.lowlevel;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteOrder;

public final class MemoryAccess {

    public static final boolean RANGE_CHECKS = true;
    public static final boolean UNSAFE_MEMORY_ACCESS = true;

    public static final boolean LITTLE_ENDIAN_NATIVE_BYTE_ORDER = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;

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

    public static byte getByteSafe(byte[] memory, int index) {
        return memory[index];
    }

    public static void setByteSafe(byte[] memory, int index, byte value) {
        memory[index] = value;
    }

    //---------------------
    //-- char

    public static char getCharUnsafe(Object memory, int index) {
        return Bits.correctBites(getNativeByteOrderCharUnsafe(memory, index));
    }

    public static char getCharUnsafe(Object memory, long offset) {
        return Bits.correctBites(getNativeByteOrderCharUnsafe(memory, offset));
    }

    public static void setCharUnsafe(Object memory, int index, char value) {
        setNativeByteOrderCharUnsafe(memory, index, Bits.correctBites(value));
    }

    public static void setCharUnsafe(Object memory, long offset, char value) {
        setNativeByteOrderCharUnsafe(memory, offset, Bits.correctBites(value));
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

    public static char getNativeByteOrderCharSafe(byte[] memory, int index) {
        return (char) ((memory[index++] & 0xff) | (memory[index] << 8));
    }

    public static void setNativeByteOrderCharSafe(byte[] memory, int index, char value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
    }

    //---------------------
    //-- short

    public static short getShortUnsafe(Object memory, int index) {
        return Bits.correctBites(getNativeByteOrderShortUnsafe(memory, index));
    }

    public static short getShortUnsafe(Object memory, long offset) {
        return Bits.correctBites(getNativeByteOrderShortUnsafe(memory, offset));
    }

    public static void setShortUnsafe(Object memory, int index, short value) {
        setNativeByteOrderShortUnsafe(memory, index, Bits.correctBites(value));
    }

    public static void setShortUnsafe(Object memory, long offset, short value) {
        setNativeByteOrderShortUnsafe(memory, offset, Bits.correctBites(value));
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

    public static short getNativeByteOrderShortSafe(byte[] memory, int index) {
        return (short) ((memory[index++] & 0xff) | (memory[index] << 8));
    }

    public static void setNativeByteOrderShortSafe(byte[] memory, int index, short value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
    }

    //---------------------
    //-- int

    public static int getIntUnsafe(Object memory, int index) {
        return Bits.correctBites(getNativeByteOrderIntUnsafe(memory, index));
    }

    public static int getIntUnsafe(Object memory, long offset) {
        return Bits.correctBites(getNativeByteOrderIntUnsafe(memory, offset));
    }

    public static void setIntUnsafe(Object memory, int index, int value) {
        setNativeByteOrderIntUnsafe(memory, index, Bits.correctBites(value));
    }

    public static void setIntUnsafe(Object memory, long offset, int value) {
        setNativeByteOrderIntUnsafe(memory, offset, Bits.correctBites(value));
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

    public static int getNativeByteOrderIntSafe(byte[] memory, int index) {
        return memory[index++] & 0xff |
                (memory[index++] & 0xff) << 8 |
                (memory[index++] & 0xff) << 16 |
                (memory[index] & 0xff) << 24;
    }

    public static void setNativeByteOrderIntSafe(byte[] memory, int index, int value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
        memory[index + 2] = (byte) (value >>> 16);
        memory[index + 3] = (byte) (value >>> 24);
    }

    //---------------------
    //-- long

    public static long getLongUnsafe(Object memory, int index) {
        return Bits.correctBites(getNativeByteOrderLongUnsafe(memory, index));
    }

    public static long getLongUnsafe(Object memory, long offset) {
        return Bits.correctBites(getNativeByteOrderLongUnsafe(memory, offset));
    }

    public static void setLongUnsafe(Object memory, int index, long value) {
        setNativeByteOrderLongUnsafe(memory, index, Bits.correctBites(value));
    }

    public static void setLongUnsafe(Object memory, long offset, long value) {
        setNativeByteOrderLongUnsafe(memory, offset, Bits.correctBites(value));
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

    public static long getNativeByteOrderLongSafe(byte[] memory, int index) {
        return (long) memory[index++] & 0xff |
                ((long) memory[index++] & 0xff) << 8 |
                ((long) memory[index++] & 0xff) << 16 |
                ((long) memory[index++] & 0xff) << 24 |
                ((long) memory[index++] & 0xff) << 32 |
                ((long) memory[index++] & 0xff) << 40 |
                ((long) memory[index++] & 0xff) << 48 |
                ((long) memory[index] & 0xff) << 56;
    }

    public static void setNativeByteOrderLongSafe(byte[] memory, int index, long value) {
        memory[index] = (byte) value;
        memory[index + 1] = (byte) (value >>> 8);
        memory[index + 2] = (byte) (value >>> 16);
        memory[index + 3] = (byte) (value >>> 24);
        memory[index + 4] = (byte) (value >>> 32);
        memory[index + 5] = (byte) (value >>> 40);
        memory[index + 6] = (byte) (value >>> 48);
        memory[index + 7] = (byte) (value >>> 56);
    }

    //---------------------
    //-- float
    public static void setNativeByteOrderFloatUnsafe(Object memory, long offset, float value) {
        UNSAFE.putFloat(memory, offset, value);
    }

    public static float getNativeByteOrderFloatUnsafe(Object memory, long offset) {
        return UNSAFE.getFloat(memory, offset);
    }

    //---------------------
    //-- double
    public static void setNativeByteOrderDoubleUnsafe(Object memory, long offset, double value) {
        UNSAFE.putDouble(memory, offset, value);
    }

    public static double getNativeByteOrderDoubleUnsafe(Object memory, long offset) {
        return UNSAFE.getDouble(memory, offset);
    }

    //---------------------
    //-- Object

    public static Object getObjectUnsafe(Object memory, long offset) {
        return UNSAFE.getObject(memory, offset);
    }

    // --------------------
    // -- copy mem

    public static void copyMemoryUnsafe(Object mem1, long offset1, Object mem2, long offset2, long length) {
        UNSAFE.copyMemory(mem1, offset1, mem2, offset2, length);
    }

    public static void copyMemoryUnsafe(long address1, long address2, long length) {
        UNSAFE.copyMemory(address1, address2, length);
    }

    public static void copyMemorySafe(byte[] mem1, int index1, byte[] mem2, int index2, int length) {
        System.arraycopy(mem1, index1, mem2, index2, length);
    }
}
