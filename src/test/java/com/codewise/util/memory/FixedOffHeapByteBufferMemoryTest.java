package com.codewise.util.memory;

import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.Test;
import sun.nio.ch.DirectBuffer;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedOffHeapByteBufferMemoryTest extends FixedOffHeapMutableMemoryTestBase<FixedOffHeapByteBufferMemory> {

    @Override
    protected FixedOffHeapByteBufferMemory wrapByteArray(ByteBuffer memoryBuffer) {
        return new FixedOffHeapByteBufferMemory(((DirectBuffer) memoryBuffer).address(), memoryBuffer.capacity());
    }

    @Test
    public void shouldPutByteArrayToBuffer() {
        // Given
        byte[] expected = RandomUtils.nextBytes(128);
        byte[] clone = expected.clone();

        FixedOffHeapByteBufferMemory buffer = wrapByteArray(ByteBuffer.allocateDirect(112));

        // When
        buffer.put(4, clone, 20, clone.length - 20);

        // Then
        assertThat(clone).isEqualTo(expected);

        byte[] actual = new byte[112];
        buffer.get(0, actual, 0, actual.length);

        for (int i = 0; i < buffer.capacity(); i++) {
            assertThat(buffer.get(i)).isEqualTo(actual[i]);
        }
    }
}
