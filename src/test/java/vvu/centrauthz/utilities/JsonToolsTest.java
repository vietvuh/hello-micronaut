package vvu.centrauthz.utilities;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.IllegalJsonValue;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class JsonToolsTest {

    @Inject
    JsonMapper jsonMapper;

    Logger logger = LoggerFactory.getLogger(JsonToolsTest.class);

    @Serdeable
    @Introspected
    record TestRes(String name, Map<String, UUID> map) {
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testConstructor() {
        assertThrows(IllegalStateException.class, JsonTools::new);
    }

    @Test
    void jsonContext_whenIOExceptionISThrown_IllegalJsonValue() throws IOException {
        SupplierWithThrowable<JsonNode, IOException> supplier = Mockito.mock(SupplierWithThrowable.class);
        Mockito.when(supplier.get()).thenThrow(new IOException());
        assertThrows(IllegalJsonValue.class, () -> JsonTools.jsonContext(supplier));
    }

    @Test
    void jsonContext_whenIOExceptionIsNotThrown_Value() throws IOException {
        String value = UUID.randomUUID().toString();
        SupplierWithThrowable<String, IOException> supplier = Mockito.mock(SupplierWithThrowable.class);
        Mockito.when(supplier.get()).thenReturn(value);
        assertDoesNotThrow(() -> {
            assertEquals(value, JsonTools.jsonContext(supplier));
        });
    }

    @Test
    void toJson() throws IOException {

        TestRes res = new TestRes(
            UUID.randomUUID().toString().split("-")[0],
            Map.of("key1", UUID.randomUUID(), "key2", UUID.randomUUID()));
        var sub = JsonTools.toJson(jsonMapper, res);

        Resource resource = Resource.builder()
            .id(UUID.randomUUID())
            .type("RESOURCE")
            .applicationKey(UUID.randomUUID().toString().split("-")[0])
            .details(sub)
            .ownerId(UUID.randomUUID())
            .createdAt(System.currentTimeMillis())
            .createdBy(UUID.randomUUID())
            .build();

        var json = JsonTools.toJson(jsonMapper, resource);

        logger.info("{}", JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(json)));

        var detail1 = JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(sub));
        var detail2 = JsonTools.jsonContext(() -> jsonMapper.writeValueAsString(json.get("details")));


        assertEquals(json.get("id").getStringValue(), resource.id().toString());
        assertEquals(json.get("type").getStringValue(), resource.type());
        assertEquals(json.get("applicationKey").getStringValue(), resource.applicationKey());
        assertEquals(json.get("ownerId").getStringValue(), resource.ownerId().toString());
        assertEquals(json.get("createdAt").getLongValue(), resource.createdAt());
        assertEquals(json.get("createdBy").getStringValue(), resource.createdBy().toString());
        assertEquals(detail1, detail2);


    }

    @Test
    void toValue() {
        Resource resource = Resource.builder()
            .id(UUID.randomUUID())
            .type("RESOURCE")
            .applicationKey(UUID.randomUUID().toString().split("-")[0])
            .ownerId(UUID.randomUUID())
            .createdAt(System.currentTimeMillis())
            .createdBy(UUID.randomUUID())
            .build();

        var json = JsonTools.toJson(jsonMapper, resource);
        var outRes = JsonTools.toValue(jsonMapper, json, Resource.class);

        assertEquals(outRes.id(), resource.id());
        assertEquals(outRes.type(), resource.type());
        assertEquals(outRes.applicationKey(), resource.applicationKey());
        assertEquals(outRes.ownerId(), resource.ownerId());
        assertEquals(outRes.createdAt(), resource.createdAt());
        assertEquals(outRes.createdBy(), resource.createdBy());

    }

    @Test
    void toBytes() {
        TestRes res = new TestRes(
                UUID.randomUUID().toString().split("-")[0],
                Map.of("key1", UUID.randomUUID(), "key2", UUID.randomUUID()));
        var sub = JsonTools.toJson(jsonMapper, res);

        Resource resource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .applicationKey(UUID.randomUUID().toString().split("-")[0])
                .details(sub)
                .ownerId(UUID.randomUUID())
                .createdAt(System.currentTimeMillis())
                .createdBy(UUID.randomUUID())
                .build();

        var json = JsonTools.toJson(jsonMapper, resource);

        var bytes = JsonTools.toBytes(jsonMapper, json);
        var fromBytes = JsonTools.fromBytes(jsonMapper, bytes);

        var sSrcValue = JsonTools.toString(jsonMapper, json);
        var sFromBytesValue = JsonTools.toString(jsonMapper, fromBytes);

        assertEquals(sSrcValue, sFromBytesValue);

    }
}