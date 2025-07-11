package vvu.centrauthz.exceptions;

import vvu.centrauthz.domains.resources.models.Error;

/**
 * Application-specific error exception that carries an Error response.
 */
public class AppError extends RuntimeException {
    private final Error error;

    public AppError(Error error) {
        super(error.message());
        this.error = error;
    }

    public AppError(String code, String message) {
        super(message);
        this.error = Error.builder()
                .code(code)
                .message(message)
                .build();
    }

    public Error getError() {
        return error;
    }

    @Override
    public String toString() {
        return "AppError{" +
                "error=" + error +
                '}';
    }
}
