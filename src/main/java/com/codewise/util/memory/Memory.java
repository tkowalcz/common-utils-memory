package com.codewise.util.memory;

public class Memory {

    public static OffHeapMutableMemory wrapOffHeapAddress(long addressOffset, long capacity) {
        return new FixedOffHeapByteBufferMemory(addressOffset, capacity);
    }

    public static OffHeapMutableMemory wrapOffHeapAddressNativeByteOrder(long addressOffset, long capacity) {
        return new FixedOffHeapNativeByteOrderMutableMemory(addressOffset, capacity);
    }
}
