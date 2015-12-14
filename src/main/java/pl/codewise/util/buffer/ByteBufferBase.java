package pl.codewise.util.buffer;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public interface ByteBufferBase<B extends ByteBufferBase> extends Comparable<ByteBufferBase<?>> {

    int capacity();

    int position();

    B position(int newPosition);

    int limit();

    boolean isLimitAtCapacity();

    B limit(int newLimit);

    int remaining();

    boolean hasRemaining();

    default B clear() {
        resetAtPosition(0);
        return (B) this;
    }

    B resetAtPosition(int position);

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
