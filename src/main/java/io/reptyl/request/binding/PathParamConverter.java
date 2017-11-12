package io.reptyl.request.binding;

@FunctionalInterface
public interface PathParamConverter<T> {

    T convert(String paramValue);
}
