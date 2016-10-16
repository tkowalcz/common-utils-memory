package pl.codewise.util.buffer;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedOffHeapByteBufferMemoryTest {

    @Test
    public void shouldPutIntToBuffer() {
        // given
        FixedOffHeapByteBufferMemory buffer = new FixedOffHeapByteBufferMemory(MemoryAccess.allocateMemory(1024), 1024);

        // when
        buffer.putInt(12, 432123);

        // then
        assertThat(buffer.getInt(12)).isEqualTo(432123);
    }

    @Test
    public void shouldPutLongToBuffer() {
        // given
        FixedOffHeapByteBufferMemory buffer = new FixedOffHeapByteBufferMemory(MemoryAccess.allocateMemory(1024), 1024);

        // when
        buffer.putLong(12, 4343215092123L);

        // then
        assertThat(buffer.getLong(12)).isEqualTo(4343215092123L);
    }
}
