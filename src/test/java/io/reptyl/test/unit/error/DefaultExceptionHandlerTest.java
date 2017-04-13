package io.reptyl.test.unit.error;

import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.error.ResponseStatus;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;
import static io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultExceptionHandlerTest {

    @Mock
    private HttpServerExchange exchange;

    @Mock
    private Sender sender;

    @Before
    public void setUp() throws Exception {
        when(exchange.getResponseSender()).thenReturn(sender);
    }

    @Test
    public void nonAttachedThrowableShouldProduceEmptyMessage() throws Exception {

        new DefaultExceptionHandler().handleRequest(exchange);

        verify(sender).send(eq(""));
    }

    @Test
    public void nullMessageShouldProduceEmptyMessage() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException());

        new DefaultExceptionHandler().handleRequest(exchange);

        verify(sender).send(eq(""));
    }

    @Test
    public void emptyMessageShouldProduceEmptyMessage() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException(""));

        new DefaultExceptionHandler().handleRequest(exchange);

        verify(sender).send(eq(""));
    }

    @Test
    public void exceptionMessageShouldBeSentToTheClient() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException("TEST-EXCEPTION-MESSAGE"));

        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

        defaultExceptionHandler.handleRequest(exchange);

        verify(sender).send(eq("TEST-EXCEPTION-MESSAGE"));
    }

    @Test
    public void nonAnnotatedExceptionShouldProduceInternalServerError() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException());

        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

        defaultExceptionHandler.handleRequest(exchange);

        verify(exchange).setStatusCode(eq(INTERNAL_SERVER_ERROR));
    }

    @Test
    public void annotatedExceptionShouldReturnTheExceptionStatusCode() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new TestException());

        DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

        defaultExceptionHandler.handleRequest(exchange);

        verify(exchange).setStatusCode(eq(123));
    }

    @ResponseStatus(123)
    private static class TestException extends RuntimeException {

    }
}
