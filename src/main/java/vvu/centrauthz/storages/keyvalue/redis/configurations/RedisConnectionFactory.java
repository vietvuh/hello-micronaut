package vvu.centrauthz.storages.keyvalue.redis.configurations;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.support.AsyncConnectionPoolSupport;
import io.lettuce.core.support.BoundedAsyncPool;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

@Factory
@Slf4j
public class RedisConnectionFactory {

    private final String redisUri;
    private final RedisPoolConfig config;

    public RedisConnectionFactory(@Value("${redis.uri}") String redisUri, RedisPoolConfig config) {
        this.redisUri = redisUri;
        this.config = config;
    }


    @Singleton
    @Replaces(RedisCodec.class)
    public RedisCodec<byte[], byte[]> redisCodec() {
        return ByteArrayCodec.INSTANCE;
    }


    @Singleton
    public CompletionStage<BoundedAsyncPool<StatefulRedisConnection<byte[], byte[]>>> poolFuture(
        RedisCodec<byte[], byte[]> codec, RedisClient client) {
        return AsyncConnectionPoolSupport.createBoundedObjectPoolAsync(
            () -> client.connectAsync(codec, RedisURI.create(redisUri)), config.toBoundedPoolConfig());
    }

}
