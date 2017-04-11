package io.reptyl.error;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.Optional;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;

public class ErrorHandler implements HttpHandler {

    private final Integer responseCode;

    public ErrorHandler(final Integer responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String message = Optional.ofNullable(exchange.getAttachment(THROWABLE))
                .map(Throwable::getMessage)
                .orElse("");

        exchange.setStatusCode(responseCode);
        exchange.getResponseSender().send(message);
    }
}
