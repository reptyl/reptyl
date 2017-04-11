package io.reptyl.test.unit.request.binding;

import io.reptyl.request.binding.BindingFactory;
import io.reptyl.request.binding.exception.MultipleBindingOnTheSameParameterException;
import io.reptyl.request.binding.exception.UnboundParameterException;
import io.reptyl.request.binding.exception.UnsupportedBindingAnnotationException;
import java.lang.reflect.Parameter;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.junit.Test;

import static io.reptyl.test.unit.ReflectionTestUtils.getParameter;
import static com.googlecode.catchexception.CatchException.verifyException;

public class BindingFactoryTest {

    @Test
    public void multipleJaxRsAnnotationsNotAllowed() {

        Parameter parameter = getParameter(Controller.class, "method1", String.class);

        verifyException(new BindingFactory(), MultipleBindingOnTheSameParameterException.class).getParameterBinding(parameter);
    }

    @Test
    public void disallowUnsupportedBindingAnnotations() {

        Parameter parameter = getParameter(Controller.class, "method2", String.class);

        verifyException(new BindingFactory(), UnsupportedBindingAnnotationException.class).getParameterBinding(parameter);
    }

    @Test
    public void disallowNonAnnotatedParameters() {

        Parameter parameter = getParameter(Controller.class, "method5", String.class);

        verifyException(new BindingFactory(), UnboundParameterException.class).getParameterBinding(parameter);
    }

    @SuppressWarnings("unused")   // because these methods are access through reflection only
    public class Controller {

        public void method1(@PathParam("param1") @QueryParam("param2") String s) {

        }

        public void method2(@MatrixParam("param") String s) {

        }

        public void method3(@FormParam("param") String s) {

        }

        public void method4(@CookieParam("param") String s) {

        }

        public void method5(String s) {

        }
    }
}
