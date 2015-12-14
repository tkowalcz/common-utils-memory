package pl.codewise.util.buffer;

import java.nio.ByteOrder;

public class FixedUnsafeNativeByteOrderByteBufferMemoryTest extends FixedByteBufferMemoryTestBase<FixedUnsafeNativeByteOrderByteBufferMemory> {

    @Override
    protected FixedUnsafeNativeByteOrderByteBufferMemory wrapByteArray(byte[] memoryBuffer) {
        return new FixedUnsafeNativeByteOrderByteBufferMemory(memoryBuffer);
    }

    @Override
    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.nativeOrder();
    }
}