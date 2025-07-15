package vvu.centrauthz.exceptions;

import vvu.centrauthz.models.Error;

public class NotFoundError extends AppError {
    public NotFoundError(Error error) {
        super(error);
    }

    public NotFoundError(String code, String message) {
        super(code, message);
    }

    public NotFoundError(String code) {
        super(code, null);
    }
}
