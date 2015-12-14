package pl.codewise.test.utils;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SerializableCallExample<B> extends Consumer<B>, Serializable {
}
