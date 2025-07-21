package vvu.centrauthz.storages.keyvalue.redis.utilities;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.support.BoundedAsyncPool;
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

    public AsyncConnContext onComplete(Consumer<Long> consumer) {
        this.consumer = consumer;
        return this;
    }

    public <R> CompletableFuture<R> execute(
            Function<RedisAsyncCommands<byte[],byte[]>,CompletableFuture<R>> action){
        var start = System.nanoTime();
        return poolFuture.thenCompose(pool ->
                        pool.acquire()
                                .thenCompose( conn ->
                                        action
                                                .apply(conn.async())
                                                .whenComplete((res, err) -> {
                                                    pool.release(conn);
                                                    var end =System.nanoTime() - start;
                                                    Optional
                                                            .ofNullable(consumer)
                                                            .ifPresent(c -> c.accept(end));
                                                })))
                .toCompletableFuture();
    }
}
