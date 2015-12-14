package pl.codewise.test.utils;

import com.google.common.base.Joiner;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

class MethodCallImpl<B> implements MethodCall<B> {
    private final Class<B> beanClass;
    private final String methodName;
    private final MethodHandle callHandle;

    public MethodCallImpl(Class<B> beanClass, String methodName, MethodHandle callHandle) {
        this.beanClass = beanClass;
        this.methodName = methodName;
        this.callHandle = callHandle;
    }

    @Override
    public Object call(B instance, Object... params) {
        try {
            return callHandle.bindTo(instance).invokeWithArguments(params);
        } catch (Throwable e) {
            String instanceInfo = null;
            try {
                instanceInfo = instance.toString();
            } catch (Exception e1) {
                instanceInfo = String.format("<toString() thrown %s>", e1.getClass().getSimpleName());
            }
            String paramsInfo = null;
            try {
                paramsInfo = Joiner.on(", ").join(params);
            } catch (Exception e1) {
                paramsInfo = String.format("<params toString() thrown %s>", e1.getClass().getSimpleName());
            }
            throw new MethodCallException(String.format("Exception when executing %s on %s with params %s", callHandle.toString(), instanceInfo, paramsInfo), e);
        }
    }

    @Override
    public String toString() {
        MethodType methodType = callHandle.type();
        String params = methodType.toString();
        String rType = params.substring(params.lastIndexOf(')') + 1);
        params = params.substring(0, params.lastIndexOf(')') + 1);

        return String.format("(%s) %s::%s%s", rType, beanClass.getSimpleName(), methodName, params);
    }
}
