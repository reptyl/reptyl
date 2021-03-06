package io.reptyl.test.unit.request.binding;

import io.reptyl.request.binding.Binding;
import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.binding.PathParamBinding;
import io.reptyl.request.binding.exception.EmptyPathParamAnnotationException;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import java.lang.reflect.Parameter;
import java.util.Map;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.reptyl.test.unit.ReflectionTestUtils.getParameter;
import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathParamBindingTest {

    @Mock
    private HttpServerExchange exchange;

    @Test
    public void allowPathParamAnnotation() {

        Parameter parameter = getParameter(Controller.class, "method1", String.class);

        when(exchange.getAttachment(eq(PathTemplateMatch.ATTACHMENT_KEY))).thenReturn(new PathTemplateMatch("/test", Map.of("param", "TEST-VALUE")));

        Binding<?> binding = new BindingFactory().getParameterBinding(parameter);

        assertThat("a binding should be created", parameter, notNullValue());
        assertThat("binding should be a PathParamBinding", binding, instanceOf(PathParamBinding.class));
        assertThat("the bound parameter should return the given value", binding.bindTo(exchange).get(), equalTo("TEST-VALUE"));
    }

    @Test(expected = EmptyPathParamAnnotationException.class)
    public void disallowEmptyAnnotation() {

        Parameter parameter = getParameter(Controller.class, "method2", String.class);

        new BindingFactory().getParameterBinding(parameter);
    }

    @Test
    public void applyDefaultValue() {

        Parameter parameter = getParameter(Controller.class, "method3", String.class);

        PathParamBinding binding = (PathParamBinding) new BindingFactory().getParameterBinding(parameter);
        when(exchange.getAttachment(eq(PathTemplateMatch.ATTACHMENT_KEY))).thenReturn(new PathTemplateMatch("/test", emptyMap()));

        assertThat("a binding should be created", parameter, notNullValue());
        assertThat("binding should be a PathParamBinding", binding, instanceOf(PathParamBinding.class));
        assertThat("a null bound parameter should return the default value", binding.bindTo(exchange).get(), equalTo("TEST-DEFAULT-VALUE"));
    }

    @SuppressWarnings("unused")   // because these methods are access through reflection only
    private class Controller {

        public void method1(@PathParam("param") String s) {

        }

        public void method2(@PathParam("") String s) {

        }

        public void method3(@PathParam("param") @DefaultValue("TEST-DEFAULT-VALUE") String s) {

        }
    }
}
