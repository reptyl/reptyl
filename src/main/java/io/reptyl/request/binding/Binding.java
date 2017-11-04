package io.reptyl.request.binding;

import io.undertow.server.HttpServerExchange;
import java.util.function.Supplier;

/**
 * Bind to a {@link HttpServerExchange}, extracting the right component, depending on implementation.
 * @param <T>
 */
public interface Binding<T> {

    Supplier<T> bindTo(HttpServerExchange exchange);
}
