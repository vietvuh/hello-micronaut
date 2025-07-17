package vvu.centrauthz.domains.resources.storages;

import io.micronaut.context.annotation.Value;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.repositories.ResourceReadable;
import vvu.centrauthz.domains.resources.repositories.ResourceRemovable;
import vvu.centrauthz.domains.resources.repositories.ResourceWritable;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.storages.interfaces.Readable;
import vvu.centrauthz.storages.interfaces.Removable;
import vvu.centrauthz.storages.interfaces.Writable;
import vvu.centrauthz.utilities.JsonTools;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Singleton
public class ResourceStorage implements ResourceReadable, ResourceRemovable, ResourceWritable {

    private final String prefix;
    private final Readable<JsonNode> readable;
    private final Writable<JsonNode> writable;
    private final Removable removable;
    private final JsonMapper jsonMapper;

    static String buildKey(String prefix, String appKey, UUID id) {
        if (Objects.isNull(prefix) || prefix.isBlank()) {
            prefix = "RES";
        }

        return String.format("%s:%s:%s", prefix, appKey, id);

    }

    static NotFoundError createNotFoundError(String prefix, String appKey, UUID id) {
        return EUtils.createNotFoundError(buildKey(prefix, appKey, id));
    }

    public ResourceStorage(@Value("${app.storage.namespace:RES}") String prefix,
                           JsonMapper jsonMapper,
                           Readable<JsonNode> readable,
                           Writable<JsonNode> writable,
                           Removable removable) {

        this.prefix = prefix;
        this.readable = readable;
        this.writable = writable;
        this.removable = removable;
        this.jsonMapper = jsonMapper;
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
        return removable.remove(buildKey(prefix, appKey, id));
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
        var data = JsonTools.toJson(jsonMapper, resource);
        return writable.save(buildKey(prefix, appKey, resource.id()), data);
    }

    @Override
    public Mono<Resource> get(String appKey, UUID id) {
        return readable
            .get(buildKey(prefix, appKey, id))
            .map( node -> JsonTools.toValue(jsonMapper,node, Resource.class));
    }
}
