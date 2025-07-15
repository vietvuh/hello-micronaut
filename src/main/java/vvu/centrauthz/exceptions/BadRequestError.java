package vvu.centrauthz.exceptions;

import vvu.centrauthz.models.Error;

public class BadRequestError extends AppError {
    public BadRequestError(String code, String message) {
        super(code, message);
    }

    public BadRequestError(String message) {
        super("BAD_REQUEST", message);
    }

    public BadRequestError(Error e) {
        super(e);
    }

    public BadRequestError() {
        super("BAD_REQUEST", null);
    }
}
