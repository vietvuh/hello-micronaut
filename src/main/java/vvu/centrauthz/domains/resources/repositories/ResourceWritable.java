package vvu.centrauthz.domains.resources.repositories;

import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.models.Void;

public interface ResourceWritable {
    /**
     * Saves a resource to the repository.
     *
     * @param appKey the application key
     * @param resource the resource to save
     * @return a Mono indicating completion
     */
    Mono<Void> save(String appKey, Resource resource);

}
