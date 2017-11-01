package io.reptyl.request.binding;

import io.reptyl.request.binding.exception.EmptyHeaderParamAnnotationException;
import io.reptyl.request.binding.exception.EmptyPathParamAnnotationException;
import io.reptyl.request.binding.exception.EmptyQueryParamAnnotationException;
import io.reptyl.request.binding.exception.InvalidExchangeAnnotationException;
import io.reptyl.request.binding.exception.MultipleBindingOnTheSameParameterException;
import io.reptyl.request.binding.exception.UnboundParameterException;
import io.reptyl.request.binding.exception.UnsupportedBindingAnnotationException;
import io.undertow.server.HttpServerExchange;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableSet;

@Singleton
public class BindingFactory {

    private static final String JAX_RS_PACKAGE = "javax.ws.rs";

    private static final Set<Class<?>> SUPPORTED_ANNOTATIONS;

    private static final Set<Class<?>> UNSUPPORTED_ANNOTATIONS;

    static {

        Set<Class<?>> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(QueryParam.class);
        supportedAnnotations.add(PathParam.class);
        supportedAnnotations.add(HeaderParam.class);

        SUPPORTED_ANNOTATIONS = unmodifiableSet(supportedAnnotations);

        Set<Class<?>> unsupportedAnnotations = new HashSet<>();
        unsupportedAnnotations.add(MatrixParam.class);
        unsupportedAnnotations.add(FormParam.class);
        unsupportedAnnotations.add(CookieParam.class);

        UNSUPPORTED_ANNOTATIONS = unmodifiableSet(unsupportedAnnotations);
    }

    public Binding<?> getParameterBinding(Parameter parameter) {

        /*
         * EXCHANGE TYPE ARGUMENT
         */

        if (parameter.getType() == HttpServerExchange.class) {

            stream(parameter.getAnnotations())
                    .filter(annotation -> annotation.annotationType().getPackage().getName().startsWith(JAX_RS_PACKAGE))
                    .findAny()
                    .ifPresent(annotation -> {
                        throw new InvalidExchangeAnnotationException(parameter, annotation);
                    });

            return new ExchangeBinding();
        }

        /*
         * COUNT ANNOTATIONS
         */

        Long bindingAnnotationsCount = stream(parameter.getAnnotations())
                .filter(annotation -> SUPPORTED_ANNOTATIONS.contains(annotation.annotationType()))
                .count();

        if (bindingAnnotationsCount > 1) {
            throw new MultipleBindingOnTheSameParameterException();
        }

        /*
         * UNSUPPORTED ANNOTATIONS
         */

        stream(parameter.getAnnotations())
                .filter(annotation -> UNSUPPORTED_ANNOTATIONS.contains(annotation.annotationType()))
                .findAny()
                .ifPresent(annotation -> {
                    throw new UnsupportedBindingAnnotationException();
                });
        /*
         * DEFAULT VALUE
         */

        String defaultValue = stream(parameter.getAnnotations())
                .filter(annotation -> annotation instanceof DefaultValue)
                .findFirst()
                .map(DefaultValue.class::cast)
                .map(DefaultValue::value)
                .orElse(null);

        /*
         * BINDING GENERATION
         */

        for (Annotation annotation : parameter.getAnnotations()) {

            if (annotation instanceof QueryParam) {

                String value = ((QueryParam) annotation).value();

                if (value.isEmpty()) {
                    throw new EmptyQueryParamAnnotationException();
                }

                return new QueryParamBinding(((QueryParam) annotation).value(), defaultValue);
            }

            if (annotation instanceof PathParam) {

                String value = ((PathParam) annotation).value();

                if (value.isEmpty()) {
                    throw new EmptyPathParamAnnotationException();
                }

                return new PathParamBinding(value, defaultValue);
            }

            if (annotation instanceof HeaderParam) {

                String value = ((HeaderParam) annotation).value();

                if (value.isEmpty()) {
                    throw new EmptyHeaderParamAnnotationException();
                }

                return new HeaderParamBinding(value, defaultValue);
            }
        }

        throw new UnboundParameterException();
    }
}
