package io.reptyl.test.unit.request.handler;

import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.request.binding.ExchangeBinding;
import io.reptyl.request.handler.MethodInvokerHandler;
import io.reptyl.request.handler.MethodInvokerHandlerFactory;
import com.google.inject.Injector;
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
    private Injector injector;

    @Mock
    private DefaultExceptionHandler defaultExceptionHandler;

    @Mock
    private Method method;

    @Test
    public void createMethodInvokerHandler() {

        MethodInvokerHandler handler = new MethodInvokerHandlerFactory(injector, defaultExceptionHandler).getHandler(MethodInvokerHandlerFactoryTest.class, method, singletonList(new ExchangeBinding()));

        assertThat("a MethodInvokerHandler shold be created", handler, notNullValue());
    }
}
