package io.reptyl.request.handler;

import io.reptyl.Controller;
import io.reptyl.error.DefaultExceptionHandler;
import io.reptyl.request.binding.Binding;
import java.lang.reflect.Method;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MethodInvokerHandlerFactory {

    private final DefaultExceptionHandler defaultExceptionHandler;

    @Inject
    public MethodInvokerHandlerFactory(final DefaultExceptionHandler defaultExceptionHandler) {
        this.defaultExceptionHandler = defaultExceptionHandler;
    }

    public MethodInvokerHandler getHandler(
            final Controller controller,
            final Method method,
            final List<Binding<?>> bindings) {

        return new MethodInvokerHandler(controller, method, bindings, defaultExceptionHandler);
    }
}
