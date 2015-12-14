package pl.codewise.util.buffer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.codewise.test.utils.MethodCall;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static pl.codewise.test.utils.MethodCallUtils.*;

abstract class ReadOnlyByteBufferTestBase<B extends ReadOnlyByteBufferImpl<?>> extends ByteBufferTestBase<B> {

    public static final String GET_METHODS_WITHOUT_INDEX = "GET_METHODS_WITHOUT_INDEX";
    public static final String GET_METHODS_WITH_INDEX = "GET_METHODS_WITH_INDEX";

    protected ByteBufferMemory memoryMockWithCapacityOf50;

    @BeforeMethod
    public void setUp() throws Exception {
        memoryMockWithCapacityOf50 = mock(ByteBufferMemory.class);
        given(memoryMockWithCapacityOf50.capacity()).willReturn(50);
    }

    @DataProvider(name = GET_METHODS_WITHOUT_INDEX)
    public Object[][] getMethodsWithoutIndex() {
        return new Object[][]{
                {methodForReference((BufferGetMethod) ReadOnlyByteBuffer::get), methodForReference((MemoryGetMethod) ByteBufferMemory::get), Byte.BYTES, (byte) 1},
                {methodForReference((BufferGetMethod) ReadOnlyByteBuffer::getChar), methodForReference((MemoryGetMethod) ByteBufferMemory::getChar), Character.BYTES, 'a'},
                {methodForReference((BufferGetMethod) ReadOnlyByteBuffer::getShort), methodForReference((MemoryGetMethod) ByteBufferMemory::getShort), Short.BYTES, (short) 2},
                {methodForReference((BufferGetMethod) ReadOnlyByteBuffer::getInt), methodForReference((MemoryGetMethod) ByteBufferMemory::getInt), Integer.BYTES, 3},
                {methodForReference((BufferGetMethod) ReadOnlyByteBuffer::getLong), methodForReference((MemoryGetMethod) ByteBufferMemory::getLong), Long.BYTES, 4l},
                {methodForReference((BufferGetMethod) ReadOnlyByteBuffer::getDouble), methodForReference((MemoryGetMethod) ByteBufferMemory::getDouble), Double.BYTES, 123.32d}
        };
    }

    @DataProvider(name = GET_METHODS_WITH_INDEX)
    public Object[][] getMethodsWithIndex() {
        return new Object[][]{
                {methodForReference((BufferGetIndexedMethod) ReadOnlyByteBuffer::get), methodForReference((MemoryGetMethod) ByteBufferMemory::get), Byte.BYTES, (byte) 1},
                {methodForReference((BufferGetIndexedMethod) ReadOnlyByteBuffer::getChar), methodForReference((MemoryGetMethod) ByteBufferMemory::getChar), Character.BYTES, 'a'},
                {methodForReference((BufferGetIndexedMethod) ReadOnlyByteBuffer::getShort), methodForReference((MemoryGetMethod) ByteBufferMemory::getShort), Short.BYTES, (short) 2},
                {methodForReference((BufferGetIndexedMethod) ReadOnlyByteBuffer::getInt), methodForReference((MemoryGetMethod) ByteBufferMemory::getInt), Integer.BYTES, 3},
                {methodForReference((BufferGetIndexedMethod) ReadOnlyByteBuffer::getLong), methodForReference((MemoryGetMethod) ByteBufferMemory::getLong), Long.BYTES, 4l},
                {methodForReference((BufferGetIndexedMethod) ReadOnlyByteBuffer::getDouble), methodForReference((MemoryGetMethod) ByteBufferMemory::getDouble), Double.BYTES, 123.32d}
        };
    }

    @Test(dataProvider = GET_METHODS_WITHOUT_INDEX)
    public void getShouldInvokeGetRangeCheckWithCurrentPositionAndTypeSize(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf10).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBufferImpl buffer = (ReadOnlyByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf10).position(1);
        buffer = spy(buffer);

        // when
        Object result = getMethod.call(buffer);

        // then
        verify(buffer).getRangeCheck(1, returnTypeSize);
    }

    @Test(dataProvider = GET_METHODS_WITHOUT_INDEX)
    public void getShouldDelegateToMemoryGet(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf10).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(1);

        // when
        Object result = getMethod.call(buffer);

        // then
        assertThat(result).isEqualTo(returnValueExample);
        verifyCall(memoryGetMethod).withParams(1).wasCalledOn(memoryMockWithCapacityOf10);
    }

    @Test(dataProvider = GET_METHODS_WITHOUT_INDEX)
    public void getShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf50).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);

        // when
        Object result = getMethod.call(buffer);

        // then
        assertThat(result).isEqualTo(returnValueExample);
        verifyCall(memoryGetMethod).withParams(11).wasCalledOn(memoryMockWithCapacityOf50);
    }

    @Test(dataProvider = GET_METHODS_WITHOUT_INDEX)
    public void getShouldChangePosition(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf10).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(1);

        // when
        Object result = getMethod.call(buffer);

        // then
        assertThat(buffer.position()).isEqualTo(1 + returnTypeSize);
    }

/*
    @Test
    public void getBytesShouldInvokeGetRangeCheckWithCurrentPositionAndReadBufferLength() {
        // given
        BufferImpl buffer = spy(bufferForMemory(memoryMockWithCapacityOf50).position(1));
        byte[] buf = new byte[8];

        // when
        BufferImpl result = buffer.get(buf);

        // then
        assertThat(result).isSameAs(buffer);
        verify(buffer).getRangeCheck(1, buf.length);
    }
*/

    @Test
    public void getBytesWithOffsetAndLengthShouldInvokeGetRangeCheckWithCurrentPositionAndSpecifiedReadBufferLength() {
        // given
        ReadOnlyByteBufferImpl buffer = (ReadOnlyByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf50).position(1);
        buffer = spy(buffer);
        byte[] buf = new byte[10];

        // when
        ReadOnlyByteBuffer result = buffer.get(buf, 2, 8);

        // then
        assertThat(result).isSameAs(buffer);
        verify(buffer).getRangeCheck(1, 8);
    }

    @Test
    public void getBytesShouldDelegateToMemoryGetBytes() {
        // given
        ReadOnlyByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.get(buf);

        // then
        verify(memoryMockWithCapacityOf50).get(1, buf, 0, buf.length);
    }

    @Test
    public void getBytesWithOffsetAndLengthShouldDelegateToMemoryGetBytes() {
        // given
        ReadOnlyByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.get(buf, 2, 8);

        // then
        verify(memoryMockWithCapacityOf50).get(1, buf, 2, 8);
    }

    @Test
    public void getBytesShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset() {
        // given
        ReadOnlyByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.get(buf);

        // then
        verify(memoryMockWithCapacityOf50).get(11, buf, 0, buf.length);
    }

    @Test
    public void getBytesWithOffsetAndLengthShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset() {
        // given
        ReadOnlyByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.get(buf, 2, 8);

        // then
        verify(memoryMockWithCapacityOf50).get(11, buf, 2, 8);
    }

    @Test
    public void getBytesShouldChangePosition() {
        // given
        ReadOnlyByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.get(buf);

        // then
        assertThat(buffer.position()).isEqualTo(11);
    }

    @Test
    public void getBytesWithOffsetAndLengthShouldChangePosition() {
        // given
        ReadOnlyByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.get(buf, 2, 8);

        // then
        assertThat(buffer.position()).isEqualTo(9);
    }

    @Test(dataProvider = GET_METHODS_WITH_INDEX)
    public void getWithIndexShouldInvokeGetRangeCheckWithIndexAndTypeSize(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf10).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBufferImpl buffer = (ReadOnlyByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf10).position(4);
        buffer = spy(buffer);

        // when
        Object result = getMethod.call(buffer, 1);

        // then
        verify(buffer).getRangeCheck(1, returnTypeSize);
    }

    @Test(dataProvider = GET_METHODS_WITH_INDEX)
    public void getWithIndexShouldDelegateToMemoryGet(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf10).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(4);

        // when
        Object result = getMethod.call(buffer, 1);

        // then
        assertThat(result).isEqualTo(returnValueExample);
        verifyCall(memoryGetMethod).withParams(1).wasCalledOn(memoryMockWithCapacityOf10);
    }

    @Test(dataProvider = GET_METHODS_WITH_INDEX)
    public void getWithIndexShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf50).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(4);

        // when
        Object result = getMethod.call(buffer, 1);

        // then
        assertThat(result).isEqualTo(returnValueExample);
        verifyCall(memoryGetMethod).withParams(11).wasCalledOn(memoryMockWithCapacityOf50);
    }

    @Test(dataProvider = GET_METHODS_WITH_INDEX)
    public void getWithIndexShouldNotChangePosition(MethodCall<ReadOnlyByteBuffer> getMethod, MethodCall<ByteBufferMemory> memoryGetMethod, int returnTypeSize, Object returnValueExample) {
        // given
        whenCall(memoryGetMethod).invokedOn(memoryMockWithCapacityOf10).withParams(anyInt()).thenReturn(returnValueExample);
        ReadOnlyByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(4);

        // when
        Object result = getMethod.call(buffer, 1);

        // then
        assertThat(buffer.position()).isEqualTo(4);
    }

    @FunctionalInterface
    interface BufferGetMethod extends Function<ReadOnlyByteBuffer, Object>, Serializable {
    }

    @FunctionalInterface
    interface BufferGetIndexedMethod extends BiFunction<ReadOnlyByteBuffer, Integer, Object>, Serializable {
    }

    @FunctionalInterface
    interface MemoryGetMethod extends BiFunction<ByteBufferMemory, Integer, Object>, Serializable {
    }
}
