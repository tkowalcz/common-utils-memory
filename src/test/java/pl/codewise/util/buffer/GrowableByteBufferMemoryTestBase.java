package pl.codewise.util.buffer;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;
import pl.codewise.test.utils.MethodCall;
import pl.codewise.test.utils.MethodCallException;

import java.nio.BufferUnderflowException;
import java.util.Arrays;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.codewise.util.buffer.GrowableUnsafeByteBufferMemory.CAPACITY_INCREMENT_GRANULARITY;

public abstract class GrowableByteBufferMemoryTestBase<M extends AbstractByteBufferMemory> extends ByteBufferMemoryTestBase<M> {

    @Test(enabled = MemoryAccess.RANGE_CHECKS, dataProvider = GET_METHODS)
    public void shouldUnderflowWhenGetNeedsBytesBeyondCapacity(MethodCall<ByteBufferMemory> getMethod, Object value, int typeSize) {
        // given

        // when
        catchException(getMethod, MethodCallException.class).call(memory, memory.capacity() - 1 + typeSize);

        // then
        Assertions.assertThat((Throwable) caughtException()).hasCauseInstanceOf(BufferUnderflowException.class);
    }

    @Test(dataProvider = PUT_METHODS)
    public void shouldGrowWhenPutTouchesBytesBeyondCapacity(MethodCall<ByteBufferMemory> putMethod, Object value, int typeSize) {
        // given

        // when
        putMethod.call(memory, CAPACITY_INCREMENT_GRANULARITY, value);

        // then
        assertThat(memory.capacity()).isEqualTo(2 * CAPACITY_INCREMENT_GRANULARITY);
        assertThatMemory(memory)
                .startsWith(zeros(CAPACITY_INCREMENT_GRANULARITY))
                .containsSubsequence(Arrays.copyOfRange(TEST_BYTES, 0, typeSize))
                .endsWith(zeros(CAPACITY_INCREMENT_GRANULARITY - typeSize));
    }

    @Test(expectedExceptions = BufferUnderflowException.class, enabled = MemoryAccess.RANGE_CHECKS)
    public void shouldUnderflowWhenGetByteArrayFromOutsideOfBuffer() {
        // given
        byte[] buf = new byte[memory.capacity() + 1];

        // when
        memory.get(Long.BYTES, buf, 0, buf.length);
    }

    @Test
    public void shouldNotUnderflowWhenGetByteArray() {
        // given
        setUpMemory();
        byte[] buf = new byte[memory.capacity() + 1];

        // when
        memory.get(0, buf, 0, buf.length);

        // then
        assertThat(buf).startsWith(TEST_BYTES).endsWith((byte)0);
    }

    @Test
    public void shouldGrowWhenPutByteArray() {
        // given

        // when
        memory.put(CAPACITY_INCREMENT_GRANULARITY - 1, TEST_BYTES, 0, TEST_BYTES.length);

        // then
        assertThatMemory(memory)
                .hasSize(2 * CAPACITY_INCREMENT_GRANULARITY)
                .startsWith(zeros(CAPACITY_INCREMENT_GRANULARITY - 1))
                .containsSubsequence(TEST_BYTES)
                .endsWith(zeros(CAPACITY_INCREMENT_GRANULARITY + 1 - TEST_BYTES.length));
    }
}