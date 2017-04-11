package io.reptyl.test.unit.error;

import io.reptyl.error.ErrorHandler;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlerTest {

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

        new ErrorHandler(123).handleRequest(exchange);

        verify(sender).send(eq(""));
    }

    @Test
    public void nullMessageShouldProduceEmptyMessage() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException());

        new ErrorHandler(123).handleRequest(exchange);

        verify(sender).send(eq(""));
    }

    @Test
    public void emptyMessageShouldProduceEmptyMessage() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException(""));

        new ErrorHandler(123).handleRequest(exchange);

        verify(sender).send(eq(""));
    }

    @Test
    public void inputMessageAndStatusShouldBeSentToTheClient() throws Exception {

        when(exchange.getAttachment(THROWABLE)).thenReturn(new RuntimeException("TEST-EXCEPTION-MESSAGE"));

        ErrorHandler errorHandler = new ErrorHandler(123);

        errorHandler.handleRequest(exchange);

        verify(exchange).setStatusCode(eq(123));
        verify(sender).send(eq("TEST-EXCEPTION-MESSAGE"));
    }
}
