package pl.codewise.util.buffer;

interface BytesWrappingCapableMemory extends ByteBufferMemory {

    void wrap(byte[] bytes);
}
