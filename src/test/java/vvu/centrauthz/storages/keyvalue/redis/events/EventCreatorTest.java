package vvu.centrauthz.storages.keyvalue.redis.events;

import io.micronaut.json.JsonMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.models.ChangedEvent;
import vvu.centrauthz.utilities.JsonTools;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class EventCreatorTest {

    @Inject
    private JsonMapper jsonMapper;

    @Test
    void createEvent() {
        var resource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .applicationKey(UUID.randomUUID().toString().split("-")[0])
                .ownerId(UUID.randomUUID())
                .createdAt(System.currentTimeMillis())
                .createdBy(UUID.randomUUID())
                .build();
        var resourceJson = JsonTools.toJson(jsonMapper, resource);
        var event = EventCreator.createEvent(ChangedEvent.EventType.CHANGED, resource.id().toString(), resourceJson);

        assertEquals(ChangedEvent.EventType.CHANGED, event.type());
        assertEquals(resource.id().toString(), event.path());
        assertNotNull(event.timestamp());
        assertSame(event.value(), resourceJson);
    }

    @Test
    void isUpdated() {
        var resource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .applicationKey(UUID.randomUUID().toString().split("-")[0])
                .ownerId(UUID.randomUUID())
                .createdAt(System.currentTimeMillis())
                .createdBy(UUID.randomUUID())
                .build();
        var resourceJson = JsonTools.toJson(jsonMapper, resource);
        var event = EventCreator.createEvent(resource.id().toString(), resourceJson);

        assertEquals(ChangedEvent.EventType.CREATED, event.type());
        assertEquals(resource.id().toString(), event.path());
        assertNotNull(event.timestamp());
        assertSame(event.value(), resourceJson);

        resource = resource.toBuilder()
                .updatedBy(UUID.randomUUID())
                .updatedAt(System.currentTimeMillis()).build();
        resourceJson = JsonTools.toJson(jsonMapper, resource);
        var event2 = EventCreator.createEvent(resource.id().toString(), resourceJson);
        assertEquals(ChangedEvent.EventType.UPDATED, event2.type());
        assertEquals(resource.id().toString(), event2.path());
        assertTrue(event2.timestamp() >= event.timestamp());
        assertSame(event2.value(), resourceJson);

    }

    @Test
    void composeEvent() {
    }

    @Test
    void composeDeletedEvent() {
    }
}