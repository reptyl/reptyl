package io.reptyl.request.handler;

import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.error.ExceptionHandlerException;
import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.ExchangeBinding;
import io.reptyl.request.binding.exception.NonAccessibleControllerException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;
import static io.undertow.util.StatusCodes.ACCEPTED;

public class MethodInvokerHandler implements HttpHandler {

    private final Object controller;
    private final Method method;
    private final List<Binding<?>> bindings;
    private final DefaultExceptionHandler defaultExceptionHandler;

    public MethodInvokerHandler(
            final Object controller,
            final Method method,
            final List<Binding<?>> bindings,
            final DefaultExceptionHandler defaultExceptionHandler) {
        this.controller = controller;
        this.method = method;
        this.bindings = bindings;
        this.defaultExceptionHandler = defaultExceptionHandler;
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

        // prepare the list of arguments
        Object[] args = bindings.stream()
                .map(o -> o.bindTo(exchange).get())
                .toArray(Object[]::new);

        // invoke the controller, marking the exchange as dispatched but using the current thread, to avoid switching
        // context to a worker thread
        exchange.dispatch(SameThreadExecutor.INSTANCE, () -> {
            try {
                method.invoke(controller, args);
            } catch (IllegalAccessException e) {
                throw new NonAccessibleControllerException(e, method);
            } catch (InvocationTargetException e) {
                exchange.putAttachment(THROWABLE, e.getCause());
                try {
                    defaultExceptionHandler.handleRequest(exchange);
                } catch (Exception e1) {
                    throw new ExceptionHandlerException(e1);
                }
            }
        });
    }
}
