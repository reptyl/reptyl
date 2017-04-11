package io.reptyl.test.unit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ReflectionTestUtils {

    public static Parameter getParameter(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes).getParameters()[0];
        } catch (NoSuchMethodException e) {
            throw new Error("NoSuchMethodException: " + e.getMessage());
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new Error("NoSuchMethodException: " + e.getMessage());
        }
    }

    public static <T> T getField(Object o, String fieldName) {

        try {

            Field field = o.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            T value = (T) field.get(o);

            return value;

        } catch (NoSuchFieldException e) {
            throw new Error("NoSuchFieldException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new Error("IllegalAccessException: " + e.getMessage());
        }
    }
}
