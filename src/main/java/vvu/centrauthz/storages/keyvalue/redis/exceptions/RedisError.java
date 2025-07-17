package vvu.centrauthz.storages.keyvalue.redis.exceptions;

import vvu.centrauthz.exceptions.AppError;

public class RedisError extends AppError {
    public RedisError(Throwable e) {
        super("STORAGE_ERROR", e);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
