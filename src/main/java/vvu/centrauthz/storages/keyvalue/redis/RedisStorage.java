package vvu.centrauthz.storages.keyvalue.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.micronaut.context.annotation.Primary;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.storages.interfaces.Readable;
import vvu.centrauthz.storages.interfaces.Removable;
import vvu.centrauthz.storages.interfaces.Writable;
import vvu.centrauthz.storages.keyvalue.redis.exceptions.RedisError;

import java.util.concurrent.CompletableFuture;

@Singleton
@Primary
@Named("RedisStorage")
public class RedisStorage implements Readable<JsonNode>, Writable<JsonNode>, Removable {

    private final RedisAsyncCommands<String, JsonNode> asyncCommands;

    public RedisStorage(StatefulRedisConnection<String, JsonNode> connection) {
        this.asyncCommands = connection.async();
    }

    private CompletableFuture<JsonNode> getFuture(String key) {
        CompletableFuture<JsonNode> future = new CompletableFuture<>();
        asyncCommands.get(key).whenComplete((value, error) -> {
            if (error != null) {
                future.completeExceptionally(new RedisError(error));
            } else if (value == null) {
                future.completeExceptionally(EUtils.createNotFoundError(key));
            } else {
                future.complete(value);
            }
        });

        return future;
    }

    private CompletableFuture<Void> saveFuture(String key, JsonNode object) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        asyncCommands.set(key, object).whenComplete((value, error) -> {
            if (error != null) {
                future.completeExceptionally(new RedisError(error));
            } else {
                future.complete(Void.create());
            }
        });

        return future;
    }

    private CompletableFuture<Void> removeFuture(String key) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        asyncCommands.del(key).whenComplete((value, error) -> {
            if (error != null) {
                future.completeExceptionally(new RedisError(error));
            } else {
                future.complete(Void.create());
            }
        });

        return future;
    }

    @Override
    public Mono<JsonNode> get(String key) {
        return Mono.fromFuture(getFuture(key));
    }

    @Override
    public Mono<Void> remove(String key) {
        return Mono.fromFuture(removeFuture(key));
    }

    @Override
    public Mono<Void> save(String key, JsonNode object) {
        return Mono.fromFuture(saveFuture(key, object));
    }
}
