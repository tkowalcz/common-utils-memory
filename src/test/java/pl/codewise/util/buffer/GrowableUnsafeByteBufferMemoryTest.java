package pl.codewise.util.buffer;

public class GrowableUnsafeByteBufferMemoryTest extends GrowableByteBufferMemoryTestBase<GrowableUnsafeByteBufferMemory> {

    @Override
    protected GrowableUnsafeByteBufferMemory allocateBuffer(int size) {
        return new GrowableUnsafeByteBufferMemory(size);
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
