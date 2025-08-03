package vvu.centrauthz.domains.applications.repositories;

import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;

public interface ApplicationReadable {
    Mono<Resource> get(String appKey);
}
