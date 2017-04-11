package io.reptyl.route.exception;

import java.lang.reflect.Method;

public class NonVoidReturningControllerException extends RuntimeException {

    public NonVoidReturningControllerException(final Method method) {
        super("controller-annotated method " + method.getDeclaringClass().getName() + "." + method.getName() + "() does not return void");
    }
}
