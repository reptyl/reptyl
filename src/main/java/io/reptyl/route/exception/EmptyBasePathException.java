package io.reptyl.route.exception;

import java.lang.reflect.Method;

public class EmptyBasePathException extends RuntimeException {

    public EmptyBasePathException(Method method) {
        super("empty base path for method " + method);
    }
}
