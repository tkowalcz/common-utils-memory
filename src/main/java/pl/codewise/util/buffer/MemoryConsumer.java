package pl.codewise.util.buffer;

@FunctionalInterface
public interface MemoryConsumer {

    void accept(byte[] memory, int offset, int length);
}
