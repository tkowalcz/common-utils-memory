package pl.codewise.util.buffer;

import java.nio.ByteOrder;

public class GrowableUnsafeNativeByteOrderByteBufferMemoryTest extends GrowableByteBufferMemoryTestBase<GrowableUnsafeNativeByteOrderByteBufferMemory> {

    @Override
    protected GrowableUnsafeNativeByteOrderByteBufferMemory allocateBuffer(int size) {
        return new GrowableUnsafeNativeByteOrderByteBufferMemory(size);
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }

    @Override
    protected byte getByteDirect(int idx) {
        return memory.memory[idx];
    }

    @Override
    protected void putByteDirect(int idx, byte b) {
        memory.memory[idx] = b;
    }
}