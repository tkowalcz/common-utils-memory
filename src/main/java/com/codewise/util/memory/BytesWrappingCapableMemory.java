package com.codewise.util.memory;

public interface BytesWrappingCapableMemory extends MutableMemory {

    void wrap(byte[] bytes);
}
