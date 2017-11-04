package io.reptyl;

public class UtilityClassInstantiationError extends AssertionError {

    public UtilityClassInstantiationError() {
        super("this utility class cannot be instantiated");
    }
}
