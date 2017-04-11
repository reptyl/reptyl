package io.reptyl.request.binding;

import io.undertow.server.HttpServerExchange;
import java.util.function.Supplier;

public interface Binding<T> {

    Supplier<T> bindTo(HttpServerExchange exchange);
}
