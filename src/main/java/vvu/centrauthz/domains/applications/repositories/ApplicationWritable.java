package vvu.centrauthz.domains.applications.repositories;

import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.models.Void;

public interface ApplicationWritable {
    Mono<Void> save(String appKey, Resource resource);
}
