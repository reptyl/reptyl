package io.reptyl.test.unit.request.binding;

import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.ExchangeBinding;
import io.reptyl.request.binding.exception.InvalidExchangeAnnotationException;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Parameter;
import javax.annotation.Nullable;
import javax.ws.rs.PathParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.reptyl.test.unit.ReflectionTestUtils.getParameter;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeBindingTest {

    @Mock
    private HttpServerExchange exchange;

    @Test
    public void exchangeParameterMustNotBeAnnotated() throws NoSuchMethodException {

        Parameter parameter = getParameter(Controller.class, "method1", HttpServerExchange.class);

        verifyException(new BindingFactory(), InvalidExchangeAnnotationException.class).getParameterBinding(parameter);
    }

    @Test
    public void allowOtherAnnotations() throws NoSuchMethodException {

        Parameter parameter = getParameter(Controller.class, "method2", HttpServerExchange.class);

        Binding<?> binding = new BindingFactory().getParameterBinding(parameter);

        assertThat("non JAX-RS annotations should be allowed on a HttpServerExchange parameter", binding, notNullValue());
        assertThat("binding should be an ExchangeBinding", binding, instanceOf(ExchangeBinding.class));
        assertThat("an exchange binding should return the bound exchange itself", binding.bindTo(exchange).get(), equalTo(exchange));
    }

    @Test
    public void exchangeParameterWithNoAnnotations() throws NoSuchMethodException {

        Parameter parameter = getParameter(Controller.class, "method3", HttpServerExchange.class);

        Binding<?> binding = new BindingFactory().getParameterBinding(parameter);

        assertThat("non annotated HttpServerExchange parameter should be allowed", binding, notNullValue());
        assertThat("binding should be an ExchangeBinding", binding, instanceOf(ExchangeBinding.class));
        assertThat("an exchange binding should return the bound exchange itself", binding.bindTo(exchange).get(), equalTo(exchange));
    }

    @SuppressWarnings("unused")   // because these methods are access through reflection only
    public class Controller {

        public void method1(@PathParam("") HttpServerExchange exchange) {

        }

        public void method2(@Nullable HttpServerExchange exchange) {

        }

        public void method3(HttpServerExchange exchange) {

        }
    }
}