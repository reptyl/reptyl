package io.reptyl.request.binding.exception;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class InvalidExchangeAnnotationException extends RuntimeException {

    public InvalidExchangeAnnotationException(Parameter parameter, Annotation annotation) {
        super("HttpServerExchange parameters cannot be annotated with any JAX-RS annotation");
    }
}
