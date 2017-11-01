package io.reptyl.error;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;
import static io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR;

@Singleton
public class DefaultExceptionHandler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        String message = "";
        Integer statusCode = INTERNAL_SERVER_ERROR;

        Throwable throwable = exchange.getAttachment(THROWABLE);

        if (throwable != null) {

            if (throwable.getMessage() != null) {
                message = throwable.getMessage();
            }

            ResponseStatus responseStatus = throwable.getClass().getDeclaredAnnotation(ResponseStatus.class);

            if (responseStatus != null) {
                statusCode = responseStatus.value();
            }
        }

        exchange.setStatusCode(statusCode);
        exchange.getResponseSender().send(message);
    }
}
