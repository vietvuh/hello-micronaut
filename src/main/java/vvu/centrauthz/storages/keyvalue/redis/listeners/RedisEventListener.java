package vvu.centrauthz.storages.keyvalue.redis.listeners;

import io.micronaut.context.annotation.Factory;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.runtime.event.annotation.EventListener;
import lombok.extern.slf4j.Slf4j;
import vvu.centrauthz.models.ChangedEvent;
import vvu.centrauthz.utilities.JsonTools;

import java.util.Objects;

@Factory
@Slf4j
public class RedisEventListener {

    private final JsonMapper mapper;

    public RedisEventListener(JsonMapper mapper) {
        this.mapper = mapper;
    }

    @EventListener
    public void handleEvent(ChangedEvent<JsonNode> event) {
        var json = JsonTools.toJson(mapper, event);
        log.info(JsonTools.toString(mapper, json));
    }
}
