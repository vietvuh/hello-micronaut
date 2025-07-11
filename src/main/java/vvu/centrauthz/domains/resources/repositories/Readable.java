package vvu.centrauthz.domains.resources.repositories;

import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;

import java.util.UUID;

public interface Readable {
    Mono<Resource> get(String appKey, UUID id);
}
