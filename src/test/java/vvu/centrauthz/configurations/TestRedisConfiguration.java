package vvu.centrauthz.configurations;

import io.micronaut.context.annotation.*;
import io.micronaut.context.env.Environment;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.inject.Singleton;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Factory
@Requires(env = Environment.TEST)
public class TestRedisConfiguration {

    @Bean
    @Primary
    @Singleton
    public RedisClient redisClient() {
        return Mockito.mock(RedisClient.class); // Return null to disable
    }

    @Bean
    @Primary
    @Singleton
    public StatefulRedisConnection<byte[], byte[]> redisConnection() {
        return Mockito.mock(StatefulRedisConnection.class);
    }

    @Bean
    @Primary
    @Singleton
    public CompletionStage<?> completionStage() {
        return CompletableFuture.completedFuture(null);
    }
}
