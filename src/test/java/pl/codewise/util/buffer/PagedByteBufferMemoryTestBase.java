package pl.codewise.util.buffer;

import com.googlecode.catchexception.CatchException;
import org.assertj.core.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.codewise.test.utils.MethodCall;
import pl.codewise.test.utils.MethodCallException;

import java.nio.BufferUnderflowException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.googlecode.catchexception.CatchException.catchException;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.codewise.test.utils.MethodCallUtils.methodForCall;

public abstract class PagedByteBufferMemoryTestBase<M extends AbstractPagedByteBufferMemory> extends ByteBufferMemoryTestBase<M> {

    public static final String PUT_AND_GET_METHODS = "putAndGetMethods";
    public static final int PAGE_SIZE = 0x100;

    @Override
    protected M allocateBuffer(int size) {
        return newMemory(4, Integer.numberOfTrailingZeros(PAGE_SIZE), size);
    }

    protected abstract M newMemory(int pageCntGrow, int pageSizeBits, int initialCapacity);

    @Override
    protected byte getByteDirect(int idx) {
        byte[][] pages = (byte[][]) memory.memory;
        return pages[idx / PAGE_SIZE][idx % PAGE_SIZE];
    }

    @Override
    protected void putByteDirect(int idx, byte b) {
        byte[][] pages = (byte[][]) memory.memory;
        pages[idx / PAGE_SIZE][idx % PAGE_SIZE] = b;
    }

    @DataProvider(name = PUT_AND_GET_METHODS)
    public Object[][] putAndGetMethods() {
        return new Object[][]{
                {methodForCall((ByteBufferMemory m) -> m.put(0, (byte) 0)), methodForCall((ByteBufferMemory m) -> m.get(0)), TEST_BYTES[0], Byte.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putChar(0, 'a')), methodForCall((ByteBufferMemory m) -> m.getChar(0)), TEST_BYTES_AS_BUFFER.getChar(0), Character.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putShort(0, (short) 0)), methodForCall((ByteBufferMemory m) -> m.getShort(0)), TEST_BYTES_AS_BUFFER.getShort(0), Short.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putInt(0, 0)), methodForCall((ByteBufferMemory m) -> m.getInt(0)), TEST_BYTES_AS_BUFFER.getInt(0), Integer.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putLong(0, 0l)), methodForCall((ByteBufferMemory m) -> m.getLong(0)), TEST_BYTES_AS_BUFFER.getLong(0), Long.BYTES},
                {methodForCall((ByteBufferMemory m) -> m.putDouble(0, 0.0d)), methodForCall((ByteBufferMemory m) -> m.getDouble(0)), TEST_BYTES_AS_BUFFER.getDouble(0), Double.BYTES},
        };
    }

    @Test(dataProvider = PUT_AND_GET_METHODS)
    public void shouldGetFromEndOfLastPage(MethodCall<ByteBufferMemory> putMethod, MethodCall<ByteBufferMemory> getMethod, Object value, int typeSize) {
        // given
        final int pageSizeBits = 8;
        final int pageSize = 1 << pageSizeBits;

        AbstractPagedByteBufferMemory memory = newMemory(4, pageSizeBits, pageSize);
        int capacity = memory.capacity();

        assert capacity == pageSize;
        assert memory.memory[0] != null;
        assert memory.memory[1] == null;

        byte[] extraPage = new byte[256];
        Arrays.fill(extraPage, (byte) 0xFF);
        memory.memory[1] = extraPage;

        // when
        Object actual = getMethod.call(memory, capacity - typeSize);

        // then
        assertThat(actual).isNotNull();
        putMethod.call(memory, 0, actual);
        assertThat(Arrays.copyOf(memory.memory[0], typeSize)).doesNotContain((byte) 0xFF);
    }

    @Test(dataProvider = PUT_AND_GET_METHODS)
    public void shouldPutOnEndOfLastPage(MethodCall<ByteBufferMemory> putMethod, MethodCall<ByteBufferMemory> getMethod, Object value, int typeSize) {
        // given
        final int pageSizeBits = 8;
        final int pageSize = 1 << pageSizeBits;

        AbstractPagedByteBufferMemory memory = newMemory(4, pageSizeBits, pageSize);
        int capacity = memory.capacity();

        assert capacity == pageSize;
        assert memory.memory[0] != null;
        assert memory.memory[1] == null;

        byte[] extraPage = new byte[256];
        Arrays.fill(extraPage, (byte) 0xFF);
        memory.memory[1] = extraPage;

        // when
        putMethod.call(memory, capacity - typeSize, value);

        // then
        assertThat(memory.capacity()).isEqualTo(capacity);
        assertThat(extraPage).containsOnly((byte) 0xFF);
    }

    @Test(dataProvider = PUT_METHODS)
    public void shouldPutOnPagesBoundary(MethodCall<ByteBufferMemory> putMethod, Object value, int typeSize) {
        for (int bytesOnFirstPage = 1; bytesOnFirstPage < typeSize; bytesOnFirstPage++) {
            // given
            memory = allocateBuffer(PAGE_SIZE);
            memory.ensureCapacity(2 * PAGE_SIZE);

            // when
            putMethod.call(memory, PAGE_SIZE - bytesOnFirstPage, value);

            // then
            assertThatMemory(memory)
                    .startsWith(zeros(PAGE_SIZE - bytesOnFirstPage))
                    .containsSubsequence(Arrays.copyOfRange(TEST_BYTES, 0, typeSize))
                    .endsWith(zeros(PAGE_SIZE - typeSize + bytesOnFirstPage));
        }
    }

    @Test(dataProvider = GET_METHODS)
    public void shouldGetFromPagesBoundary(MethodCall<ByteBufferMemory> getMethod, Object expected, int typeSize) {
        for (int bytesOnFirstPage = 1; bytesOnFirstPage < typeSize; bytesOnFirstPage++) {
            // given
            memory = allocateBuffer(PAGE_SIZE);
            memory.ensureCapacity(2 * PAGE_SIZE);
            for (int idx = 0; idx < typeSize; idx++) {
                putByteDirect(PAGE_SIZE - bytesOnFirstPage + idx, TEST_BYTES[idx]);
            }

            // when
            Object actual = getMethod.call(memory, PAGE_SIZE - bytesOnFirstPage);

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test(enabled = MemoryAccess.RANGE_CHECKS, dataProvider = GET_METHODS)
    public void shouldUnderflowWhenGetNeedsBytesBeyondCapacity(MethodCall<ByteBufferMemory> getMethod, Object value, int typeSize) {
        // given

        // when
        catchException(getMethod, MethodCallException.class).call(memory, PAGE_SIZE - typeSize + 1);

        // then
        Assertions.assertThat((Throwable) CatchException.caughtException()).hasCauseInstanceOf(BufferUnderflowException.class);
    }

    @Test(dataProvider = PUT_METHODS)
    public void shouldGrowWhenPutTouchesBytesBeyondCapacity(MethodCall<ByteBufferMemory> putMethod, Object value, int typeSize) {
        // given

        // when
        putMethod.call(memory, PAGE_SIZE, value);

        // then
        assertThat(memory.capacity()).isEqualTo(2 * PAGE_SIZE);
        assertThatMemory(memory)
                .startsWith(zeros(PAGE_SIZE))
                .containsSubsequence(Arrays.copyOfRange(TEST_BYTES, 0, typeSize))
                .endsWith(zeros(PAGE_SIZE - typeSize));
    }

    @Test(expectedExceptions = BufferUnderflowException.class, enabled = MemoryAccess.RANGE_CHECKS)
    public void shouldUnderflowWhenGetByteArrayFromOutsideOfBuffer() {
        // given
        byte[] buf = new byte[memory.capacity() + 1];

        // when
        memory.get(PAGE_SIZE, buf, 0, buf.length);
    }

    @Test
    public void shouldNotUnderflowWhenGetByteArray() {
        // given
        setUpMemory();
        byte[] buf = new byte[memory.capacity() + 1];

        // when
        memory.get(0, buf, 0, buf.length);

        // then
        assertThat(buf).startsWith(TEST_BYTES).endsWith((byte) 0);
    }

    @Test
    public void shouldGrowWhenPutByteArray() {
        // given

        // when
        memory.put(PAGE_SIZE - 1, TEST_BYTES, 0, TEST_BYTES.length);

        // then
        assertThatMemory(memory)
                .hasSize(2 * PAGE_SIZE)
                .startsWith(zeros(PAGE_SIZE - 1))
                .containsSubsequence(TEST_BYTES)
                .endsWith(zeros(PAGE_SIZE + 1 - TEST_BYTES.length));
    }

    @Test
    public void ensureCapacityShouldNotGrowMemory() {
        // given
        int pageSizeBits = 8;
        int pageSize = 1 << pageSizeBits;
        AbstractPagedByteBufferMemory memory = newMemory(4, pageSizeBits, pageSize);

        // when
        memory.ensureCapacity(pageSize);

        // then
        assertThat(memory.capacity()).isEqualTo(pageSize);
        AtomicInteger iteratedBytesCount = new AtomicInteger(0);
        memory.iterateOverMemory((byte[] page, int offset, int length) -> {
            assertThat(offset).isEqualTo(0);
            assertThat(length).isEqualTo(pageSize);
            assertThat(page).isNotNull().hasSize(length);
            iteratedBytesCount.addAndGet(length);
        }, 0, memory.capacity());
        assertThat(iteratedBytesCount.get()).isEqualTo(memory.capacity());
    }

    @Test
    public void ensureCapacityOnFirstPageBoundaryShouldGrowMemoryByOnePage() {
        // given
        int pageSizeBits = 8;
        int pageSize = 1 << pageSizeBits;
        AbstractPagedByteBufferMemory memory = newMemory(4, pageSizeBits, pageSize);

        // when
        memory.ensureCapacity(pageSize - 3 + 4);

        // then
        assertThat(memory.capacity()).isEqualTo(2 * pageSize);
        AtomicInteger iteratedBytesCount = new AtomicInteger(0);
        memory.iterateOverMemory((byte[] page, int offset, int length) -> {
            assertThat(offset).isEqualTo(0);
            assertThat(length).isEqualTo(pageSize);
            assertThat(page).isNotNull().hasSize(length);
            iteratedBytesCount.addAndGet(length);
        }, 0, memory.capacity());
        assertThat(iteratedBytesCount.get()).isEqualTo(memory.capacity());
    }

    @Test
    public void ensureCapacityOnThirdPageBoundaryShouldGrowMemoryByThreePages() {
        // given
        int pageSizeBits = 8;
        int pageSize = 1 << pageSizeBits;
        AbstractPagedByteBufferMemory memory = newMemory(4, pageSizeBits, pageSize);

        // when
        memory.ensureCapacity((3 * pageSize) - 3 + 4);

        // then
        assertThat(memory.capacity()).isEqualTo(4 * pageSize);
        AtomicInteger iteratedBytesCount = new AtomicInteger(0);
        memory.iterateOverMemory((byte[] page, int offset, int length) -> {
            assertThat(offset).isEqualTo(0);
            assertThat(length).isEqualTo(pageSize);
            assertThat(page).isNotNull().hasSize(length);
            iteratedBytesCount.addAndGet(length);
        }, 0, memory.capacity());
        assertThat(iteratedBytesCount.get()).isEqualTo(memory.capacity());
    }

    @Test
    public void ensureCapacityOnThirdPageInteriorShouldGrowMemoryByTwoPages() {
        // given
        int pageSizeBits = 8;
        int pageSize = 1 << pageSizeBits;
        AbstractPagedByteBufferMemory memory = newMemory(4, pageSizeBits, pageSize);

        // when
        memory.ensureCapacity((3 * pageSize) - 5 + 4);

        // then
        assertThat(memory.capacity()).isEqualTo(3 * pageSize);
        AtomicInteger iteratedBytesCount = new AtomicInteger(0);
        memory.iterateOverMemory((byte[] page, int offset, int length) -> {
            assertThat(offset).isEqualTo(0);
            assertThat(length).isEqualTo(pageSize);
            assertThat(page).isNotNull().hasSize(length);
            iteratedBytesCount.addAndGet(length);
        }, 0, memory.capacity());
        assertThat(iteratedBytesCount.get()).isEqualTo(memory.capacity());
    }
}