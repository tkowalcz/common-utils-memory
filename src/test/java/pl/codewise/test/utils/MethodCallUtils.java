package pl.codewise.test.utils;

import com.google.common.base.Preconditions;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.mockito.verification.VerificationMode;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.mockito.Mockito.verify;

public class MethodCallUtils {

    public static <B> MethodCallVerificationStubbing<B> verifyCall(final MethodCall<B> methodCall) {
        return new MethodCallVerificationStubbing<B>() {
            private Object[] params = new Object[]{};

            @Override
            public MethodCallVerificationStubbing<B> withParams(Object... params) {
                Preconditions.checkArgument(params != null && params.length > 0);
                this.params = params;
                return this;
            }

            @Override
            public void wasCalledOn(B mock) {
                methodCall.call(verify(mock), params);
            }

            @Override
            public void wasCalledOn(B mock, VerificationMode mode) {
                methodCall.call(verify(mock, mode), params);
            }
        };
    }

    public static <B> MethodCallOngoingStubbing<B> whenCall(final MethodCall<B> methodCall) {
        return new MethodCallOngoingStubbing<B>() {
            private B mock;

            @Override
            public MethodCallOngoingStubbing<B> invokedOn(B mock) {
                this.mock = mock;
                return this;
            }

            @Override
            public OngoingStubbing<Object> withoutParams() {
                return Mockito.when(methodCall.call(mock));
            }

            @Override
            public OngoingStubbing<Object> withParams(Object... params) {
                Preconditions.checkArgument(params != null && params.length > 0);
                return Mockito.when(methodCall.call(mock, params));
            }
        };
    }

    private static SerializedLambda asSerializedLambda(Object lambda) {
        Preconditions.checkArgument(lambda != null && Serializable.class.isInstance(lambda));
        try {
            Class<?> lambdaClass = lambda.getClass();
            Method m = lambdaClass.getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            Object replacement = m.invoke(lambda);
            return (SerializedLambda) replacement;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Cannot access %s as serialized lambda", lambda.getClass().getSimpleName()), e);
        }
    }

    public static <M extends Serializable, B> MethodCall<B> methodForReference(M lambda) {
        try {
            SerializedLambda l = asSerializedLambda(lambda);

            @SuppressWarnings("unchecked")
            Class<B> beanClass = (Class<B>) Class.forName(l.getImplClass().replaceAll("/", "."));
            String methodName = l.getImplMethodName();
            MethodType methodType = MethodType.fromMethodDescriptorString(l.getImplMethodSignature(), beanClass.getClassLoader());
            MethodHandle callHandle = MethodHandles.lookup().in(beanClass).findVirtual(beanClass, methodName, methodType);

            return new MethodCallImpl<>(beanClass, methodName, callHandle);
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot get method handle for given lambda", e);
        }
    }

    public static <B> MethodCall<B> methodForCall(SerializableCallExample<B> callExample) {
        SerializedLambda l = asSerializedLambda(callExample);

        // get class of B
        @SuppressWarnings("unchecked")
        Class<B> beanClass = (Class<B>) MethodType.fromMethodDescriptorString(l.getImplMethodSignature(), callExample.getClass().getClassLoader()).parameterType(0);

        // make sure B is an interface - leaving cglib for later..
        Preconditions.checkArgument(beanClass.isInterface());
        // make an exploratory call... :)
        Object[] callSpecs = new Object[3];
        @SuppressWarnings("unchecked")
        B proxy = (B) Proxy.newProxyInstance(beanClass.getClassLoader(), new Class[]{beanClass}, (Object instance, Method method, Object[] args) -> {
            if (!Boolean.TRUE.equals(callSpecs[0])) {
                try {
                    callSpecs[0] = true;
                    callSpecs[1] = MethodHandles.lookup().in(beanClass).unreflect(method);
                    callSpecs[2] = method.getName();
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(String.format("Cannot get method handle for %s", method), e);
                }
            }
            Class<?> returnType = method.getReturnType();
            Object result = null;
            if (returnType.isPrimitive()) {
                if (byte.class == returnType) {
                    result = (byte) 0;
                } else if (short.class == returnType) {
                    result = (short) 0;
                } else if (char.class == returnType) {
                    result = 'a';
                } else if (int.class == returnType) {
                    return 0;
                } else if (long.class == returnType) {
                    return 0l;
                } else if (float.class == returnType) {
                    return 0.0f;
                } else if (double.class == returnType) {
                    return 0.0d;
                } else if (void.class == returnType) {
                } else {
                    throw new IllegalArgumentException(String.format("Methods with result type of %s are not supported", returnType.getSimpleName()));
                }
            }
            return result;
        });
        callExample.accept(proxy);
        // implement MethodCall... :D
        Preconditions.checkState(Boolean.TRUE.equals(callSpecs[0]));
        MethodHandle callHandle = (MethodHandle) callSpecs[1];
        String methodName = (String) callSpecs[2];
        return new MethodCallImpl<>(beanClass, methodName, callHandle);
    }
}