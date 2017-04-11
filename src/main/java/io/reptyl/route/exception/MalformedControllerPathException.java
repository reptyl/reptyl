package io.reptyl.route.exception;

import java.lang.reflect.Method;

public class MalformedControllerPathException extends RuntimeException {

    public MalformedControllerPathException(String path, Method method) {
        super("malformed path " + path + " in controller " + method.getName());
    }
}
