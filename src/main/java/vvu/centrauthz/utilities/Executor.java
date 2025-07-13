package vvu.centrauthz.utilities;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vvu.centrauthz.utilities.executors.FluxExecutor;
import vvu.centrauthz.utilities.executors.MonoExecutor;

import java.util.function.Supplier;

public class Executor {

    Executor() {
        throw new IllegalStateException();
    }

    public static <T> MonoExecutor<T> mono(Supplier<Mono<T>> supplier) {
        return new MonoExecutor<>(supplier);
    }

    public static <T> FluxExecutor<T> flux(Supplier<Flux<T>> supplier) {
        return new FluxExecutor<>(supplier);
    }

}
