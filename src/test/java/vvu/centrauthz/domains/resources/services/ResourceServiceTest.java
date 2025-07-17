package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ResourceServiceTest {

    @Test
    void testResourceNotFound() {
        String appKey = UUID.randomUUID().toString().split("-")[0];
        UUID id = UUID.randomUUID();
        String eMess = String.format("Resource with ID %s not found for application %s", id, appKey);

        var error =ResourceService.resourceNotFound(appKey, id);

        assertEquals(eMess, error.getMessage());
        var e = error.getError();
        assertEquals("NOT_FOUND", e.code());
        assertEquals(eMess, e.message());
        assertNull(e.details());
    }
}