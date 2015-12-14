package pl.codewise.test.utils;

import org.mockito.stubbing.OngoingStubbing;

public interface MethodCallOngoingStubbing<B> {
    MethodCallOngoingStubbing<B> invokedOn(B mock);

    OngoingStubbing<Object> withoutParams();

    OngoingStubbing<Object> withParams(Object... params);
}
