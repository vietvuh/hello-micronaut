package vvu.centrauthz.configuration.events;

import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import lombok.extern.slf4j.Slf4j;

@Factory
@Slf4j
public class AppConfiguration {

    @EventListener
    public void onStartup(ServerStartupEvent event) {
        log.info("Redis URI: " + System.getenv("REDIS_URI"));
    }

}
