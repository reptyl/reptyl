package io.reptyl.test.unit.request.handler;

import io.reptyl.request.binding.ExchangeBinding;
import io.reptyl.request.handler.MethodInvokerHandler;
import io.reptyl.request.handler.exception.MethodInvocationException;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MethodInvokerHandlerTest {

    @Mock
    private HttpServerExchange exchange;

    @Mock
    private Sender sender;

    @Mock
    private Method method;

    @Test
    public void boundMethodShouldBeInvoked() throws Exception {

        Object controller = new Object();

        MethodInvokerHandler handler = new MethodInvokerHandler(controller, method, singletonList(new ExchangeBinding()));

        handler.handleRequest(exchange);

        verify(method, times(1)).invoke(eq(controller), any());
    }

    @Test
    public void asyncHandlerShouldAlwaysFinalizeTheExchange() throws Exception {

        when(exchange.getResponseSender()).thenReturn(sender);

        MethodInvokerHandler handler = new MethodInvokerHandler(new Object(), method, emptyList());

        handler.handleRequest(exchange);

        verify(sender, times(1)).send(anyString());
        verify(exchange, times(1)).setStatusCode(202);
    }

    @Test(expected = TestException.class)
    public void methodExceptionsShouldBeUnpacked() throws Exception {

        when(exchange.getResponseSender()).thenReturn(sender);
        when(method.invoke(any(), any())).thenThrow(new InvocationTargetException(new TestException()));

        MethodInvokerHandler handler = new MethodInvokerHandler(new Object(), method, emptyList());

        handler.handleRequest(exchange);
    }

    @Test(expected = MethodInvocationException.class)
    public void otherThrowablesShouldBeWrapped() throws Exception {

        when(exchange.getResponseSender()).thenReturn(sender);
        when(method.invoke(any(), any())).thenThrow(new InvocationTargetException(new Error()));

        MethodInvokerHandler handler = new MethodInvokerHandler(new Object(), method, emptyList());

        handler.handleRequest(exchange);
    }

    private static class TestException extends RuntimeException {

    }
}
