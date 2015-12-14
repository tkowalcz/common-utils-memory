package pl.codewise.test.utils;

@FunctionalInterface
public interface MethodCall<B> {
    Object call(B instance, Object... params);
}
