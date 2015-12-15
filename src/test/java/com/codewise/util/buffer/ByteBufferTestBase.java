package com.codewise.util.buffer;

import com.codewise.util.memory.MutableMemory;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.function.Supplier;

import static com.codewise.util.buffer.ByteBufferAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public abstract class ByteBufferTestBase<B extends AbstractByteBuffer<?>> {

    protected MutableMemory memoryMockWithCapacityOf10;

    @BeforeMethod
    public void setUpMemoryMockWithCapacityOf10() {
        memoryMockWithCapacityOf10 = mock(MutableMemory.class);
        given(memoryMockWithCapacityOf10.capacity()).willReturn(10);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void capacityQueryShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.capacity();
    }

    @Test
    public void capacityQueryShouldDelegateToUnderlyingMemory() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        int capacity = buffer.capacity();

        // then
        verify(memoryMockWithCapacityOf10).capacity();
        verifyNoMoreInteractions(memoryMockWithCapacityOf10);

        assertThat(capacity).isEqualTo(10);
    }

    @Test
    public void capactiyQueryShouldSubtractBaseOffsetFromUnderlyingMemoryCapactiy() {
        // given
        ByteBufferBase<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 5);

        // when
        int capacity = buffer.capacity();

        // then
        verify(memoryMockWithCapacityOf10).capacity();
        verifyNoMoreInteractions(memoryMockWithCapacityOf10);

        assertThat(capacity).isEqualTo(5);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void positionQueryShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.position();
    }

    @Test
    public void positionQueryShouldReturnPreviouslySetPosition() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.position(5);

        // when
        int position = buffer.position();

        // then
        assertThat(position).isEqualTo(5);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void postionChangeShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.position(1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void positionChangeShouldThrowExceptionForValuesLessThanZero() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        buffer.position(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void positionChangeShouldThrowExceptionForValuesGreaterThanLimit() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        buffer.position(buffer.limit() + 1);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void limitQueryShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.limit();
    }

    @Test
    public void limitQueryShouldDelegateToUnderlyingMemoryIfLimitWasNotSet() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        int limit = buffer.limit();

        // then
        verify(memoryMockWithCapacityOf10).capacity();
        verifyNoMoreInteractions(memoryMockWithCapacityOf10);

        assertThat(limit).isEqualTo(10);
    }

    @Test
    public void limitQueryShouldDelegateToUnderlyingMemoryIfLimitWasResetToMemoryCapacity() {
        // given
        MutableMemory memory = mock(MutableMemory.class);
        given(memory.capacity()).willReturn(10, 20);

        AbstractByteBuffer<?> buffer = bufferForMemory(memory);
        buffer.limit = 5;

        // when
        int limitBeforeReset = buffer.limit();  // limit is 5 - no interaction with memory
        buffer.limit(10);                       // limit change to capacity - memory.capacity() will be called to check that fact
        int limitAfterReset = buffer.limit();   // limit will delegate to memory.capacity()

        // then
        verify(memory, times(2)).capacity();
        verifyNoMoreInteractions(memory);

        assertThat(limitBeforeReset).isEqualTo(5);
        assertThat(limitAfterReset).isEqualTo(20);
        assertThat(buffer).limitIsAtCapacity();
    }

    @Test
    public void limitQueryShouldSubtractBaseOffsetWhenDelegatingToUnderlyingMememory() {
        // given
        ByteBufferBase<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 5);

        // when
        int limit = buffer.limit();

        // then
        verify(memoryMockWithCapacityOf10).capacity();
        verifyNoMoreInteractions(memoryMockWithCapacityOf10);

        assertThat(limit).isEqualTo(5);
    }

    @Test
    public void limitQueuryShouldReturnPreviouslySetLimit() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        int limit = buffer.limit(5).limit();

        // then
        assertThat(limit).isEqualTo(5);
        assertThat(buffer).limitFieldIsEqualTo(5);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void limitChangeShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.limit(1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void limitChangeShouldThrowExceptionWhenNewValueIsLessThanZero() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        buffer.limit(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void limitChangeShouldThrowExceptionWhenNewValueIsGreaterThanCapacity() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        buffer.limit(11);
    }

    // remaining
    @Test(expectedExceptions = AssertionError.class)
    public void remainingQueryShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.remaining();
    }

    @Test
    public void remainingQueryShouldReturnDifferenceBetweenLimitAndPosition() {
        // given
        ByteBufferBase buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(8).position(3);

        // when
        int remaining = buffer.remaining();

        // then
        assertThat(remaining).isEqualTo(5);
    }

    // hasRemaining
    @Test(expectedExceptions = AssertionError.class)
    public void hasRemainingQueryShouldThrowExceptionForUnintitialziedBuffer() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.hasRemaining();
    }

    @Test
    public void hasRemainingQueryShouldReturnTrueIfRemainingIsGreaterThanZero() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(8).position(3);

        // when
        boolean hasRemaining = buffer.hasRemaining();

        // then
        assertThat(hasRemaining).isTrue();
    }

    @Test
    public void hasRemainingQueryShouldReturnFalseIfRemainingIsZero() {
        // given
        ByteBufferBase<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(8).position(8);

        // when
        boolean hasRemaining = buffer.hasRemaining();

        // then
        assertThat(hasRemaining).isFalse();
    }

    // sliceOf
    @Test(expectedExceptions = AssertionError.class)
    public void sliceOfShouldThrowExceptionForNonAbstractByteBufferDescendant() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();
        ByteBufferBase<?> source = mock(ByteBufferBase.class);

        // when
        buffer.sliceOf(source);
    }

    // TODO consider implementing this behaviour
/*
    @Test(expectedExceptions = IllegalStateException.class)
    public void sliceOfShouldThrowExceptionForInitializedBuffer() {
        // given
        ByteBufferBase<?> source = bufferUninitialized();
        ByteBufferBase<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 1).position(2).limit(3);

        // when
        buffer.sliceOf(source);

    }
*/

    @Test
    public void sliceOfShouldSuccessForUninitializedBuffer() {
        // given
        ByteBufferBase<?> source = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 1).position(2).limit(7);
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.sliceOf(source);

        // then
        assertThat(buffer)
                .isInitialized()
                .memoryIsSameAs(memoryMockWithCapacityOf10)
                .baseOffsetIsEqualTo(3)
                .positionIsEqualTo(0)
                .limitIsEqualTo(5);
    }

    // duplicateOf
    @Test(expectedExceptions = AssertionError.class)
    public void duplicateOfShouldThrowExceptionForNonAbstractByteBufferDescendant() {
        // given
        ByteBufferBase<?> buffer = bufferUninitialized();
        ByteBufferBase<?> source = mock(ByteBufferBase.class);

        // when
        buffer.duplicateOf(source);
    }

    // TODO consider implementing this behaviour
/*
    @Test(expectedExceptions = IllegalStateException.class)
    public void duplicateOfShouldThrowExceptionForInitializedBuffer() {
        // given
        ByteBufferBase<?> source = bufferUninitialized();
        ByteBufferBase<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 1).position(2).limit(3);

        // when
        buffer.duplicateOf(source);

    }
*/

    @Test
    public void duplicateOfShouldSuccessForUninitializedBuffer() {
        // given
        ByteBufferBase<?> source = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 1).position(2).limit(3);
        ByteBufferBase<?> buffer = bufferUninitialized();

        // when
        buffer.duplicateOf(source);

        // then
        assertThat(buffer)
                .isInitialized()
                .memoryIsSameAs(memoryMockWithCapacityOf10)
                .baseOffsetIsEqualTo(1)
                .positionIsEqualTo(2)
                .limitIsEqualTo(3);
    }

    // free
    @Test
    public void freeShouldClearInternalDataForAbstractByteBufferDescendant() {
        // given
        ByteBufferBase<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 1).position(2).limit(3);

        // when
        buffer.free();

        // then
        assertThat(buffer)
                .isNotInitialzied()
                .memoryIsNull()
                .baseOffsetIsEqualTo(0)
                .positionFieldIsEqualTo(0)
                .limitIsAtCapacity();
    }

    // uninitializedBufferFactory
    @Test
    public void uninitializedBufferFactoryShouldReturnWorkingFactoryOfSameClass() {
        // given
        ByteBufferBase<?> source = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        Supplier<? extends ByteBufferBase> factory = source.uninitializedBufferFactory();
        ByteBufferBase<?> buffer = factory.get();

        // then
        assertThat(factory).describedAs("uninitializedBufferFactory return value").isNotNull();
        assertThat(buffer).isNotInitialzied().isInstanceOf(bufferClass());
    }

    // compareTo
    @Test(expectedExceptions = AssertionError.class)
    public void compareToShouldThrowExceptionForUninitializedBuffer() {
        // given
        ByteBufferBase<?> a = bufferUninitialized();
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        a.compareTo(b);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void compareToShouldThrowExceptionForNonAbstractByteBufferDescendant() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10);
        ByteBufferBase<?> b = mock(ByteBufferBase.class);

        // when
        a.compareTo(b);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void compareToShouldThrowExceptionForUninitializedBufferPassedAsArgument() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10);
        ByteBufferBase<?> b = bufferUninitialized();

        // when
        a.compareTo(b);
    }

    @Test
    public void compareToShouldDelegateToMemoryCompareOfCommonRemainingBytes() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10).limit(4);

        given(memoryMockWithCapacityOf10.compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt())).willReturn(Integer.MIN_VALUE);

        // when
        int result = a.compareTo(b);

        // then
        ArgumentCaptor<Integer> indexArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<MutableMemory> memoryArgumentCaptor = ArgumentCaptor.forClass(MutableMemory.class);
        ArgumentCaptor<Integer> offsetArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> lengthArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(memoryMockWithCapacityOf10)
                .compare(indexArgumentCaptor.capture(), memoryArgumentCaptor.capture(),
                        offsetArgumentCaptor.capture(), lengthArgumentCaptor.capture());

        assertThat(indexArgumentCaptor.getValue()).isEqualTo(a.position());
        assertThat(memoryArgumentCaptor.getValue()).isSameAs(memoryMockWithCapacityOf10);
        assertThat(offsetArgumentCaptor.getValue()).isEqualTo(b.position());
        assertThat(lengthArgumentCaptor.getValue()).isEqualTo(Math.min(a.remaining(), b.remaining()));

        assertThat(result).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void compareToShouldReturnRemainingDifferenceIfMemoryEqual() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10).limit(4);

        given(memoryMockWithCapacityOf10.compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt())).willReturn(0);

        // when
        int result = a.compareTo(b);

        // then
        ArgumentCaptor<Integer> indexArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<MutableMemory> memoryArgumentCaptor = ArgumentCaptor.forClass(MutableMemory.class);
        ArgumentCaptor<Integer> offsetArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> lengthArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(memoryMockWithCapacityOf10)
                .compare(indexArgumentCaptor.capture(), memoryArgumentCaptor.capture(),
                        offsetArgumentCaptor.capture(), lengthArgumentCaptor.capture());

        assertThat(indexArgumentCaptor.getValue()).isEqualTo(5);
        assertThat(memoryArgumentCaptor.getValue()).isSameAs(memoryMockWithCapacityOf10);
        assertThat(offsetArgumentCaptor.getValue()).isEqualTo(0);
        assertThat(lengthArgumentCaptor.getValue()).isEqualTo(Math.min(a.remaining(), b.remaining()));

        assertThat(result).isEqualTo(a.remaining() - b.remaining());
    }

    // equals
    @Test
    public void equalsShouldReturnTrueForSameBufferAsArgument() {
        // given
        ByteBufferBase<?> a = bufferUninitialized();

        // when
        boolean actual = a.equals(a);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    public void equalsShouldReturnFalseForNullAsArgument() {
        // given
        ByteBufferBase<?> a = bufferUninitialized();

        // when
        boolean actual = a.equals(null);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void equalsShouldReturnFalseForNonAbstractByteBufferDescendant() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10);
        ByteBufferBase<?> b = mock(ByteBufferBase.class);

        // when
        boolean actual = a.equals(b);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    public void equalsShouldReturnTrueIfBothBuffersUninitialized() {
        // given
        ByteBufferBase<?> a = bufferUninitialized();
        ByteBufferBase<?> b = bufferUninitialized();

        // when
        boolean actual = a.equals(b);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    public void equalsShouldReturnTrueForInitializedAndUninitializedBuffersMix() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10);
        ByteBufferBase<?> b = bufferUninitialized();

        // when
        boolean actualAB = a.equals(b);
        boolean actualBA = b.equals(a);

        // then
        assertThat(actualAB).isFalse();
        assertThat(actualBA).isFalse();
    }

    @Test
    public void equalsShouldReturnFalseIfValuesOfRemainingDiffers() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10).limit(4);

        // when
        boolean actual = a.equals(b);

        // then
        verify(memoryMockWithCapacityOf10, never()).compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt());

        assertThat(actual).isFalse();
    }

    @Test
    public void equalsShouldDelegateToMemoryCompareIfValuesOfRemainingAreSame() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10).limit(5);

        given(memoryMockWithCapacityOf10.compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt())).willReturn(Integer.MIN_VALUE);

        // when
        boolean actual = a.equals(b);

        // then
        ArgumentCaptor<Integer> indexArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<MutableMemory> memoryArgumentCaptor = ArgumentCaptor.forClass(MutableMemory.class);
        ArgumentCaptor<Integer> offsetArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> lengthArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(memoryMockWithCapacityOf10)
                .compare(indexArgumentCaptor.capture(), memoryArgumentCaptor.capture(),
                        offsetArgumentCaptor.capture(), lengthArgumentCaptor.capture());

        assertThat(indexArgumentCaptor.getValue()).isEqualTo(5);
        assertThat(memoryArgumentCaptor.getValue()).isSameAs(memoryMockWithCapacityOf10);
        assertThat(offsetArgumentCaptor.getValue()).isEqualTo(0);
        assertThat(lengthArgumentCaptor.getValue()).isEqualTo(Math.min(a.remaining(), b.remaining()));
    }

    @Test
    public void equalsShouldDelegateToMemoryCompareAndReturnTrueIfComparationResultIsZero() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10).limit(5);

        given(memoryMockWithCapacityOf10.compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt())).willReturn(0);

        // when
        boolean actual = a.equals(b);

        // then
        verify(memoryMockWithCapacityOf10).compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt());

        assertThat(actual).isTrue();
    }

    @Test
    public void equalsShouldDelegateToMemoryCompareAndReturnFalseIfComparationResultIsNonZero() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);
        ByteBufferBase<?> b = bufferForMemory(memoryMockWithCapacityOf10).limit(5);

        given(memoryMockWithCapacityOf10.compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt())).willReturn(1);

        // when
        boolean actual = a.equals(b);

        // then
        verify(memoryMockWithCapacityOf10).compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt());

        assertThat(actual).isFalse();
    }

    // getOffset
    @Test(expectedExceptions = AssertionError.class)
    public void getOffsetShouldThrowExceptionForUninitializedBuffer() {
        // given
        AbstractByteBuffer<?> buffer = bufferUninitialized();

        // when
        int offset = buffer.getOffset();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void getOffsetWithIndexShouldThrowExceptionForUninitializedBuffer() {
        // given
        AbstractByteBuffer<?> buffer = bufferUninitialized();

        // when
        int offset = buffer.getOffset(0);
    }

    @Test
    public void getOffsetShouldReturnPositionPlusBaseOffset() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 2);
        buffer.position(3);

        // when
        int offset = buffer.getOffset();

        // then
        assertThat(offset).isEqualTo(5);
    }

    @Test
    public void getOffsetWithIndexShouldReturnIndexPlusBaseOffset() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemoryAndBaseOffset(memoryMockWithCapacityOf10, 2);

        // when
        int offset = buffer.getOffset(3);

        // then
        assertThat(offset).isEqualTo(5);
    }

    // getRangeCheck
    @Test(expectedExceptions = AssertionError.class)
    public void getRangeCheckShouldThrowExceptionForUninitializedBuffer() {
        // given
        AbstractByteBuffer<?> buffer = bufferUninitialized();

        // when
        buffer.getRangeCheck(0, 0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void getRangeCheckShouldThrowExceptionForNegativeIndex() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(5);

        // when
        buffer.getRangeCheck(-13, 3);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void getRangeCheckShouldThrowExceptionForNegativeSize() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(5);

        // when
        buffer.getRangeCheck(3, -3);
    }

    @Test(expectedExceptions = BufferUnderflowException.class)
    public void getRangeCheckShouldThrowExceptionIfIndexPlusSizeIsGreaterThanLimitWhenLimitIsNotAtCapacity() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(5);

        // when
        buffer.getRangeCheck(3, 3);
    }

    @Test(expectedExceptions = BufferUnderflowException.class)
    public void getRangeCheckShouldThrowExceptionIfIndexPlusSizeIsGreaterThanLimitWhenLimitIsAtCapacity() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        buffer.getRangeCheck(3, 8);
    }

    // putRangeCheck
    @Test(expectedExceptions = AssertionError.class)
    public void putRangeCheckShouldThrowExceptionForUninitializedBuffer() {
        // given
        AbstractByteBuffer<?> buffer = bufferUninitialized();

        // when
        buffer.putRangeCheck(0, 0);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void putRangeCheckShouldThrowExceptionForNegativeIndex() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(5);

        // when
        buffer.putRangeCheck(-13, 3);
    }

    @Test(expectedExceptions = AssertionError.class)
    public void putRangeCheckShouldThrowExceptionForNegativeSize() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(5);

        // when
        buffer.putRangeCheck(3, -3);
    }

    @Test(expectedExceptions = BufferOverflowException.class)
    public void putRangeCheckShouldThrowExceptionIfIndexPlusSizeIsGreaterThanLimitWhenLimitIsNotAtCapacity() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);
        buffer.limit(5);

        // when
        buffer.putRangeCheck(3, 3);
    }

    @Test
    public void resetAtCapacityShouldSetCapacityAndPosition() {
        // given
        ByteBufferBase<?> a = bufferForMemory(memoryMockWithCapacityOf10).position(5);

        given(memoryMockWithCapacityOf10.compare(anyInt(), any(MutableMemory.class), anyInt(), anyInt())).willReturn(Integer.MIN_VALUE);

        // when
        a.resetAtPosition(3);

        // then
        assertThat(a.position()).isEqualTo(3);
        assertThat(a.limit()).isEqualTo(10);
        assertThat(a.remaining()).isEqualTo(7);
        assertThat(a.capacity()).isEqualTo(10);
    }

    @Test
    public void putRangeCheckShouldNotThrowExceptionIfIndexPlusSizeIsGreaterThanLimitWhenLimitIsAtCapacity() {
        // given
        AbstractByteBuffer<?> buffer = bufferForMemory(memoryMockWithCapacityOf10);

        // when
        buffer.putRangeCheck(3, 8);
    }

    protected abstract Class<?> bufferClass();

    protected abstract B bufferUninitialized();

    protected abstract B bufferForMemory(MutableMemory memory);

    protected abstract B bufferForMemoryAndBaseOffset(MutableMemory memory, int baseOffset);
}