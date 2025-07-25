package vvu.centrauthz.storages.keyvalue.redis.utilities;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.support.BoundedAsyncPool;
import lombok.Builder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncConnContext {

    private final CompletionStage<BoundedAsyncPool<StatefulRedisConnection<byte[], byte[]>>> poolFuture;
    private Consumer<Long> consumer;
    public AsyncConnContext(CompletionStage<BoundedAsyncPool<StatefulRedisConnection<byte[], byte[]>>> poolFuture) {
        this.poolFuture = poolFuture;
    }

    private static void release(Context context, Long start, Consumer<Long> consumer) {
        context.pool.release(context.connection)
                .whenComplete((v, e) -> {
                    var end = System.nanoTime() - start;
                    Optional
                            .ofNullable(consumer)
                            .ifPresent(c -> c.accept(end));
                });
    }

    public AsyncConnContext onComplete(Consumer<Long> consumer) {
        this.consumer = consumer;
        return this;
    }

    private CompletableFuture<Context> acquire() {
        var builder = Context.builder();
        return poolFuture.thenCompose(pool ->
                        pool.acquire().thenApply(
                                conn ->
                                        builder.connection(conn).pool(pool).build()))
                .toCompletableFuture();
    }

    public <R> CompletableFuture<R> execute(Function<RedisAsyncCommands<byte[], byte[]>, CompletableFuture<R>> action) {
        var start = System.currentTimeMillis();
        return acquire()
                .thenCompose(context ->
                        action
                                .apply(context.connection.async())
                                .whenComplete((res, err) -> release(context, start, consumer)))
                .toCompletableFuture();
    }

    @Builder(toBuilder = true)
    private record Context(
            BoundedAsyncPool<StatefulRedisConnection<byte[], byte[]>> pool,
            StatefulRedisConnection<byte[], byte[]> connection
    ) {
    }

}
