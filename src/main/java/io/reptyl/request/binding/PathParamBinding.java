package io.reptyl.request.binding;

import io.undertow.server.HttpServerExchange;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class PathParamBinding implements Binding<String> {

    private final String pathParam;
    private final String defaultValue;

    public PathParamBinding(final String pathParam, @Nullable final String defaultValue) {
        this.pathParam = pathParam;
        this.defaultValue = defaultValue;
    }

    @Override
    public Supplier<String> bindTo(HttpServerExchange exchange) {
        return () -> Optional.ofNullable(exchange.getPathParameters().get(pathParam))
                .map(Deque::getFirst)
                .orElse(defaultValue);
    }
}
