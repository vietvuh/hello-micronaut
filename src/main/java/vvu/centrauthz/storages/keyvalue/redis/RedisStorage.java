package vvu.centrauthz.storages.keyvalue.redis;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.ValueOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandType;
import io.micronaut.context.annotation.Primary;
import io.micronaut.json.JsonMapper;
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
import vvu.centrauthz.utilities.JsonTools;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.concurrent.CompletableFuture;

@Singleton
@Primary
@Named("RedisStorage")
@Slf4j
public class RedisStorage implements Readable<JsonNode>, Writable<JsonNode>, Removable {

    private final StatefulRedisConnection<String, String> connection;
    private final JsonMapper jsonMapper;

    public RedisStorage(StatefulRedisConnection<String, String> connection, JsonMapper jsonMapper) {
        this.connection = connection;
        this.jsonMapper = jsonMapper;
    }

    private RedisAsyncCommands<String, String> async() {
        return connection.async();
    }

    private <T> CompletableFuture<T> toFuture(RedisFuture<T> redisFuture, BiConsumer<CompletableFuture<T>, T> consumer) {
        CompletableFuture<T> future = new CompletableFuture<>();
        redisFuture.whenComplete( (value, error) -> {
            if (error != null) {
                log.error(error.getMessage());
                future.completeExceptionally(new RedisError(error));
            } else {
                Optional
                    .ofNullable(consumer)
                    .ifPresentOrElse(
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

        return toFuture(async().get(key), (future,  value) -> {
            if (value == null) {
                future.completeExceptionally(EUtils.createNotFoundError(key));
            } else {
                future.complete(value);
            }
        }).thenApply(value -> JsonTools.fromString(jsonMapper, value));
    }

    private CompletableFuture<Void> saveFuture(String key, JsonNode object) {
        try {
            var s = JsonTools.toString(jsonMapper, object);
            return toFuture(async().set(key, s)).thenApply( v -> Void.INSTANCE);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
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
