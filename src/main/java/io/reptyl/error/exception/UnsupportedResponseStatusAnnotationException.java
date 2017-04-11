package io.reptyl.error.exception;

public class UnsupportedResponseStatusAnnotationException extends RuntimeException {

    public UnsupportedResponseStatusAnnotationException(Class<?> clazz) {
        super("@ResponseStatus annotation unsupported on class " + clazz);
    }
}
