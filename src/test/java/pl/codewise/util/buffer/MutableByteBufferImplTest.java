package pl.codewise.util.buffer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.codewise.test.utils.MethodCall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static pl.codewise.test.utils.MethodCallUtils.methodForCall;
import static pl.codewise.test.utils.MethodCallUtils.verifyCall;

public class MutableByteBufferImplTest extends ReadOnlyByteBufferTestBase<MutableByteBufferImpl> {

    public static final String PUT_METHODS_WITHOUT_INDEX = "PUT_METHODS_WITHOUT_INDEX";
    public static final String PUT_METHODS_WITH_INDEX = "PUT_METHODS_WITH_INDEX";

    @DataProvider(name = PUT_METHODS_WITHOUT_INDEX)
    public Object[][] putMethodsWithoutIndex() {
        return new Object[][]{
                {methodForCall((MutableByteBuffer b) -> b.put((byte) 0)), methodForCall((ByteBufferMemory m) -> m.put(0, (byte) 0)), Byte.BYTES, (byte) 1},
                {methodForCall((MutableByteBuffer b) -> b.putChar('a')), methodForCall((ByteBufferMemory m) -> m.putChar(0, 'a')), Character.BYTES, 'a'},
                {methodForCall((MutableByteBuffer b) -> b.putShort((short) 0)), methodForCall((ByteBufferMemory m) -> m.putShort(0, (short) 0)), Short.BYTES, (short) 2},
                {methodForCall((MutableByteBuffer b) -> b.putInt(0)), methodForCall((ByteBufferMemory m) -> m.putInt(0, 0)), Integer.BYTES, 3},
                {methodForCall((MutableByteBuffer b) -> b.putLong(0l)), methodForCall((ByteBufferMemory m) -> m.putLong(0, 0l)), Long.BYTES, 4l},
                {methodForCall((MutableByteBuffer b) -> b.putDouble(0.0d)), methodForCall((ByteBufferMemory m) -> m.putDouble(0, 0.0d)), Double.BYTES, 123.32d}
        };
    }

    @DataProvider(name = PUT_METHODS_WITH_INDEX)
    public Object[][] putMehodsWithIndex() {
        return new Object[][]{
                {methodForCall((MutableByteBuffer b) -> b.put(0, (byte) 0)), methodForCall((ByteBufferMemory m) -> m.put(0, (byte) 0)), Byte.BYTES, (byte) 1},
                {methodForCall((MutableByteBuffer b) -> b.putChar(0, 'a')), methodForCall((ByteBufferMemory m) -> m.putChar(0, 'a')), Character.BYTES, 'a'},
                {methodForCall((MutableByteBuffer b) -> b.putShort(0, (short) 0)), methodForCall((ByteBufferMemory m) -> m.putShort(0, (short) 0)), Short.BYTES, (short) 2},
                {methodForCall((MutableByteBuffer b) -> b.putInt(0, 0)), methodForCall((ByteBufferMemory m) -> m.putInt(0, 0)), Integer.BYTES, 3},
                {methodForCall((MutableByteBuffer b) -> b.putLong(0, 0l)), methodForCall((ByteBufferMemory m) -> m.putLong(0, 0l)), Long.BYTES, 4l},
                {methodForCall((MutableByteBuffer b) -> b.putDouble(0, 0.0d)), methodForCall((ByteBufferMemory m) -> m.putDouble(0, 0.0d)), Double.BYTES, 123.32d}
        };
    }

    @Test(dataProvider = PUT_METHODS_WITHOUT_INDEX)
    public void putShouldInvokePutRangeCheckWithCurrentPositionAndTypeSize(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBufferImpl buffer = (MutableByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf10).position(1);
        buffer = spy(buffer);

        // when
        Object result = putMethod.call(buffer, valueExample);

        // then
        verify(buffer).putRangeCheck(1, typeSize);
    }

    @Test(dataProvider = PUT_METHODS_WITHOUT_INDEX)
    public void putShouldDelegateToMemoryPut(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(1);

        // when
        Object result = putMethod.call(buffer, valueExample);

        // then
        assertThat(result).isSameAs(buffer);
        verifyCall(memoryPutMethod).withParams(1, valueExample).wasCalledOn(memoryMockWithCapacityOf10);
    }

    @Test(dataProvider = PUT_METHODS_WITHOUT_INDEX)
    public void putShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);

        // when
        Object result = putMethod.call(buffer, valueExample);

        // then
        assertThat(result).isSameAs(buffer);
        verifyCall(memoryPutMethod).withParams(11, valueExample).wasCalledOn(memoryMockWithCapacityOf50);
    }

    @Test(dataProvider = PUT_METHODS_WITHOUT_INDEX)
    public void putShouldChangePosition(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(1);

        // when
        Object result = putMethod.call(buffer, valueExample);

        // then
        assertThat(result).isSameAs(buffer);
        assertThat(buffer.position()).isEqualTo(1 + typeSize);
    }

/*
    @Test
    public void putBytesShouldInvokeGetRangeCheckWithCurrentPositionAndReadBufferLength() {
        // given
        MutableByteBufferImpl buffer = (MutableByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf10).position(1);
        buffer = spy(buffer);
        byte[] buf = new byte[8];

        // when
        MutableByteBuffer result = buffer.put(buf);

        // then
        assertThat(result).isSameAs(buffer);
        verify(buffer).putRangeCheck(1, buf.length);
    }
*/

    @Test
    public void putBytesWithOffsetAndLengthShouldInvokeGetRangeCheckWithCurrentPositionAndSpecifiedReadBufferLength() {
        // given
        MutableByteBufferImpl buffer = (MutableByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf10).position(1);
        buffer = spy(buffer);
        byte[] buf = new byte[10];

        // when
        MutableByteBuffer result = buffer.put(buf, 2, 8);

        // then
        assertThat(result).isSameAs(buffer);
        verify(buffer).putRangeCheck(1, 8);
    }

    @Test
    public void putBytesShouldDelegateToMemoryPutBytes() {
        // given
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.put(buf);

        // then
        verify(memoryMockWithCapacityOf50).put(1, buf, 0, buf.length);
    }

    @Test
    public void putBytesWithOffsetAndLengthShouldDelegateToMemoryPutBytes() {
        // given
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.put(buf, 2, 8);

        // then
        verify(memoryMockWithCapacityOf50).put(1, buf, 2, 8);
    }

    @Test
    public void putBytesShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset() {
        // given
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.put(buf);

        // then
        verify(memoryMockWithCapacityOf50).put(11, buf, 0, buf.length);
    }

    @Test
    public void putBytesWithOffsetAndLengthShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset() {
        // given
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.put(buf, 2, 8);

        // then
        verify(memoryMockWithCapacityOf50).put(11, buf, 2, 8);
    }

    @Test
    public void putBytesShouldChangePosition() {
        // given
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.put(buf);

        // then
        assertThat(buffer.position()).isEqualTo(11);
    }

    @Test
    public void putBytesWithOffsetAndLengthShouldChangePosition() {
        // given
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(1);
        byte[] buf = new byte[10];

        // when
        buffer.put(buf, 2, 8);

        // then
        assertThat(buffer.position()).isEqualTo(9);
    }

    @Test
    public void putBufferShouldInvokeGetRangeCheckWithCurrentPositionAndReadBufferRemaining() {
        // given
        ReadOnlyByteBuffer srcBuffer = ReadOnlyByteBufferImplTest.BufferImpl.forMemory(memoryMockWithCapacityOf50).position(5).limit(15);
        MutableByteBufferImpl buffer = (MutableByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf50).position(1);
        buffer = spy(buffer);

        // when
        MutableByteBuffer result = buffer.put(srcBuffer);

        // then
        assertThat(result).isSameAs(buffer);
        verify(buffer).putRangeCheck(1, 10);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void putBufferShouldThrowExceptionIfSourceBufferIsNotAbstractByteBufferDescendant() {
        // given
        ReadOnlyByteBuffer srcBuffer = mock(ReadOnlyByteBuffer.class);
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(1);

        // when
        buffer.put(srcBuffer);
    }

    @Test
    public void putBufferShouldDelegateToMemoryPutPassingSourceBufferMemoryOffsetAndRemainingAsParams() {
        // given
        ReadOnlyByteBuffer srcBuffer = ReadOnlyByteBufferImplTest.BufferImpl.forMemory(memoryMockWithCapacityOf10).position(1).limit(9);
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(2);

        // when
        buffer.put(srcBuffer);

        // then
        verify(memoryMockWithCapacityOf50).put(2, memoryMockWithCapacityOf10, 1, 8);
    }

    @Test
    public void putBufferShouldDelegateToMemoryPutComputingMemoryPositionUsingBaseOffset() {
        // given
        ReadOnlyByteBuffer srcBuffer = ReadOnlyByteBufferImplTest.BufferImpl.forMemory(memoryMockWithCapacityOf10);
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(2);

        // when
        buffer.put(srcBuffer);

        // then
        verify(memoryMockWithCapacityOf50).put(12, memoryMockWithCapacityOf10, 0, 10);
    }

    @Test
    public void putBufferShouldChangePosition() {
        // given
        ReadOnlyByteBuffer srcBuffer = ReadOnlyByteBufferImplTest.BufferImpl.forMemory(memoryMockWithCapacityOf10).position(1).limit(9);
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(2);

        // when
        buffer.put(srcBuffer);

        // then
        assertThat(buffer.position()).isEqualTo(10);
    }

    @Test
    public void putBufferShouldChangePositionOfSourceBuffer() {
        // given
        ReadOnlyByteBuffer srcBuffer = ReadOnlyByteBufferImplTest.BufferImpl.forMemory(memoryMockWithCapacityOf10).position(1).limit(9);
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf50).position(2);

        // when
        buffer.put(srcBuffer);

        // then
        assertThat(srcBuffer.position()).isEqualTo(9);
    }

    @Test(dataProvider = PUT_METHODS_WITH_INDEX)
    public void putWithIndexShouldInvokePutRangeCheckWithCurrentPositionAndTypeSize(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBufferImpl buffer = (MutableByteBufferImpl) bufferForMemory(memoryMockWithCapacityOf10).position(4);
        buffer = spy(buffer);

        // when
        Object result = putMethod.call(buffer, 1, valueExample);

        // then
        verify(buffer).putRangeCheck(1, typeSize);
    }

    @Test(dataProvider = PUT_METHODS_WITH_INDEX)
    public void putWithIndexShouldDelegateToMemoryGet(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(4);

        // when
        Object result = putMethod.call(buffer, 1, valueExample);

        // then
        assertThat(result).isSameAs(buffer);
        verifyCall(memoryPutMethod).withParams(1, valueExample).wasCalledOn(memoryMockWithCapacityOf10);
    }

    @Test(dataProvider = PUT_METHODS_WITH_INDEX)
    public void putWithIndexShouldDelegateToMemoryComputingMemoryPositionUsingBaseOffset(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBuffer buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf50, 10).position(4);

        // when
        Object result = putMethod.call(buffer, 1, valueExample);

        // then
        assertThat(result).isSameAs(buffer);
        verifyCall(memoryPutMethod).withParams(11, valueExample).wasCalledOn(memoryMockWithCapacityOf50);
    }

    @Test(dataProvider = PUT_METHODS_WITH_INDEX)
    public void putWithIndexShouldNotChangePosition(MethodCall<MutableByteBuffer> putMethod, MethodCall<ByteBufferMemory> memoryPutMethod, int typeSize, Object valueExample) {
        // given
        MutableByteBuffer buffer = bufferForMemory(memoryMockWithCapacityOf10).position(4);

        // when
        Object result = putMethod.call(buffer, 1, valueExample);

        // then
        assertThat(buffer.position()).isEqualTo(4);
    }

    @Override
    protected MutableByteBufferImpl bufferUninitialized() {
        return new MutableByteBufferImpl();
    }

    @Override
    protected MutableByteBufferImpl bufferForMemory(ByteBufferMemory memory) {
        return new MutableByteBufferImpl(memory);
    }

    @Override
    protected MutableByteBufferImpl bufferForMemoryAndBaseOffset(ByteBufferMemory memory, int baseOffset) {
        return (MutableByteBufferImpl) new MutableByteBufferImpl(memory).withBaseOffset(baseOffset);
    }

    @Override
    protected Class<?> bufferClass() {
        return MutableByteBufferImpl.class;
    }
}