package vvu.centrauthz.domains.resources.services;


import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.ResourceForPatch;
import vvu.centrauthz.domains.resources.repositories.ResourceReadable;
import vvu.centrauthz.domains.resources.repositories.ResourceRemovable;
import vvu.centrauthz.domains.resources.repositories.ResourceWritable;
import vvu.centrauthz.exceptions.ConflictError;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.utilities.Context;
import vvu.centrauthz.utilities.Executor;

import java.util.Objects;
import java.util.UUID;

@Singleton
@Slf4j
public class ResourceService {

    private final ResourceReadable resourceReadable;
    private final ResourceWritable resourceWritable;
    private final ResourceRemovable resourceRemovable;

    public ResourceService(
            ResourceReadable resourceReadable,
            ResourceWritable resourceWritable,
            ResourceRemovable resourceRemovable) {
        this.resourceReadable = Objects.requireNonNull(resourceReadable);
        this.resourceWritable = Objects.requireNonNull(resourceWritable);
        this.resourceRemovable = Objects.requireNonNull(resourceRemovable);
    }

    static NotFoundError resourceNotFound(String appKey, UUID id) {
        String eMess = String.format("Resource with ID %s not found for application %s", id, appKey);
        return EUtils.createNotFoundError(eMess);
    }

    private Mono<Resource> getResource(String appKey, UUID id) {
        return resourceReadable
                .get(appKey, id)
                .switchIfEmpty(Mono.error(resourceNotFound(appKey, id)));
    }

    public Mono<Resource> get(String appKey, UUID id, Context context) {
        return Executor.mono(() -> getResource(appKey, id))
            .withLogger(log)
            .withContext(context)
            .execute();
    }

    private Mono<Resource> saveNewResource(String appKey, Resource resource, Context context) {
        var newRes = resource
                .toBuilder()
                .createdBy(context.user().id())
                .createdAt(System.currentTimeMillis()).build();
        return resourceWritable.save(appKey, newRes).map(v -> newRes);
    }

    private Mono<Resource> createResource(String appKey, final Resource resource, Context context) {

        if (Objects.isNull(resource.id())) {
            var r = resource
                    .toBuilder()
                    .id(UUID.randomUUID())
                    .build();
            return saveNewResource(appKey, r, context);
        }

        String eMess = String.format("Resource with ID %s is existing", resource.id());

        return getResource(appKey, resource.id())
                .flatMap(r -> Mono.<Resource>error(new ConflictError(eMess)))
                .doOnNext( r -> log.info("Resource already exists: {}", r))
                .onErrorResume( throwable -> {
                    if (throwable instanceof NotFoundError) {
                        return saveNewResource(appKey, resource, context);
                    }
                    return Mono.error(throwable);
                });
    }

    public Mono<Resource> create(String appKey, final Resource resource, Context context) {
        return Executor
                .mono(() -> createResource(appKey, resource, context))
                .withLogger(log)
                .withContext(context)
                .execute();
    }

    private Mono<Void> saveResource(String appKey, Resource resource, Context context) {
        return getResource(appKey, resource.id())
                .flatMap(r -> {
                    r = r.toBuilder()
                            .updatedBy(context.user().id())
                            .updatedAt(System.currentTimeMillis())
                            .build();
                    return resourceWritable.save(appKey, r);
                });
    }

    public Mono<Void> save(String appKey, Resource resource, Context context) {
        return Executor
                .mono(() -> saveResource(appKey, resource, context))
                .withLogger(log)
                .withContext(context)
                .execute();
    }

    private Mono<Void> patchResource(String appKey, UUID id, ResourceForPatch patcher, Context context) {

        return getResource(appKey, id)
                .flatMap(resource -> {
                    var patchedResource = resource.patch(patcher);
                    patchedResource = patchedResource
                            .toBuilder()
                            .updatedBy(context.user().id())
                            .updatedAt(System.currentTimeMillis())
                            .build();
                    return resourceWritable.save(appKey, patchedResource);
                });
    }

    public Mono<Void> patch(String appKey, UUID id, ResourceForPatch patcher, Context context) {
        return Executor
                .mono(() -> patchResource(appKey, id, patcher, context))
                .withLogger(log)
                .withContext(context)
                .execute();
    }

    public Mono<Void> remove(String appKey, UUID id, Context context) {
        return Executor
                .mono(() -> resourceRemovable.remove(appKey, id))
                .withLogger(log)
                .withContext(context)
                .execute();
    }
}
