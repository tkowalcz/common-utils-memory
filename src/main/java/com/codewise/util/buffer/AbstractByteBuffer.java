package com.codewise.util.buffer;

import com.codewise.util.memory.MutableMemory;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

@SuppressWarnings("unchecked")
abstract class AbstractByteBuffer<B extends ByteBufferBase<B>> implements ByteBufferBase<B> {

    protected boolean initialized = false;

    protected MutableMemory memory;
    protected long baseOffset;
    protected long position;
    protected long limit;

    protected AbstractByteBuffer() {
    }

    protected AbstractByteBuffer(MutableMemory memory) {
        wrapMemory(memory);
    }

    protected void wrapMemory(MutableMemory memory) {
        this.memory = memory;
        this.baseOffset = 0;
        this.position = 0;
        this.initialized = true;
        this.limit = capacity();
    }

    protected B withBaseOffset(long baseOffset) {
        this.baseOffset = baseOffset;
        this.limit = capacity();
        return (B) this;
    }

    @Override
    public long capacity() {
        assert initialized : "Buffer not initialized";
        return memory.capacity() - baseOffset;
    }

    @Override
    public long position() {
        assert initialized : "Buffer not initialized";
        return position;
    }

    @Override
    public B position(long newPosition) {
        assert initialized : "Buffer not initialized";
        if (newPosition < 0 || newPosition > limit()) {
            throw new IllegalArgumentException();
        }
        position = newPosition;
        return (B) this;
    }

    @Override
    public long limit() {
        assert initialized : "Buffer not initialized";
        return limit;
    }

    @Override
    public B limit(long newLimit) {
        assert initialized : "Buffer not initialized";
        if (newLimit < 0) {
            throw new IllegalArgumentException();
        } else {
            long capacity = capacity();
            if (newLimit <= capacity) {
                limit = newLimit;
            } else {
                throw new IllegalArgumentException();
            }

            if (position > newLimit) {
                position = newLimit;
            }
        }
        return (B) this;
    }

    @Override
    public B resetAtPosition(long position) {
        limit = capacity();
        this.position = position;

        return (B) this;
    }

    @Override
    public long remaining() {
        assert initialized : "Buffer not initialized";
        return limit() - position;
    }

    @Override
    public boolean hasRemaining() {
        assert initialized : "Buffer not initialized";
        return position < limit();
    }

    @Override
    public B sliceOf(ByteBufferBase<?> source) {
        assert source instanceof AbstractByteBuffer;
        AbstractByteBuffer<?> that = (AbstractByteBuffer<?>) source;

        this.memory = that.memory;
        this.baseOffset = that.baseOffset + that.position;
        this.position = 0;
        this.limit = that.remaining();
        this.initialized = true;

        return (B) this;
    }

    @Override
    public B sliceMe() {
        assert initialized;

        long oldPosition = position;
        this.baseOffset += oldPosition;
        this.position = 0;

        this.limit = this.limit - oldPosition;

        return (B) this;
    }

    @Override
    public B duplicateOf(ByteBufferBase<?> source) {
        assert source instanceof AbstractByteBuffer;
        AbstractByteBuffer<?> that = (AbstractByteBuffer<?>) source;

        this.memory = that.memory;
        this.baseOffset = that.baseOffset;
        this.limit = that.limit;
        this.position = that.position;
        this.initialized = true;

        return (B) this;
    }

    @Override
    public B free() {
        memory = null;
        baseOffset = 0;
        position = 0;
        limit = 0;
        initialized = false;

        return (B) this;
    }

    @Override
    public int compareTo(ByteBufferBase<?> o) {
        if (this == o) {
            return 0;
        }

        assert initialized : "Buffer not initialized";

        assert o instanceof AbstractByteBuffer;
        AbstractByteBuffer<?> that = (AbstractByteBuffer<?>) o;

        long commonLength = Math.min(this.remaining(), that.remaining());
        int result = this.memory.compare(getOffset(), that.memory, that.getOffset(), commonLength);
        if (result == 0) {
            return Long.compare(this.remaining(), that.remaining());
        } else {
            return result;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AbstractByteBuffer)) {
            return false;
        }

        AbstractByteBuffer that = (AbstractByteBuffer) obj;
        if (!this.initialized && !that.initialized) {
            return true;
        } else if (this.initialized && that.initialized) {
            if (this.remaining() != that.remaining()) {
                return false;
            } else {
                return this.memory.compare(getOffset(), that.memory, that.getOffset(), this.remaining()) == 0;
            }
        } else {
            return false;
        }
    }

    long getOffset() {
        assert initialized : "Buffer not initialized";
        return baseOffset + position;
    }

    long getOffset(long index) {
        assert initialized : "Buffer not initialized";
        return baseOffset + index;
    }

    void getRangeCheck(long index, long size) {
        assert initialized : "Buffer not initialized";
        assert index >= 0 && size >= 0 : "Index and size must be greater than or equal to zero";
        if (index + size > limit()) {
            throw new BufferUnderflowException();
        }
    }

    void putRangeCheck(long index, long size) {
        assert initialized : "Buffer not initialized";
        assert index >= 0 && size >= 0 : "Index and size must be greater than or equal to zero";

        // check only if limit is not set at memory capacity
        // otherwise - memory implementation range check will be applied when doing put...
        if (index + size > limit) {
            throw new BufferOverflowException();
        }
    }
}
