package pl.codewise.util.buffer;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

@SuppressWarnings("unchecked")
abstract class AbstractByteBuffer<B extends ByteBufferBase<B>> implements ByteBufferBase<B> {

    static final int LIMIT_AT_CAPACITY = -1;

    protected boolean initialized = false;

    protected ByteBufferMemory memory;
    protected int baseOffset;
    protected int position;
    protected int limit = LIMIT_AT_CAPACITY;

    protected AbstractByteBuffer() {
    }

    protected AbstractByteBuffer(ByteBufferMemory memory) {
        wrapMemory(memory);
    }

    protected void wrapMemory(ByteBufferMemory memory) {
        this.memory = memory;
        this.baseOffset = 0;
        this.position = 0;
        this.initialized = true;
    }

    protected B withBaseOffset(int baseOffset) {
        this.baseOffset = baseOffset;
        return (B) this;
    }

    @Override
    public int capacity() {
        assert initialized : "Buffer not initialized";
        return memory.capacity() - baseOffset;
    }

    @Override
    public int position() {
        assert initialized : "Buffer not initialized";
        return position;
    }

    @Override
    public B position(int newPosition) {
        assert initialized : "Buffer not initialized";
        if (newPosition < 0 || newPosition > limit()) {
            throw new IllegalArgumentException();
        }
        position = newPosition;
        return (B) this;
    }

    @Override
    public int limit() {
        assert initialized : "Buffer not initialized";
        return limit == LIMIT_AT_CAPACITY ? capacity() : limit;
    }

    @Override
    public boolean isLimitAtCapacity() {
        return limit == LIMIT_AT_CAPACITY;
    }

    @Override
    public B limit(int newLimit) {
        assert initialized : "Buffer not initialized";
        if (newLimit < 0) {
            throw new IllegalArgumentException();
        } else {
            int capacity = capacity();
            if (newLimit < capacity) {
                limit = newLimit;
            } else if (newLimit == capacity) {
                limit = LIMIT_AT_CAPACITY;
            } else {
                throw new IllegalArgumentException();
            }
            if (position > newLimit) position = newLimit;
        }
        return (B) this;
    }

    @Override
    public B resetAtPosition(int position) {
        limit = LIMIT_AT_CAPACITY;
        this.position = position;

        return (B) this;
    }

    @Override
    public int remaining() {
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
        this.limit = that.limit == LIMIT_AT_CAPACITY ? LIMIT_AT_CAPACITY : that.remaining();
        this.initialized = true;

        return (B) this;
    }

    @Override
    public B sliceMe() {
        assert initialized;

        int oldPosition = position;
        this.baseOffset += oldPosition;
        this.position = 0;

        if (this.limit != LIMIT_AT_CAPACITY) {
            this.limit = this.limit - oldPosition;
        }

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
        limit = LIMIT_AT_CAPACITY;
        position = 0;
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

        int commonLength = Math.min(this.remaining(), that.remaining());
        int result = this.memory.compare(getOffset(), that.memory, that.getOffset(), commonLength);
        if (result == 0) {
            return this.remaining() - that.remaining();
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

    int getOffset() {
        assert initialized : "Buffer not initialized";
        return baseOffset + position;
    }

    int getOffset(int index) {
        assert initialized : "Buffer not initialized";
        return baseOffset + index;
    }

    void getRangeCheck(int index, int size) {
        assert initialized : "Buffer not initialized";
        assert index >= 0 && size >= 0 : "Index and size must be greater than or equal to zero";
        if (index + size > limit()) {
            throw new BufferUnderflowException();
        }
    }

    void putRangeCheck(int index, int size) {
        assert initialized : "Buffer not initialized";
        assert index >= 0 && size >= 0 : "Index and size must be greater than or equal to zero";
        // check only if limit is not set at memory capacity
        // otherwise - memory implementation range check will be applied when doing put...
        if (limit != LIMIT_AT_CAPACITY && index + size > limit) {
            throw new BufferOverflowException();
        }
//        if (!memory.canExpand() || limit != LIMIT_AT_CAPACITY) {
//            if (index + size > limit) {
//                throw new BufferOverflowException();
//            }
//        }
    }
}
