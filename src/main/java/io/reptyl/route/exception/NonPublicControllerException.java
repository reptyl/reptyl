package io.reptyl.route.exception;

import java.lang.reflect.Method;

public class NonPublicControllerException extends RuntimeException {

    public NonPublicControllerException(final Method method) {
        super("controller-annotated method " + method.getDeclaringClass().getName() + "." + method.getName() + "() is not public");
    }
}
