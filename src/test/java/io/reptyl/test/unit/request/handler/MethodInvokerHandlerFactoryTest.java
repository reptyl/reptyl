package io.reptyl.test.unit.request.handler;

import io.reptyl.Controller;
import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.request.binding.ExchangeBinding;
import io.reptyl.request.handler.MethodInvokerHandler;
import io.reptyl.request.handler.MethodInvokerHandlerFactory;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MethodInvokerHandlerFactoryTest {

    @Mock
    private DefaultExceptionHandler defaultExceptionHandler;

    @Mock
    private Method method;

    @Mock
    private Controller controller;

    @Test
    public void createMethodInvokerHandler() {

        MethodInvokerHandler handler = new MethodInvokerHandlerFactory(defaultExceptionHandler).getHandler(controller, method, singletonList(new ExchangeBinding()));

        assertThat("a MethodInvokerHandler shold be created", handler, notNullValue());
    }
}
