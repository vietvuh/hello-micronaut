package vvu.centrauthz.storages.keyvalue.redis;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.BoundedAsyncPool;
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
import vvu.centrauthz.storages.keyvalue.redis.utilities.AsyncConnContext;
import vvu.centrauthz.utilities.JsonTools;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
@Named("RedisByteArrayStorage")
@Primary
@Slf4j
public class RedisByteArrayStorage implements Readable<JsonNode>, Writable<JsonNode>, Removable {

    private final JsonMapper mapper;
    private final AsyncConnContext context;

    public RedisByteArrayStorage(
            JsonMapper mapper,
            CompletionStage<BoundedAsyncPool<StatefulRedisConnection<byte[], byte[]>>> poolFuture) {
        this.mapper = mapper;
        this.context = new AsyncConnContext(poolFuture);
    }

    private CompletableFuture<JsonNode> getFuture(String key) {
        return context.execute(command ->
            command.get(key.getBytes(StandardCharsets.UTF_8))
            .toCompletableFuture()
            .exceptionallyCompose( e -> CompletableFuture.failedFuture(new RedisError(e)))
            .thenApply( v -> {
                if (Objects.isNull(v)) {
                    throw EUtils.createNotFoundError(key);
                }
                return JsonTools.fromBytes(mapper, v);
            }));
    }

    private CompletableFuture<Void> saveFuture(String key, JsonNode node) {
        try {
            var value = JsonTools.toBytes(mapper, node);
            return context.execute(command ->
                command.set(key.getBytes(StandardCharsets.UTF_8), value)
                .toCompletableFuture()
                .exceptionallyCompose( e -> CompletableFuture.failedFuture(new RedisError(e)))
                .thenApply( v -> Void.INSTANCE));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private CompletableFuture<Void> removeFuture(String key) {
        try {
            return context.execute(command ->
                command.del(key.getBytes(StandardCharsets.UTF_8))
                    .toCompletableFuture()
                    .exceptionallyCompose( e -> CompletableFuture.failedFuture(new RedisError(e)))
                    .thenApply( v -> Void.INSTANCE));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public Mono<JsonNode> get(String key) {
        return Mono.fromFuture(getFuture(key));
    }

    @Override
    public Mono<Void> remove(String appKey) {
        return Mono.fromFuture(removeFuture(appKey));
    }

    @Override
    public Mono<Void> save(String key, JsonNode object) {
        return Mono.fromFuture(saveFuture(key, object));
    }
}
