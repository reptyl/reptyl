package io.reptyl.request.handler;

import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.ExchangeBinding;
import io.reptyl.request.handler.exception.MethodInvocationException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static io.undertow.util.StatusCodes.ACCEPTED;

public class MethodInvokerHandler implements HttpHandler {

    private final Object controller;
    private final Method method;
    private final List<Binding<?>> bindings;

    public MethodInvokerHandler(
            final Object controller,
            final Method method,
            final List<Binding<?>> bindings) {
        this.controller = controller;
        this.method = method;
        this.bindings = bindings;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        Boolean exchangeIsBound = bindings
                .stream()
                .anyMatch(binding -> binding instanceof ExchangeBinding);

        if (!exchangeIsBound) {
            exchange.setStatusCode(ACCEPTED);
            exchange.getResponseSender().send("");
        }

        try {
            Object[] args = bindings.stream()
                    .map(o -> o.bindTo(exchange).get())
                    .toArray(Object[]::new);
            method.invoke(controller, args);
        } catch (InvocationTargetException e) {

            Throwable cause = e.getCause();

            if (cause instanceof Exception) {
                throw (Exception) cause;
            } else {
                throw new MethodInvocationException(cause);
            }
        }
    }
}
