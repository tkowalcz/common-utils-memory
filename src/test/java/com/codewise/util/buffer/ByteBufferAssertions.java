package com.codewise.util.buffer;

import org.assertj.core.api.Assertions;

public class ByteBufferAssertions extends Assertions {

    public static AbstractByteBufferAssert assertThat(ByteBufferBase<?> buffer) {
        assert buffer instanceof AbstractByteBuffer;
        return new AbstractByteBufferAssert((AbstractByteBuffer) buffer);
    }
}
