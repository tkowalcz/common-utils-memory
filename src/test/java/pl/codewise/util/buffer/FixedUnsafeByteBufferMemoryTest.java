package pl.codewise.util.buffer;

public class FixedUnsafeByteBufferMemoryTest extends FixedByteBufferMemoryTestBase<FixedUnsafeByteBufferMemory> {

    @Override
    protected FixedUnsafeByteBufferMemory wrapByteArray(byte[] memoryBuffer) {
        return new FixedUnsafeByteBufferMemory(memoryBuffer);
    }
}
