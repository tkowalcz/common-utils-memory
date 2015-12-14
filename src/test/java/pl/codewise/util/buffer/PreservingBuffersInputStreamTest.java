package pl.codewise.util.buffer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class PreservingBuffersInputStreamTest {

    private byte[] in1;
    private byte[] in2;
    private byte[] in3;
    private ReadOnlyByteBuffer buf1;
    private ReadOnlyByteBuffer buf2;
    private ReadOnlyByteBuffer buf3;

    @BeforeMethod
    public void setUp() {
        in1 = new byte[1024];
        in2 = new byte[768];
        in3 = new byte[17];
        Random random = new Random();
        random.nextBytes(in1);
        random.nextBytes(in2);
        random.nextBytes(in3);
        buf1 = Buffers.wrap(in1);
        buf2 = Buffers.wrap(in2);
        buf3 = Buffers.wrap(in3);
    }

    @Test
    public void shouldCreateInputStreamForSingleBuffer() throws IOException {
        // Given
        PreservingBuffersInputStream in = new PreservingBuffersInputStream(buf1);
        byte[] out = new byte[4096];

        // When
        int read = IOUtils.read(in, out);

        // Then
        assertThat(read).isEqualTo(in1.length);
        assertThat(Arrays.copyOf(out, read)).isEqualTo(in1);
    }

    @Test
    public void shouldCreateInputStreamForTwoBuffers() throws IOException {
        // Given
        PreservingBuffersInputStream in = new PreservingBuffersInputStream(buf1, buf2);
        byte[] out = new byte[4096];

        // When
        int read = IOUtils.read(in, out);

        // Then
        assertThat(read).isEqualTo(in1.length + in2.length);
        assertThat(Arrays.copyOf(out, read)).isEqualTo(ArrayUtils.addAll(in1, in2));
    }

    @Test
    public void shouldCreateInputStreamForThreeBuffers() throws IOException {
        // Given
        PreservingBuffersInputStream in = new PreservingBuffersInputStream(buf1, buf2, buf3);
        byte[] out = new byte[4096];

        // When
        int read = IOUtils.read(in, out);

        // Then
        assertThat(read).isEqualTo(in1.length + in2.length + in3.length);
        assertThat(Arrays.copyOf(out, read)).isEqualTo(ArrayUtils.addAll(in1, ArrayUtils.addAll(in2, in3)));
    }

    @Test
    public void shouldResetBufferPositionAndLimit() throws IOException {
        // Given
        PreservingBuffersInputStream in = new PreservingBuffersInputStream(buf1, buf2, buf3);
        byte[] out = new byte[4096];
        IOUtils.read(in, out);

        // When
        in.close();

        // Then
        assertThat(buf1.position()).isEqualTo(0);
        assertThat(buf1.limit()).isEqualTo(in1.length);
        assertThat(buf2.position()).isEqualTo(0);
        assertThat(buf2.limit()).isEqualTo(in2.length);
        assertThat(buf3.position()).isEqualTo(0);
        assertThat(buf3.limit()).isEqualTo(in3.length);
    }


}
