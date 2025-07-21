package vvu.centrauthz.storages.keyvalue.redis.configurations;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.BoundedAsyncPool;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import vvu.centrauthz.storages.keyvalue.redis.utilities.AsyncConnContext;

import java.util.concurrent.CompletionStage;

@Slf4j
@Singleton
public class RedisConnectionInitializer implements ApplicationEventListener<StartupEvent> {

    private final RedisPoolConfig config;
    private final AsyncConnContext context;
    private static final Long NANOS = 1000000L;


    public RedisConnectionInitializer(RedisPoolConfig config, CompletionStage<BoundedAsyncPool<StatefulRedisConnection<byte[], byte[]>>> poolFuture) {
        this.config = config;
        context = new AsyncConnContext(poolFuture)
                .onComplete( t -> log.info("Redis connection initialized in {} millis", t /NANOS) );
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(StartupEvent event) {
        for (int i = 0; i < config.minIdle(); i++) {
            context.execute( command -> command.ping().toCompletableFuture());
        }
    }

    /**
     * Whether the given event is supported.
     *
     * @param event The event
     * @return True if it is
     */
    @Override
    public boolean supports(StartupEvent event) {
        return ApplicationEventListener.super.supports(event);
    }
}
