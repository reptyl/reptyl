package io.reptyl.test.unit.error;

import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.error.GlobalExceptionHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private DefaultExceptionHandler defaultExceptionHandler;

    @Mock
    private HttpServerExchange exchange;

    private GlobalExceptionHandler globalExceptionHandler;

    @Before
    public void setUp() throws Exception {
        globalExceptionHandler = new GlobalExceptionHandler(exceptionHandler, defaultExceptionHandler);
    }

    @Test
    public void delegateToExceptionHandler() throws Exception {

        globalExceptionHandler.handleRequest(exchange);

        verify(defaultExceptionHandler, never()).handleRequest(exchange);
    }

    @Test
    public void delegateToDefaultExceptionHandlerIfExceptionHandlerFails() throws Exception {

        doThrow(new RuntimeException())
                .when(exceptionHandler)
                .handleRequest(exchange);

        globalExceptionHandler.handleRequest(exchange);

        verify(defaultExceptionHandler, times(1)).handleRequest(exchange);
    }
}
