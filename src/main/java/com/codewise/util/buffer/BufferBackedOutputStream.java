package com.codewise.util.buffer;

import com.codewise.util.memory.Preconditions;

import java.io.IOException;
import java.io.OutputStream;

public class BufferBackedOutputStream extends OutputStream {

    private final MutableByteBuffer buffer;

    private boolean closed = false;

    public BufferBackedOutputStream(MutableByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        Preconditions.checkState(!closed);
        buffer.put((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        Preconditions.checkState(!closed);
        buffer.put(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        Preconditions.checkState(!closed);
        buffer.put(b, off, len);
    }

    @Override
    public void close() {
        closed = true;
    }
}