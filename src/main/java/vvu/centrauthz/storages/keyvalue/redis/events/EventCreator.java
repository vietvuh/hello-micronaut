package vvu.centrauthz.storages.keyvalue.redis.events;

import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.json.tree.JsonNode;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import vvu.centrauthz.models.ChangedEvent;

import java.util.Objects;

@Singleton
@Slf4j
public class EventCreator {

    private final ApplicationEventPublisher<ChangedEvent<JsonNode>> eventPublisher;
    private final Boolean enabled;

    public EventCreator(ApplicationEventPublisher<ChangedEvent<JsonNode>> eventPublisher,
                        @Value("${redis.cdc.enabled}") Boolean enabled) {
        this.eventPublisher = eventPublisher;
        this.enabled = enabled;
    }

    public static ChangedEvent<JsonNode> createEvent(ChangedEvent.EventType type, String key, JsonNode node) {
        return ChangedEvent
                .<JsonNode>builder()
                .type(type)
                .path(key)
                .value(node)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    static boolean isUpdated(JsonNode node) {
        var updatedBy = node.get("updatedBy");
        return Objects.nonNull(updatedBy);
    }

    public static ChangedEvent<JsonNode> createEvent(String key, JsonNode node) {

        if (Objects.isNull(node) || node.isNull()) {
            return createEvent(ChangedEvent.EventType.CHANGED, key, node);
        }

        var type = isUpdated(node) ? ChangedEvent.EventType.UPDATED : ChangedEvent.EventType.CREATED;
        return createEvent(type, key, node);
    }

    public static ChangedEvent<JsonNode> composeDeletedEvent(String key) {
        return createEvent(ChangedEvent.EventType.REMOVED, key, null);
    }

    public void raiseEvent(ChangedEvent<JsonNode> event) {
        try {
            if (!Boolean.TRUE.equals(enabled)) {
                return;
            }
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish event", e);
        }
    }
}
