package vvu.centrauthz.utilities.executors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility class that provides a safe execution wrapper for operations that might throw exceptions.
 * It allows for clean error handling and logging in a fluent API style.
 *
 * <p>The Executor is designed to wrap potentially failing operations and provide:
 * <ul>
 *   <li>Automatic exception handling with a fallback function</li>
 *   <li>Optional logging of errors</li>
 *   <li>Fluent API for configuration</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * String result = new Executor<String>(
 *     () -> someRiskyOperation(),
 *     e -> "fallback value"
 * ).withLogger(logger)
 * .execute();
 * }</pre>
 *
 * @param <T> the type of the result returned by the execution
 */
public class Executor<T> {

    protected Supplier<T> supplier;
    protected Function<Throwable, T> whenError;
    protected Logger logger;

    /**
     * Creates a new Executor with the specified operation and error handler.
     *
     * @param supplier the operation to execute
     * @param whenError the function to handle any exceptions that occur during execution
     * @throws NullPointerException if either supplier or whenError is null
     */
    public Executor(Supplier<T> supplier, Function<Throwable, T> whenError) {
        this.supplier = Objects.requireNonNull(supplier);
        this.whenError = Objects.requireNonNull(whenError);
    }

    /**
     * Executes the wrapped operation and returns its result.
     * If an exception occurs during execution, the error handler will be called
     * and its result will be returned instead.
     *
     * @return the result of the operation or the error handler
     */
    public T execute() {
        try {
            return supplier.get();
        } catch (Exception e) {

            Optional.ofNullable(logger).ifPresent(l -> {
                if (Objects.nonNull(e.getMessage())) {
                    l.error(e.getMessage());
                } else {
                    l.error(ExceptionUtils.getStackTrace(e));
                }
            });
            return whenError.apply(e);
        }
    }

    /**
     * Replaces the supplier used by this executor to generate the result.
     * The supplier replacement is used for all future calls to {@link #execute()}.
     * @param supplier the new supplier to use
     * @return this executor, for fluent API
     */
    /**
     * Replaces the supplier used by this executor.
     *
     * @param supplier the new supplier to use for execution
     * @return this executor instance for method chaining
     * @throws NullPointerException if the supplier is null
     */
    public Executor<T> withSupplier(Supplier<T> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
        return this;
    }

    /**
     * Replaces the error handler function used by this executor.
     *
     * @param whenError the function to handle exceptions
     * @return this executor instance for method chaining
     * @throws NullPointerException if the error handler is null
     */
    public Executor<T> withWhenError(Function<Throwable, T> whenError) {
        this.whenError = Objects.requireNonNull(whenError);
        return this;
    }

    /**
     * Sets a logger to be used for error reporting.
     * If set, any exceptions will be logged using this logger.
     *
     * @param logger the logger to use for error reporting
     * @return this executor instance for method chaining
     */
    public Executor<T> withLogger(Logger l) {
        this.logger = l;
        return this;
    }

}
