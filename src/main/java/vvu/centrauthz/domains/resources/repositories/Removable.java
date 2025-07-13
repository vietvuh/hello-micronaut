package vvu.centrauthz.domains.resources.repositories;

import reactor.core.publisher.Mono;
import vvu.centrauthz.models.Void;

import java.util.UUID;

public interface Removable {

    /**
     * Deletes a resource from the repository.
     *
     * @param appKey the application key
     * @param id the ID of the resource to delete
     * @return a Mono indicating completion
     */
    Mono<Void> remove(String appKey, UUID id);
}
