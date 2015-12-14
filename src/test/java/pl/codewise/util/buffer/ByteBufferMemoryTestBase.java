package pl.codewise.util.buffer;

import org.assertj.core.api.AbstractByteArrayAssert;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.codewise.test.utils.MethodCall;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.codewise.test.utils.MethodCallUtils.methodForCall;

public abstract class ByteBufferMemoryTestBase<M extends AbstractByteBufferMemory> {

    public static final String PUT_METHODS = "putMethods";
    public static final String GET_METHODS = "getMethods";

    public static final byte[] TEST_BYTES = {0x12, 0x23, 0x34, 0x45, 0x56, 0x67, 0x78, (byte) 0x89}; // RandomUtils.nextBytes(Long.BYTES);
    public static final java.nio.ByteBuffer TEST_BYTES_AS_BUFFER = ByteBuffer.wrap(TEST_BYTES);

    protected M memory;

    protected abstract M allocateBuffer(int size);

    protected abstract byte getByteDirect(int idx);

    protected abstract void putByteDirect(int idx, byte b);

    @BeforeMethod
    protected void setUp() {
        memory = allocateBuffer(Long.BYTES);
    }

    protected ByteOrder getBufferByteOrder() {
        return ByteOrder.BIG_ENDIAN;
    }

    public void clearMemory() {
        for (int idx = 0; idx < Long.BYTES; idx++) {
            putByteDirect(idx, (byte) 0);
        }
        TEST_BYTES_AS_BUFFER.position(0);
    }

    public void setUpMemory() {
        for (int idx = 0; idx < Long.BYTES; idx++) {
            putByteDirect(idx, TEST_BYTES[idx]);
        }
        TEST_BYTES_AS_BUFFER.position(0);
    }

    @DataProvider(name = PUT_METHODS)
    public Object[][] putMethods() {
        return new Object[][]{
                {methodForCall((ByteBufferMemory m) -> m.put(0, (byte) 0)), TEST_BYTES[0], Byte.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putChar(0, 'a')), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getChar(0), Character.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putShort(0, (short) 0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getShort(0), Short.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putInt(0, 0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getInt(0), Integer.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putLong(0, 0l)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getLong(0), Long.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putDouble(0, 0.0d)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getDouble(0), Double.BYTES},
        };
    }

    @DataProvider(name = GET_METHODS)
    public Object[][] getMethods() {
        return new Object[][]{
                {methodForCall((ByteBufferMemory m) -> m.get(0)), TEST_BYTES[0], Byte.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.getChar(0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getChar(0), Character.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.getShort(0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getShort(0), Short.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.getInt(0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getInt(0), Integer.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.getLong(0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getLong(0), Long.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.getDouble(0)), TEST_BYTES_AS_BUFFER.order(getBufferByteOrder()).getDouble(0), Double.BYTES},
        };
    }

    @Test(dataProvider = PUT_METHODS)
    public void shouldPut(MethodCall<ByteBufferMemory> putMethod, Object value, int typeSize) {
        // given
        clearMemory();

        // when
        putMethod.call(memory, 0, value);

        // then
        assertThatMemory(memory).startsWith(Arrays.copyOfRange(TEST_BYTES, 0, typeSize));
        if (memory.capacity() > typeSize) {
            assertThatMemory(memory).endsWith(zeros(memory.capacity() - typeSize));
        }
    }

    @Test(dataProvider = GET_METHODS)
    public void shoudGet(MethodCall<ByteBufferMemory> getMethod, Object expected, int typeSize) {
        // given
        setUpMemory();

        // when
        Object actual = getMethod.call(memory, 0);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPutByteArray() {
        // given
        clearMemory();

        // when
        memory.put(0, TEST_BYTES, 0, 6);

        // then
        assertThatMemory(memory)
                .startsWith(Arrays.copyOfRange(TEST_BYTES, 0, 6))
                .endsWith(zeros(memory.capacity() - 6));
    }

    @Test
    public void shouldGetByteArray() {
        // given
        setUpMemory();
        int index = Long.BYTES - 6;

        // when
        byte[] actual = new byte[6];
        memory.get(index, actual, 0, 6);

        // then
        byte[] expected = new byte[6];
        TEST_BYTES_AS_BUFFER.position(index);
        TEST_BYTES_AS_BUFFER.get(expected, 0, 6);
        TEST_BYTES_AS_BUFFER.position(0);
        assertThat(actual).containsExactly(expected);
    }

    @Test
    public void shouldGetByteBuffer() {
        // given
        setUpMemory();
        int index = Long.BYTES - 6;

        // when
        ByteBuffer actual = ByteBuffer.allocate(6);
        memory.get(index, actual);

        // then
        byte[] expected = new byte[6];
        TEST_BYTES_AS_BUFFER.position(index);
        TEST_BYTES_AS_BUFFER.get(expected, 0, 6);
        TEST_BYTES_AS_BUFFER.position(0);
        assertThat(actual.array()).containsExactly(expected);
    }


    protected AbstractByteArrayAssert<?> assertThatMemory(M memory) {
        int capacity = memory.capacity;
        byte[] currentBytes = new byte[capacity];
        for (int idx = 0; idx < capacity; idx++) {
            currentBytes[idx] = getByteDirect(idx);
        }
        return Assertions.assertThat(currentBytes);
    }

    public static byte[] zeros(int count) {
        return new byte[count];
    }
}