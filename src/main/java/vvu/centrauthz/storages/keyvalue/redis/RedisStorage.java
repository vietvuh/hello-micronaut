package vvu.centrauthz.storages.keyvalue.redis;

import io.micronaut.context.annotation.Primary;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import vvu.centrauthz.exceptions.NotImplementedError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.storages.interfaces.Readable;
import vvu.centrauthz.storages.interfaces.Removable;
import vvu.centrauthz.storages.interfaces.Writable;

@Singleton
@Primary
@Named("RedisStorage")
public class RedisStorage implements Readable<JsonNode>, Writable<JsonNode>, Removable {

    @Override
    public Mono<JsonNode> get(String key) {
        // TODO:
        return Mono.error(new NotImplementedError("RedisStorage.get is not implemented"));
    }

    @Override
    public Mono<Void> remove(String appKey) {
        // TODO:
        return Mono.error(new NotImplementedError("RedisStorage.remove is not implemented"));
    }

    @Override
    public Mono<Void> save(String key, JsonNode object) {
        // TODO:
        return Mono.error(new NotImplementedError("RedisStorage.save is not implemented"));
    }
}
