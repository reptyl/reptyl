package io.reptyl.test.unit.error;

import io.reptyl.error.ExceptionHandlerFactory;
import io.reptyl.error.exception.UnsupportedResponseStatusAnnotationException;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.reptyl.test.unit.ReflectionTestUtils.getField;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerFactoryTest {

    @Mock
    private RoutingHandler routingHandler;

    private ExceptionHandlerFactory exceptionHandlerFactory;

    @Before
    public void setUp() throws Exception {
        exceptionHandlerFactory = new ExceptionHandlerFactory(routingHandler);
    }

    @Test
    public void nonAnnotatedExceptionsShouldReturnEmptyExceptionHandler() {

        ExceptionHandler exceptionHandler = exceptionHandlerFactory.getFromPackage("io.reptyl.test.unit.error.nonexistingpackage");

        assertThat("an ExceptionHandler should be returned", exceptionHandler, notNullValue());
        assertThat("there should be no mapped exception handlers", getField(exceptionHandler, "exceptionHandlers"), empty());
    }

    @Test
    public void annotatedNonExceptionClassesShouldBeRejected() {

        verifyException(exceptionHandlerFactory, UnsupportedResponseStatusAnnotationException.class).getFromPackage("io.reptyl.test.unit.error.package1");
    }

    @Test
    public void validAnnotatedExceptionClass() {

        ExceptionHandler exceptionHandler = exceptionHandlerFactory.getFromPackage("io.reptyl.test.unit.error.package2");

        assertThat("an ExceptionHandler should be returned", exceptionHandler, notNullValue());
        assertThat("there should be no mapped exception handlers", getField(exceptionHandler, "exceptionHandlers"), hasSize(1));
    }
}
