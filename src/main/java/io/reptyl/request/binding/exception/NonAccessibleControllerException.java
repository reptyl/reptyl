package io.reptyl.request.binding.exception;

import java.lang.reflect.Method;

/**
 * Thrown when trying to invoke a controller method which is not accessible.
 */
public class NonAccessibleControllerException extends RuntimeException {

    public NonAccessibleControllerException(final IllegalAccessException e, final Method method) {
        super("the controller method " + method + " is not accessible", e);
    }
}
