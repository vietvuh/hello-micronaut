package vvu.centrauthz.storages.keyvalue.redis;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.micronaut.context.annotation.Primary;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.storages.interfaces.Readable;
import vvu.centrauthz.storages.interfaces.Removable;
import vvu.centrauthz.storages.interfaces.Writable;
import vvu.centrauthz.storages.keyvalue.redis.exceptions.RedisError;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.concurrent.CompletableFuture;

@Singleton
@Primary
@Named("RedisStorage")
@Slf4j
public class RedisStorage implements Readable<JsonNode>, Writable<JsonNode>, Removable {

    private final StatefulRedisConnection<String, JsonNode> connection;

    public RedisStorage(StatefulRedisConnection<String, JsonNode> connection) {
        this.connection = connection;
    }

    private RedisAsyncCommands<String, JsonNode> async() {
        return connection.async();
    }

    private <T> CompletableFuture<T> toFuture(RedisFuture<T> redisFuture, BiConsumer<CompletableFuture<T>, T> consumer) {
        CompletableFuture<T> future = new CompletableFuture<>();
        redisFuture.whenComplete( (value, error) -> {
            if (error != null) {
                log.error(error.getMessage());
                future.completeExceptionally(new RedisError(error));
            } else {
                Optional.ofNullable(consumer).ifPresentOrElse(
                    c -> c.accept(future, value),
                    () -> future.complete(value));
            }

        });

        return future;
    }

    private <T> CompletableFuture<T> toFuture(RedisFuture<T> redisFuture) {
        return toFuture(redisFuture, null);
    }

    private CompletableFuture<JsonNode> getFuture(String key) {
        return toFuture(async().get(key), (future, value) -> {
            if (value == null) {
                future.completeExceptionally(EUtils.createNotFoundError(key));
            } else {
                future.complete(value);
            }
        });
    }

    private CompletableFuture<Void> saveFuture(String key, JsonNode object) {
        return toFuture(async().set(key, object)).thenApply( v -> Void.INSTANCE);
    }

    private CompletableFuture<Void> removeFuture(String key) {
        return toFuture(async().del(key)).thenApply( v -> Void.INSTANCE);
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
