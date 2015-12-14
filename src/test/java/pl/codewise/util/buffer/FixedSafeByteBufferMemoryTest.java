package pl.codewise.util.buffer;

public class FixedSafeByteBufferMemoryTest extends FixedByteBufferMemoryTestBase<FixedSafeByteBufferMemory> {

    @Override
    protected FixedSafeByteBufferMemory wrapByteArray(byte[] memoryBuffer) {
        return new FixedSafeByteBufferMemory(memoryBuffer);
    }
}
