package vvu.centrauthz.storages.keyvalue.redis.configurations;

import io.lettuce.core.support.BoundedPoolConfig;
import io.micronaut.context.annotation.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("redis.pool")
public record RedisPoolConfig(boolean enabled,
                              int maxActive,
                              int maxIdle,
                              int minIdle,
                              Duration maxWait) {
    public RedisPoolConfig() {
        this(true, 10, 5, 3, Duration.ofMillis(2000));
    }

    public BoundedPoolConfig toBoundedPoolConfig() {
        return BoundedPoolConfig.builder()
            .maxTotal(maxActive)
            .maxIdle(maxIdle)
            .minIdle(minIdle)
            .build();
    }
}
