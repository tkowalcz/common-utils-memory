package com.codewise.util.memory;

public class Memory {

    public static MutableMemory wrapOffHeapAddress(long addressOffset, long capacity) {
        return new FixedOffHeapByteBufferMemory(addressOffset, capacity);
    }

    public static MutableMemory wrapOffHeapAddressNativeByteOrder(long addressOffset, long capacity) {
        return new FixedOffHeapNativeByteOrderMutableMemory(addressOffset, capacity);
    }
}
