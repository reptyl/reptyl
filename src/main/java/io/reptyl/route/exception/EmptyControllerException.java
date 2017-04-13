package io.reptyl.route.exception;

public class EmptyControllerException extends RuntimeException {

    public EmptyControllerException(Class<?> clazz) {
        super("no JAX-RS annotated methods in class " + clazz);
    }
}
