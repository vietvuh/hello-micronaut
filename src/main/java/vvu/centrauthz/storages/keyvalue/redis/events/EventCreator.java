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

    /**
     * Create a {@link ChangedEvent} for the given {@link ChangedEvent.EventType} and key, with the given JSON node
     * as the value.
     *
     * @param type the type of the event
     * @param key the key identifying the resource
     * @param node the JSON node representing the resource
     * @return a new {@link ChangedEvent} with the given type, key, value, and current timestamp
     */
    public static ChangedEvent<JsonNode> createEvent(ChangedEvent.EventType type, String key, JsonNode node) {
        return ChangedEvent
                .<JsonNode>builder()
                .type(type)
                .path(key)
                .value(node)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Determines if the given {@link JsonNode} represents an update event.
     *
     * <p>An update event is defined as a {@link JsonNode} that contains a non-null
     * "updatedBy" property.
     *
     * @param node the JSON node to inspect
     * @return true if the given JSON node represents an update event, false otherwise
     */
    static boolean isUpdated(JsonNode node) {
        var updatedBy = node.get("updatedBy");
        return Objects.nonNull(updatedBy);
    }

    /**
     * Create a {@link ChangedEvent} for the given key and JSON node.
     *
     * <p>If the given JSON node is null, or if the JSON node is an update event (i.e. it contains a non-null "updatedBy"
     * property), then the type of the event is inferred from the JSON node.
     *
     * @param key the key identifying the resource
     * @param node the JSON node representing the resource
     * @return a new {@link ChangedEvent} with the inferred type, the given key, the given value, and the current timestamp
     */
    public static ChangedEvent<JsonNode> createEvent(String key, JsonNode node) {

        if (Objects.isNull(node) || node.isNull()) {
            return createEvent(ChangedEvent.EventType.CHANGED, key, node);
        }

        var type = isUpdated(node) ? ChangedEvent.EventType.UPDATED : ChangedEvent.EventType.CREATED;
        return createEvent(type, key, node);
    }

    /**
     * Creates a {@link ChangedEvent} of type {@link ChangedEvent.EventType#REMOVED} for the given key and null value.
     *
     * @param key the key identifying the resource to be deleted
     * @return a new {@link ChangedEvent} with the given key, null value, and the current timestamp
     */
    public static ChangedEvent<JsonNode> composeDeletedEvent(String key) {
        return createEvent(ChangedEvent.EventType.REMOVED, key, null);
    }

    /**
     * Publishes the given event to the event bus, but only if events are enabled.
     *
     * @param event the event to publish
     */
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
