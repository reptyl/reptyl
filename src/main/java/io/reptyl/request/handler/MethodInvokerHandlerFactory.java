package io.reptyl.request.handler;

import io.reptyl.request.binding.Binding;
import com.google.inject.Injector;
import java.lang.reflect.Method;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MethodInvokerHandlerFactory {

    private final Injector injector;

    @Inject
    public MethodInvokerHandlerFactory(final Injector injector) {
        this.injector = injector;
    }

    public MethodInvokerHandler getHandler(
            final Class<?> controllerClass,
            final Method method,
            final List<Binding<?>> bindings) {

        return new MethodInvokerHandler(injector.getInstance(controllerClass), method, bindings);
    }
}
