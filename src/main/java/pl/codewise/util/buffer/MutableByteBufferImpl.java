package pl.codewise.util.buffer;

import java.util.function.Supplier;

class MutableByteBufferImpl extends ReadOnlyByteBufferImpl<MutableByteBuffer> implements MutableByteBuffer {

    MutableByteBufferImpl() {
    }

    MutableByteBufferImpl(ByteBufferMemory memory) {
        super(memory);
    }

    @Override
    public MutableByteBuffer wrap(ByteBufferMemory memory) {
        wrapMemory(memory);
        return this;
    }

    @Override
    public MutableByteBuffer put(byte b) {
        putRangeCheck(position, Byte.BYTES);
        memory.put(getOffset(), b);
        position += Byte.BYTES;
        return this;
    }

    @Override
    public MutableByteBuffer put(int index, byte b) {
        putRangeCheck(index, Byte.BYTES);
        memory.put(getOffset(index), b);
        return this;
    }

    @Override
    public MutableByteBuffer put(byte[] src, int offset, int length) {
        putRangeCheck(position, length);
        memory.put(getOffset(), src, offset, length);
        position += length;
        return this;
    }

    @Override
    public MutableByteBuffer put(ReadOnlyByteBuffer src) {
        assert src instanceof AbstractByteBuffer;
        int bytesToCopy = src.remaining();
        putRangeCheck(position, bytesToCopy);
        AbstractByteBuffer source = (AbstractByteBuffer) src;
        memory.put(getOffset(), source.memory, source.getOffset(), bytesToCopy);
        position += bytesToCopy;
        src.position(src.position() + bytesToCopy);
        return this;
    }

    @Override
    public MutableByteBuffer putChar(char value) {
        putRangeCheck(position, Character.BYTES);
        memory.putChar(getOffset(), value);
        position += Character.BYTES;
        return this;
    }

    @Override
    public MutableByteBuffer putChar(int index, char value) {
        putRangeCheck(index, Character.BYTES);
        memory.putChar(getOffset(index), value);
        return this;
    }

    @Override
    public MutableByteBuffer putShort(short value) {
        putRangeCheck(position, Short.BYTES);
        memory.putShort(getOffset(), value);
        position += Character.BYTES;
        return this;
    }

    @Override
    public MutableByteBuffer putShort(int index, short value) {
        putRangeCheck(index, Short.BYTES);
        memory.putShort(getOffset(index), value);
        return this;
    }

    @Override
    public MutableByteBuffer putInt(int value) {
        putRangeCheck(position, Integer.BYTES);
        memory.putInt(getOffset(), value);
        position += Integer.BYTES;
        return this;
    }

    @Override
    public MutableByteBuffer putInt(int index, int value) {
        putRangeCheck(index, Integer.BYTES);
        memory.putInt(getOffset(index), value);
        return this;
    }

    @Override
    public MutableByteBuffer putLong(long value) {
        putRangeCheck(position, Long.BYTES);
        memory.putLong(getOffset(), value);
        position += Long.BYTES;
        return this;
    }

    @Override
    public MutableByteBuffer putLong(int index, long value) {
        putRangeCheck(index, Long.BYTES);
        memory.putLong(getOffset(index), value);
        return this;
    }

    @Override
    public MutableByteBuffer putDouble(double value) {
        putRangeCheck(position, Double.BYTES);
        memory.putDouble(getOffset(), value);
        position += Double.BYTES;
        return this;
    }

    @Override
    public MutableByteBuffer putDouble(int index, double value) {
        putRangeCheck(index, Double.BYTES);
        memory.putDouble(getOffset(index), value);
        return this;
    }

    @Override
    public Supplier<MutableByteBuffer> uninitializedBufferFactory() {
        return MutableByteBufferImpl::new;
    }
}
