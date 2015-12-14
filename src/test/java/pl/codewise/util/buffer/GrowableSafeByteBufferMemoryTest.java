package pl.codewise.util.buffer;

public class GrowableSafeByteBufferMemoryTest extends GrowableByteBufferMemoryTestBase<GrowableSafeByteBufferMemory> {

    @Override
    protected GrowableSafeByteBufferMemory allocateBuffer(int size) {
        return new GrowableSafeByteBufferMemory(size);
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
