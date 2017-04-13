package io.reptyl.route.exception;

import javax.inject.Singleton;

@Singleton
public class NonSingletonControllerException extends RuntimeException {

    public NonSingletonControllerException(Class<?> clazz) {
        super("class " + clazz + " is not a @Singleton");
    }
}
