package io.reptyl.error;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import javax.inject.Inject;
import javax.inject.Singleton;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;

@Singleton
public class GlobalExceptionHandler implements HttpHandler {

    private final ExceptionHandler exceptionHandler;
    private final DefaultExceptionHandler defaultExceptionHandler;

    @Inject
    public GlobalExceptionHandler(
            final ExceptionHandler exceptionHandler,
            final DefaultExceptionHandler defaultExceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        this.defaultExceptionHandler = defaultExceptionHandler;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        try {
            exceptionHandler.handleRequest(exchange);
        } catch (Exception e) {
            exchange.putAttachment(THROWABLE, e);
            defaultExceptionHandler.handleRequest(exchange);
        }
    }
}
