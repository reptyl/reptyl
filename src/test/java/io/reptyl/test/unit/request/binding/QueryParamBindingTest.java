package io.reptyl.test.unit.request.binding;

import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.QueryParamBinding;
import io.reptyl.request.binding.exception.EmptyQueryParamAnnotationException;
import io.reptyl.test.unit.ReflectionTestUtils;
import io.undertow.server.HttpServerExchange;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.googlecode.catchexception.CatchException.verifyException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryParamBindingTest {

    @Mock
    private HttpServerExchange exchange;

    @Test
    public void allowQueryParamAnnotation() {

        Parameter parameter = ReflectionTestUtils.getParameter(Controller.class, "method1", String.class);

        Deque<String> parameterValues = new ArrayDeque<>();
        parameterValues.add("TEST-VALUE");

        HashMap<String, Deque<String>> parameters = new HashMap<>();
        parameters.put("param", parameterValues);

        when(exchange.getQueryParameters()).thenReturn(parameters);

        Binding<?> binding = new BindingFactory().getParameterBinding(parameter);

        assertThat("a binding should be created", parameter, notNullValue());
        assertThat("binding should be a QueryParamBinding", binding, instanceOf(QueryParamBinding.class));
        assertThat("the bound parameter should return the given value", binding.bindTo(exchange).get(), equalTo("TEST-VALUE"));
    }

    @Test
    public void disallowEmptyAnnotation() {

        Parameter parameter = ReflectionTestUtils.getParameter(Controller.class, "method2", String.class);

        verifyException(new BindingFactory(), EmptyQueryParamAnnotationException.class).getParameterBinding(parameter);
    }

    @Test
    public void applyDefaultValue() {

        Parameter parameter = ReflectionTestUtils.getParameter(Controller.class, "method3", String.class);

        when(exchange.getQueryParameters()).thenReturn(new HashMap<>());

        QueryParamBinding binding = (QueryParamBinding) new BindingFactory().getParameterBinding(parameter);

        assertThat("a binding should be created", parameter, notNullValue());
        assertThat("binding should be a QueryParamBinding", binding, instanceOf(QueryParamBinding.class));
        assertThat("a null bound parameter should return the default value", binding.bindTo(exchange).get(), equalTo("TEST-DEFAULT-VALUE"));
    }

    @SuppressWarnings("unused")   // because these methods are access through reflection only
    private class Controller {

        public void method1(@QueryParam("param") String s) {

        }

        public void method2(@QueryParam("") String s) {

        }

        public void method3(@QueryParam("param") @DefaultValue("TEST-DEFAULT-VALUE") String s) {

        }
    }
}
