package vvu.centrauthz.domains.applications.repositories;

import reactor.core.publisher.Mono;
import vvu.centrauthz.models.Void;

import java.util.UUID;

public interface ApplicationRemovable {
    Mono<Void> remove(String appKey, UUID id);
}
