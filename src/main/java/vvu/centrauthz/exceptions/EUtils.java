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

    public static NotImplementedError createNotImplementedError(String message) {
        return new NotImplementedError(message);
    }

    public static NotImplementedError createNotImplementedError() {
        return new NotImplementedError("Feature not implemented");
    }

}
