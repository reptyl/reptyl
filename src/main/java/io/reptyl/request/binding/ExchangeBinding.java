package io.reptyl.request.binding;

import io.undertow.server.HttpServerExchange;
import java.util.function.Supplier;

public class ExchangeBinding implements Binding<HttpServerExchange> {

    @Override
    public Supplier<HttpServerExchange> bindTo(HttpServerExchange exchange) {
        return () -> exchange;
    }
}
