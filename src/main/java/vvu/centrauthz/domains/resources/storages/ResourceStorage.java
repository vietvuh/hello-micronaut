package vvu.centrauthz.domains.resources.storages;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.repositories.Readable;
import vvu.centrauthz.domains.resources.repositories.Removable;
import vvu.centrauthz.domains.resources.repositories.Writable;
import vvu.centrauthz.models.Void;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class ResourceStorage implements Readable, Removable, Writable {

    private final String prefix;

    static String buildKey(String prefix, String appKey, UUID id) {
        if (Objects.isNull(prefix) || prefix.isBlank()) {
            prefix = "RES";
        }

        return String.format("%s:%s:%s", prefix, appKey, id);

    }

    public ResourceStorage(@Value("${app.storage.namespace:RES}") String prefix) {
        this.prefix = prefix;
    }

    /**
     * Deletes a resource from the repository.
     *
     * @param appKey the application key
     * @param id     the ID of the resource to delete
     * @return a Mono indicating completion
     */
    @Override
    public Mono<Void> remove(String appKey, UUID id) {
        // TODO: Implement the logic to remove a resource from the storage
        return Mono.empty();
    }

    /**
     * Saves a resource to the repository.
     *
     * @param appKey   the application key
     * @param resource the resource to save
     * @return a Mono indicating completion
     */
    @Override
    public Mono<Void> save(String appKey, Resource resource) {
        // TODO: Implement the logic to save a resource to the storage
        return Mono.empty();
    }

    @Override
    public Mono<Resource> get(String appKey, UUID id) {
        // TODO: Implement the logic to get a resource to the storage
        return Mono.empty();
    }
}
