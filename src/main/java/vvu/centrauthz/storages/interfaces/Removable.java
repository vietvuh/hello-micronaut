package vvu.centrauthz.storages.interfaces;

import reactor.core.publisher.Mono;
import vvu.centrauthz.models.Void;

public interface Removable {
    Mono<Void> remove(String appKey);
}
