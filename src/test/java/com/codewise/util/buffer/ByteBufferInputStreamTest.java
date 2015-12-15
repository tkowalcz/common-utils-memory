package com.codewise.util.buffer;

import org.apache.commons.lang3.RandomUtils;
import org.testng.annotations.Test;

import java.io.BufferedInputStream;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;
import static org.assertj.core.api.Assertions.assertThat;

public class ByteBufferInputStreamTest {

    public static final int BYTE_OFFSET = 50;

    @Test
    public void shouldReturnByteValueCorrectlyConvertedToInt() throws Exception {
        // Given
        byte[] bytes = new byte[]{-117};
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes);

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When & Then
        assertThat(cut.read()).isEqualTo(139);
    }

    @Test
    public void shouldReadBytesOneByOne() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When & Then
        for (int i = 0; i < bytes.length - BYTE_OFFSET; i++) {
            assertThat(cut.read()).isEqualTo(Byte.toUnsignedInt(bytes[i + BYTE_OFFSET]));
        }

        assertThat(cut.read()).isEqualTo(-1);
    }

    @Test
    public void shouldReadBytesInBatches() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] buffer1 = new byte[150];
        byte[] buffer2 = new byte[150];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        int bytesRead1 = cut.read(buffer1, 0, buffer1.length);
        int bytesRead2 = cut.read(buffer2, 0, buffer2.length);

        // Then
        assertThat(bytesRead1).isEqualTo(150);
        assertThat(bytesRead2).isEqualTo(56);

        assertThat(buffer1).isEqualTo(copyOfRange(bytes, BYTE_OFFSET, BYTE_OFFSET + 150));
        assertThat(copyOfRange(buffer2, 0, 56)).isEqualTo(copyOfRange(bytes, BYTE_OFFSET + 150, 256));
        assertThat(copyOfRange(buffer2, 56, 150)).containsOnly((byte) 0);
    }

    @Test
    public void testReadToByteArrayWithOffset() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] actual = new byte[206];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        int bytesRead1 = cut.read(actual, 0, 121);
        int bytesRead2 = cut.read(actual, 121, 79);
        int bytesRead3 = cut.read(actual, 200, 6);

        // Then
        assertThat(bytesRead1).isEqualTo(121);
        assertThat(bytesRead2).isEqualTo(79);
        assertThat(bytesRead3).isEqualTo(6);

        assertThat(actual).isEqualTo(Arrays.copyOfRange(bytes, BYTE_OFFSET, 256));
    }

    @Test
    public void shouldNotReadPastEndOfBuffer() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] actual = new byte[206];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        int bytesRead1 = cut.read(actual);
        int bytesRead2 = cut.read(actual);

        // Then
        assertThat(bytesRead1).isEqualTo(206);
        assertThat(bytesRead2).isEqualTo(-1);

        assertThat(actual).isEqualTo(Arrays.copyOfRange(bytes, BYTE_OFFSET, 256));
    }

    @Test
    public void shouldSkipBytes() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] actual = new byte[206];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        long bytesSkipped1 = cut.skip(10);
        int bytesRead1 = cut.read(actual, 0, 10);
        long bytesSkipped2 = cut.skip(12);
        int bytesRead2 = cut.read(actual, 10, 10);

        // Then
        assertThat(bytesSkipped1).isEqualTo(10);
        assertThat(bytesRead1).isEqualTo(10);
        assertThat(bytesSkipped2).isEqualTo(12);
        assertThat(bytesRead2).isEqualTo(10);

        for (int i = 0; i < 10; i++) {
            assertThat(actual[i]).isEqualTo(bytes[i + 10 + BYTE_OFFSET]);
        }

        for (int i = 0; i < 10; i++) {
            assertThat(actual[i + 10]).isEqualTo(bytes[i + 20 + 12 + BYTE_OFFSET]);
        }
    }

    @Test
    public void shouldNotSkipPastEndOfBuffer() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] actual = new byte[206];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        int bytesRead1 = cut.read(actual, 0, 10);
        long bytesSkipped1 = cut.skip(1023);
        int bytesRead2 = cut.read(actual, 10, 10);
        long bytesSkipped2 = cut.skip(10);

        // Then
        assertThat(bytesRead1).isEqualTo(10);
        assertThat(bytesSkipped1).isEqualTo(196);
        assertThat(bytesRead2).isEqualTo(-1);
        assertThat(bytesSkipped2).isEqualTo(0);

        for (int i = 0; i < 10; i++) {
            assertThat(actual[i]).isEqualTo(bytes[i + BYTE_OFFSET]);
        }
    }

    @Test
    public void shouldReturnAvailableBytes() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        int available = cut.available();

        // Then
        assertThat(available).isEqualTo(206);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void shouldReturnAvailableBytesAfterReadingAndSkipping() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] actual = new byte[206];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        cut.read();
        cut.read(actual, 0, 42);
        cut.skip(32);
        cut.read();

        int available = cut.available();

        // Then
        assertThat(available).isEqualTo(130);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testWithStream() throws Exception {
        // Given
        byte[] bytes = RandomUtils.nextBytes(256);
        MutableByteBuffer byteBuffer = Buffers.wrap(bytes)
                .position(BYTE_OFFSET)
                .sliceMe();

        byte[] actual = new byte[121];

        ByteBufferInputStream cut = new ByteBufferInputStream(byteBuffer);

        // When
        BufferedInputStream inputStream = new BufferedInputStream(cut);
        inputStream.skip(10);
        inputStream.read(actual);

        // Then
        assertThat(actual).isEqualTo(Arrays.copyOfRange(bytes, 10 + BYTE_OFFSET, 131 + BYTE_OFFSET));
    }
}
