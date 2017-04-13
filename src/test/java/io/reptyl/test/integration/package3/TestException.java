package io.reptyl.test.integration.package3;

import io.reptyl.error.ResponseStatus;

import static io.undertow.util.StatusCodes.INSUFFICIENT_STORAGE;

@ResponseStatus(INSUFFICIENT_STORAGE)
public class TestException extends RuntimeException {
}
