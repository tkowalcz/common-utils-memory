package com.codewise.util.buffer;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static java.util.stream.Collectors.toList;

/**
 * Restores original position and limit on close.
 */
public class PreservingBuffersInputStream extends FilterInputStream {

    private final long[][] originalPositionAndLimit;
    private final ReadOnlyByteBuffer[] buffers;

    public PreservingBuffersInputStream(ReadOnlyByteBuffer... buffers) {
        super(toStream(buffers));
        this.originalPositionAndLimit = getPositionAndLimit(buffers);
        this.buffers = buffers;
    }

    @Override
    public void close() throws IOException {
        restorePositionAndLimits();
        super.close();
    }

    @Override
    public int available() throws IOException {
        return Buffers.sumRemaining(buffers);
    }

    private static InputStream toStream(ReadOnlyByteBuffer... bufs) {
        if (bufs.length == 1) {
            return new BufferBackedInputStream(bufs[0]);
        } else if (bufs.length == 2) {
            return new SequenceInputStream(new BufferBackedInputStream(bufs[0]), new BufferBackedInputStream(bufs[1]));
        } else {
            return new SequenceInputStream(enumeration(asList(bufs).stream().map(BufferBackedInputStream::new).collect(toList())));
        }
    }

    private static long[][] getPositionAndLimit(ReadOnlyByteBuffer... bufs) {
        long[][] positionAndLimit = new long[bufs.length][];
        for (int i = 0; i < bufs.length; i++) {
            positionAndLimit[i] = new long[]{bufs[i].position(), bufs[i].limit()};
        }
        return positionAndLimit;
    }

    private void restorePositionAndLimits() {
        for (int i = 0; i < buffers.length; i++) {
            buffers[i].position(originalPositionAndLimit[i][0]).limit(originalPositionAndLimit[i][1]);
        }
    }
}
