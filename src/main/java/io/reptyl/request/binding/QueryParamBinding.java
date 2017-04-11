package io.reptyl.request.binding;

import io.undertow.server.HttpServerExchange;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class QueryParamBinding implements Binding<String> {

    private final String queryParam;
    private final String defaultValue;

    public QueryParamBinding(final String queryParam, @Nullable final String defaultValue) {
        this.queryParam = queryParam;
        this.defaultValue = defaultValue;
    }

    @Override
    public Supplier<String> bindTo(HttpServerExchange exchange) {
        return () -> Optional.ofNullable(exchange.getQueryParameters().get(queryParam))
                .map(Deque::getFirst)
                .orElse(defaultValue);
    }
}
