package vvu.centrauthz.domains.resources.storages;

import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.repositories.ResourceReadable;
import vvu.centrauthz.domains.resources.repositories.ResourceRemovable;
import vvu.centrauthz.domains.resources.repositories.ResourceWritable;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.storages.interfaces.Readable;
import vvu.centrauthz.storages.interfaces.Removable;
import vvu.centrauthz.storages.interfaces.Writable;
import vvu.centrauthz.utilities.JsonTools;
import java.util.UUID;

@Singleton
public class ResourceStorage implements ResourceReadable, ResourceRemovable, ResourceWritable {

    private final Readable<JsonNode> readable;
    private final Writable<JsonNode> writable;
    private final Removable removable;
    private final JsonMapper jsonMapper;

    static String buildKey(String appKey, UUID id) {
        return String.format("%s:%s", appKey, id);

    }

    public ResourceStorage(JsonMapper jsonMapper,
                           Readable<JsonNode> readable,
                           Writable<JsonNode> writable,
                           Removable removable) {
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
        return removable.remove(buildKey(appKey, id));
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
        return writable.save(buildKey(appKey, resource.id()), data);
    }

    @Override
    public Mono<Resource> get(String appKey, UUID id) {
        return readable
            .get(buildKey(appKey, id))
            .map( node -> JsonTools.toValue(jsonMapper,node, Resource.class));
    }
}
