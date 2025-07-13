package vvu.centrauthz.utilities.executors;

import reactor.core.publisher.Flux;

import java.util.function.Supplier;

/**
 * A specialized executor for handling reactive {@link Flux} operations with built-in error handling.
 *
 * <p>This class extends {@link Executor} to work specifically with Reactor's {@link Flux} type,
 * providing a convenient way to execute reactive operations that produce multiple items with
 * automatic error handling that propagates errors through the reactive stream.
 *
 * <p>Example usage:
 * <pre>{@code
 * Flux<String> result = new FluxExecutor<>(() -> someFluxOperation())
 *     .withLogger(logger)
 *     .execute()
 *     .onErrorResume(e -> Flux.just("fallback"));
 * }</pre>
 *
 * @param <T> the type of elements in the Flux
 */
public class FluxExecutor<T> extends Executor<Flux<T>> {
    /**
     * Creates a new FluxExecutor with the specified Flux supplier.
     *
     * @param supplier the supplier that provides the Flux to be executed.
     *                 If the supplier throws an exception, it will be wrapped in a Flux.error()
     * @throws NullPointerException if the supplier is null
     */
    public FluxExecutor(Supplier<Flux<T>> supplier) {
        super(supplier, Flux::error);
    }
}
