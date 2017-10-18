package com.codewise.util.buffer;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public interface ByteBufferBase<B extends ByteBufferBase> extends Comparable<ByteBufferBase<?>> {

    long capacity();

    long position();

    B position(long newPosition);

    default B position(int newPosition) {
        return position((long) newPosition);
    }

    long limit();

    B limit(long newLimit);

    default B limit(int newLimit) {
        return limit((long) newLimit);
    }

    long remaining();

    boolean hasRemaining();

    default B clear() {
        resetAtPosition(0L);
        return (B) this;
    }

    B resetAtPosition(long position);

    default B resetAtPosition(int position) {
        return resetAtPosition((long) position);
    }

    default B flip() {
        limit(position());
        position(0);
        return (B) this;
    }

    B sliceOf(ByteBufferBase<?> source);

    B sliceMe();

    B duplicateOf(ByteBufferBase<?> source);

    B free();

    Supplier<B> uninitializedBufferFactory();
}
