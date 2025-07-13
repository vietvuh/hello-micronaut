package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.User;
import vvu.centrauthz.domains.resources.repositories.Readable;
import vvu.centrauthz.domains.resources.repositories.Removable;
import vvu.centrauthz.domains.resources.repositories.Writable;
import vvu.centrauthz.exceptions.ConflictError;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.utilities.Context;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

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

    @Test
    void save() {

    }

    @Test
    void patch() {
    }

    @Test
    void remove() {
    }
}