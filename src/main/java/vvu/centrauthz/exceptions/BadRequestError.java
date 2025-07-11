package vvu.centrauthz.exceptions;

public class BadRequestError extends AppError {
    public BadRequestError(String code, String message) {
        super(code, message);
    }

    public BadRequestError(String message) {
        super("BAD_REQUEST", message);
    }

    public BadRequestError() {
        super("BAD_REQUEST", null);
    }
}
