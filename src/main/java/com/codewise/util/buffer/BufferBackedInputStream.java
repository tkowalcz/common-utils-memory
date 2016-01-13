package com.codewise.util.buffer;

import java.io.IOException;
import java.io.InputStream;

public class BufferBackedInputStream extends InputStream {

    private final ReadOnlyByteBuffer buffer;

    private boolean closed = false;

    public BufferBackedInputStream(ReadOnlyByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (buffer.hasRemaining()) {
            return Byte.toUnsignedInt(buffer.get());
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (buffer.hasRemaining()) {
            int bytesToGet = Math.toIntExact(Math.min(buffer.remaining(), b.length));
            buffer.get(b, 0, bytesToGet);
            return bytesToGet;
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (buffer.hasRemaining()) {
            int bytesToGet = Math.toIntExact(Math.min(buffer.remaining(), len));
            buffer.get(b, off, bytesToGet);
            return bytesToGet;
        } else {
            return -1;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        if (n > 0 && buffer.hasRemaining()) {
            long bytesToSkip = Math.min(buffer.remaining(), n);
            buffer.position(buffer.position() + bytesToSkip);
            return bytesToSkip;
        } else {
            return 0;
        }
    }

    @Override
    public int available() throws IOException {
        return Math.toIntExact(buffer.remaining());
    }
}
