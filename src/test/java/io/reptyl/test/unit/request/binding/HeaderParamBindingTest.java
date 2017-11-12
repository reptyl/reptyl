package io.reptyl.test.unit.request.binding;

import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.HeaderParamBinding;
import io.reptyl.request.binding.exception.EmptyHeaderParamAnnotationException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.lang.reflect.Parameter;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.reptyl.test.unit.ReflectionTestUtils.getParameter;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HeaderParamBindingTest {

    @Mock
    private HttpServerExchange exchange;

    @Test
    public void allowHeaderParamAnnotation() {

        Parameter parameter = getParameter(Controller.class, "method1", String.class);

        HeaderMap headers = new HeaderMap();
        headers.add(new HttpString("param"), "TEST-VALUE");

        when(exchange.getRequestHeaders()).thenReturn(headers);

        Binding<?> binding = new BindingFactory().getParameterBinding(parameter);

        assertThat("a binding should be created", parameter, notNullValue());
        assertThat("binding should be a HeaderParamBinding", binding, instanceOf(HeaderParamBinding.class));
        assertThat("the bound parameter should return the given value", binding.bindTo(exchange).get(), equalTo("TEST-VALUE"));
    }

    @Test(expected = EmptyHeaderParamAnnotationException.class)
    public void disallowEmptyAnnotation() {

        Parameter parameter = getParameter(Controller.class, "method2", String.class);

        new BindingFactory().getParameterBinding(parameter);
    }

    @Test
    public void applyDefaultValue() {

        Parameter parameter = getParameter(Controller.class, "method3", String.class);

        when(exchange.getRequestHeaders()).thenReturn(new HeaderMap());

        HeaderParamBinding binding = (HeaderParamBinding) new BindingFactory().getParameterBinding(parameter);

        assertThat("a binding should be created", parameter, notNullValue());
        assertThat("binding should be a HeaderParamBinding", binding, instanceOf(HeaderParamBinding.class));
        assertThat("a null bound parameter should return the default value", binding.bindTo(exchange).get(), equalTo("TEST-DEFAULT-VALUE"));
    }

    @SuppressWarnings("unused")   // because these methods are access through reflection only
    private class Controller {

        public void method1(@HeaderParam("param") String s) {

        }

        public void method2(@HeaderParam("") String s) {

        }

        public void method3(@HeaderParam("param") @DefaultValue("TEST-DEFAULT-VALUE") String s) {

        }
    }
}
