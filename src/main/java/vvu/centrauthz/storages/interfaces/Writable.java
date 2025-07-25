package vvu.centrauthz.storages.interfaces;

import reactor.core.publisher.Mono;
import vvu.centrauthz.models.Void;

public interface Writable<T> {
    Mono<Void> save(String key, T object);
}
