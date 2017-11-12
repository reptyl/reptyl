package io.reptyl.request.binding;

import io.reptyl.request.binding.exception.UnsupportedPathParamTypeException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class PathParamBinding<T> implements Binding<T> {

    private static final Map<Class<?>, PathParamConverter<?>> CONVERTERS = Map.of(
            Byte.class, Byte::valueOf,
            Short.class, Short::valueOf,
            Integer.class, Integer::valueOf,
            Long.class, Long::valueOf,
            Float.class, Float::valueOf,
            Double.class, Double::valueOf
    );

    private final String pathParam;
    private final String defaultValue;
    private final Class<T> type;

    public PathParamBinding(final String pathParam, @Nullable final String defaultValue, final Class<T> type) {
        this.pathParam = pathParam;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @Override
    public Supplier<T> bindTo(HttpServerExchange exchange) {

        return () -> {

            String paramValue = exchange
                    .getAttachment(PathTemplateMatch.ATTACHMENT_KEY)
                    .getParameters()
                    .getOrDefault(pathParam, defaultValue);

            if (!type.isAssignableFrom(String.class)) {

                @SuppressWarnings("unchecked")
                PathParamConverter<T> pathParamConverter = (PathParamConverter<T>) CONVERTERS.get(type);

                if (pathParamConverter == null) {
                    throw new UnsupportedPathParamTypeException();
                }

                return pathParamConverter.convert(paramValue);
            }

            // this cast is safe because paramValue is actually a String
            @SuppressWarnings("unchecked")
            T result = (T) paramValue;

            return result;
        };
    }
}
