package io.reptyl.error;

/**
 * Thrown when an exception is thrown during exception handling.
 */
public class ExceptionHandlerException extends RuntimeException {

    public ExceptionHandlerException(final Exception e) {
        super("unexpected exception occured handling controller exception", e);
    }
}
