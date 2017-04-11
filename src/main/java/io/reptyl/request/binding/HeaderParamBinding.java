package io.reptyl.request.binding;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class HeaderParamBinding implements Binding<String> {

    private final String headerParam;
    private final String defaultValue;

    public HeaderParamBinding(final String headerParam, @Nullable final String defaultValue) {
        this.headerParam = headerParam;
        this.defaultValue = defaultValue;
    }

    @Override
    public Supplier<String> bindTo(HttpServerExchange exchange) {
        return () -> Optional.ofNullable(exchange.getRequestHeaders().get(headerParam))
                .map(HeaderValues::getFirst)
                .orElse(defaultValue);
    }
}
