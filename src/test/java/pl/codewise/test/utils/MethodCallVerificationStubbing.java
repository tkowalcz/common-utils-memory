package pl.codewise.test.utils;

import org.mockito.verification.VerificationMode;

public interface MethodCallVerificationStubbing<B> {
    void wasCalledOn(B mock);

    void wasCalledOn(B mock, VerificationMode mode);

    MethodCallVerificationStubbing<B> withParams(Object... params);
}
