package io.reptyl.route.exception;

import java.lang.reflect.Method;

public class EmptyControllerPathException extends RuntimeException {

    public EmptyControllerPathException(Method method) {
        super("empty path in controller " + method);
    }
}
