package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.models.User;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.utilities.Context;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class ResourceServiceSaveForceTest {

    void save_resourceNotExisting_NotFoundError(Boolean force) {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var updatedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .applicationKey(appKey)
                .ownerId(UUID.randomUUID()).build();
        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();

        var eMessage = UUID.randomUUID().toString();
        var mocker = ResourceServiceMocker.create();
        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKey, updatedResource.id()))
                        .thenReturn(Mono.error(EUtils.createNotFoundError(eMessage))))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(appKey, updatedResource.id());
                })
                .forWritable( w -> Mockito.when(w.save(appKeyCaptor.capture(), resourceCaptor.capture()))
                        .thenReturn(Mono.just(vvu.centrauthz.models.Void.create())))
                .withWritableVerifier( v -> {
                    if (Boolean.TRUE.equals(force)) {
                        Mockito.verify(v, Mockito.times(1)).save(anyString(), any(Resource.class));
                        assertEquals(appKey, appKeyCaptor.getAllValues().getLast());
                        assertEquals(updatedResource.id(), resourceCaptor.getValue().id());
                        assertEquals(user.id(), resourceCaptor.getValue().createdBy());
                        assertNull(resourceCaptor.getValue().updatedBy());
                    } else {
                        Mockito.verify(v, Mockito.never()).save(anyString(), any(Resource.class));
                    }

                })
                .build();

        // When & Then
        if (Boolean.TRUE.equals(force)) {
            StepVerifier.create(resourceService.save(appKey, updatedResource, force, context))
                    .expectNext(Void.create())
                    .verifyComplete();
        } else {
            StepVerifier.create(resourceService.save(appKey, updatedResource, force, context))
                    .expectErrorSatisfies(e -> {
                        Assertions.assertInstanceOf(NotFoundError.class, e);
                        var error = ((NotFoundError) e).getError();
                        assertEquals("NOT_FOUND", error.code());
                        assertEquals(eMessage, error.message());
                        assertNull(error.details());
                    }).verify();
        }


        mocker.verify();
    }

    @Test
    void save_resourceNotExistingForceFalse_NotFoundError() {
        save_resourceNotExisting_NotFoundError(false);
    }

    @Test
    void save_resourceNotExistingForceNull_NotFoundError() {
        save_resourceNotExisting_NotFoundError(null);
    }

    @Test
    void save_resourceNotExistingForceTrue_SaveOnce() {
        save_resourceNotExisting_NotFoundError(true);
    }

    @Test
    void get_whenHasResource_shouldReturnResource() {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var updatedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .applicationKey(appKey)
                .ownerId(UUID.randomUUID()).build();
        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();

        var expectedResource = Resource.builder().id(updatedResource.id()).createdBy(updatedResource.createdBy()).build();
        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var idCaptor = ArgumentCaptor.forClass(UUID.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        var mocker = ResourceServiceMocker.create();
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKeyCaptor.capture(), idCaptor.capture())).thenReturn(Mono.just(expectedResource)))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.times(1)).get(anyString(), any(UUID.class));
                    assertEquals(appKey, appKeyCaptor.getAllValues().getFirst());
                    assertEquals(updatedResource.id(), idCaptor.getAllValues().getFirst());
                })
                .forWritable( w ->
                        Mockito.when(
                                        w.save(appKeyCaptor.capture(), resourceCaptor.capture()))
                                .thenReturn(Mono.just(vvu.centrauthz.models.Void.create())))
                .withWritableVerifier( v -> {
                    Mockito.verify(v, Mockito.times(1)).save(anyString(), any(Resource.class));
                    assertEquals(appKey, appKeyCaptor.getAllValues().getLast());
                    assertEquals(updatedResource.id(), resourceCaptor.getValue().id());
                    assertEquals(user.id(), resourceCaptor.getValue().updatedBy());
                })
                .build();

        // When & Then
        StepVerifier
                .create(resourceService.save(appKey, updatedResource, false,context))
                .expectNext(Void.create())
                .verifyComplete();
        mocker.verify();
    }
}
