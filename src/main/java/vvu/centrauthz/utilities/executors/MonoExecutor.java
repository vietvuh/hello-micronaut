package vvu.centrauthz.utilities.executors;

import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * A specialized executor for handling reactive {@link Mono} operations with built-in error handling.
 *
 * <p>This class extends {@link Executor} to work specifically with Reactor's {@link Mono} type,
 * providing a convenient way to execute reactive operations that produce a single result with
 * automatic error handling that propagates errors through the reactive stream.
 *
 * <p>Example usage:
 * <pre>{@code
 * Mono<String> result = new MonoExecutor<>(() -> someMonoOperation())
 *     .withLogger(logger)
 *     .execute()
 *     .onErrorResume(e -> Mono.just("fallback"));
 * }</pre>
 *
 * @param <T> the type of the result in the Mono
 */
public class MonoExecutor<T> extends Executor<Mono<T>> {
    /**
     * Creates a new MonoExecutor with the specified Mono supplier.
     *
     * @param supplier the supplier that provides the Mono to be executed.
     *                 If the supplier throws an exception, it will be wrapped in a Mono.error()
     * @throws NullPointerException if the supplier is null
     */
    public MonoExecutor(Supplier<Mono<T>> supplier) {
        super(supplier, Mono::error);
    }
}
