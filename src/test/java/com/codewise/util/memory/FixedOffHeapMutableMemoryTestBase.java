package com.codewise.util.memory;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import pl.codewise.test.utils.MethodCall;
import pl.codewise.test.utils.MethodCallException;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;

public abstract class FixedOffHeapMutableMemoryTestBase<M extends MutableMemory> extends MutableMemoryTestBase<M> {

    protected ByteBuffer memoryBuffer;

    @Override
    protected M allocateBuffer(int size) {
        memoryBuffer = ByteBuffer.allocateDirect(size);
        return wrapByteArray(memoryBuffer);
    }

    protected abstract M wrapByteArray(ByteBuffer memoryBuffer);

    @Test(dataProvider = GET_METHODS)
    public void shouldUnderflowWhenGetNeedsBytesBeyondCapacity(MethodCall<MutableMemory> getMethod, Object value, int typeSize) {
        // given

        // when
        catchException(getMethod, MethodCallException.class).call(memory, memory.capacity() - 1 + typeSize);

        // then
        Assertions.assertThat((Throwable) caughtException()).hasCauseInstanceOf(BufferUnderflowException.class);
    }

    @Test(dataProvider = PUT_METHODS)
    public void shouldOverflowWhenPutTouchesBytesBeyondCapacity(MethodCall<MutableMemory> putMethod, Object value, int typeSize) {
        // given

        // when
        catchException(putMethod, MethodCallException.class).call(memory, memory.capacity() - 1 + typeSize, value);

        // then
        Assertions.assertThat((Throwable) caughtException()).hasCauseInstanceOf(BufferOverflowException.class);
    }

    @Test(expectedExceptions = BufferUnderflowException.class)
    public void shouldUnderflowWhenGetByteArrayFromOutsideOfBuffer() {
        // given
        byte[] buf = new byte[(int) memory.capacity() + 1];

        // when
        memory.get(Long.BYTES, buf, 0, buf.length);
    }

    @Test
    public void shouldNotUnderflowWhenGetByteArray() {
        // given
        setUpMemory();
        byte[] buf = new byte[(int) memory.capacity() + 1];

        // when
        memory.get(0, buf, 0, buf.length);

        // then
        Assertions.assertThat(buf).startsWith(TEST_BYTES).endsWith((byte) 0);
    }

    @Test(expectedExceptions = BufferOverflowException.class)
    public void shouldOverflowWhenPutByteArray() {
        // given
        byte[] buf = new byte[(int) memory.capacity() + 1];

        // when
        memory.put(0, buf, 0, buf.length);
    }

    @Override
    protected byte getByteDirect(int idx) {
        return memoryBuffer.get(idx);
    }

    @Override
    protected void putByteDirect(int idx, byte b) {
        memoryBuffer.put(idx, b);
    }
}