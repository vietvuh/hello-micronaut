package vvu.centrauthz.exceptions;

/**
 * Error Utilities
 */
public class EUtils {
    EUtils() {
        throw new IllegalStateException();
    }

    public static NotFoundError createNotFoundError(String message) {
        return new NotFoundError("NOT_FOUND", message);
    }

    public static BadRequestError createBadRequestError(String message) {
        return new BadRequestError(message);
    }

}
