package vvu.centrauthz.storages.interfaces;

import reactor.core.publisher.Mono;

public interface Readable<T> {
    Mono<T> get(String key);
}
