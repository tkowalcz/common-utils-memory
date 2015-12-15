package com.codewise.util.buffer;

import java.io.IOException;
import java.io.InputStream;

public class ByteBufferInputStream extends InputStream {

    private ReadOnlyByteBuffer byteBuffer;

    public ByteBufferInputStream(ReadOnlyByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer.duplicate();
    }

    @Override
    public int read() throws IOException {
        if (byteBuffer.remaining() > 0) {
            return byteBuffer.get() & 0xFF;
        }

        return -1;
    }

    @Override
    public int read(byte[] array, int offset, int length) throws IOException {
        if (!byteBuffer.hasRemaining()) {
            return -1;
        }

        int lengthToRead = Math.min(byteBuffer.remaining(), length);
        byteBuffer.get(array, offset, lengthToRead);

        return lengthToRead;
    }

    @Override
    public long skip(long amount) throws IOException {
        int lengthToSkip = (int) Math.min(byteBuffer.remaining(), amount);
        byteBuffer.position(byteBuffer.position() + lengthToSkip);

        return lengthToSkip;
    }

    @Override
    public int available() throws IOException {
        return byteBuffer.remaining();
    }
}
