package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.User;
import vvu.centrauthz.exceptions.ConflictError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.utilities.Context;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("ResourceService Create method tests")
class ResourceServiceCreateTest {

    @Test
    void create_whenNoResource_newOneCreated() {
        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var resource = Resource.builder()
                .applicationKey(appKey)
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .ownerId(UUID.randomUUID())
                .build();
        ArgumentCaptor<String> appKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        var mocker = ResourceServiceMocker.create();
        var resourceService =
                mocker
                        .forReadable(r -> Mockito.when(r.get(appKeyCaptor.capture(), uuidCaptor.capture())).thenReturn(Mono.empty()))
                        .withReadableVerifier(r -> {
                            Mockito.verify(r, Mockito.only()).get(anyString(), any(UUID.class));
                            var capturedAppKey = appKeyCaptor.getAllValues().getFirst();
                            var capturedId = uuidCaptor.getAllValues().getFirst();
                            assertEquals(capturedAppKey, appKey);
                            assertEquals(capturedId, resource.id());
                        }).forWritable( w -> Mockito.when(w.save(appKeyCaptor.capture(), resourceCaptor.capture())).thenReturn(Mono.just(vvu.centrauthz.models.Void.create())))
                        .withWritableVerifier( v -> {
                            Mockito.verify(v, Mockito.only()).save(anyString(), any(Resource.class));
                            var capturedAppKey = appKeyCaptor.getAllValues().getLast();
                            var capturedResource = resourceCaptor.getValue();
                            assertEquals(capturedAppKey, appKey);
                            assertEquals(capturedResource.id(), resource.id());
                            assertEquals(capturedResource.createdBy(), user.id());
                        }).build();

        // When & Then
        StepVerifier.create(resourceService.create(appKey, resource, context))
                .assertNext( res -> {
                    assertEquals(resource.id(), res.id());
                    assertEquals(resource.applicationKey(), res.applicationKey());
                    assertNull(res.updatedAt());
                    assertNull(res.updatedBy());
                    assertNotNull(res.createdAt());
                })
                .verifyComplete();
        mocker.verify();
    }

    @Test
    void create_whenResourceNoId_newOneCreated() {
        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var resource = Resource.builder()
                .applicationKey(appKey)
                .type("RESOURCE")
                .ownerId(UUID.randomUUID())
                .build();
        ArgumentCaptor<String> appKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        var mocker = ResourceServiceMocker.create();
        var resourceService =
                mocker
                        .forReadable(r -> Mockito.when(r.get(appKeyCaptor.capture(), uuidCaptor.capture())).thenReturn(Mono.empty()))
                        .withReadableVerifier(r -> {
                            Mockito.verify(r, Mockito.times(0)).get(anyString(), any(UUID.class));
                        }).forWritable( w -> Mockito.when(w.save(appKeyCaptor.capture(), resourceCaptor.capture())).thenReturn(Mono.just(vvu.centrauthz.models.Void.create())))
                        .withWritableVerifier( v -> {
                            Mockito.verify(v, Mockito.only()).save(anyString(), any(Resource.class));
                            var capturedAppKey = appKeyCaptor.getAllValues().getFirst();
                            var capturedResource = resourceCaptor.getValue();
                            assertEquals(capturedAppKey, appKey);
                            assertNotNull(capturedResource.id());
                            assertEquals(capturedResource.createdBy(), user.id());
                        }).build();

        // When & Then
        StepVerifier.create(resourceService.create(appKey, resource, context))
                .assertNext( res -> {
                    assertEquals(resource.applicationKey(), res.applicationKey());
                    assertNull(res.updatedAt());
                    assertNull(res.updatedBy());
                    assertNotNull(res.createdAt());
                })
                .verifyComplete();
        mocker.verify();

    }

    @Test
    void create_whenResourceExisting_noCreating() {
        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var resource = Resource.builder()
                .applicationKey(appKey)
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .ownerId(UUID.randomUUID())
                .build();
        ArgumentCaptor<String> appKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        var mocker = ResourceServiceMocker.create();
        var resourceService =
                mocker
                        .forReadable(r -> Mockito.when(r.get(appKeyCaptor.capture(), uuidCaptor.capture())).thenReturn(Mono.just(resource)))
                        .withReadableVerifier(r -> {
                            Mockito.verify(r, Mockito.only()).get(anyString(), any(UUID.class));
                            var capturedAppKey = appKeyCaptor.getAllValues().getFirst();
                            var capturedId = uuidCaptor.getAllValues().getFirst();
                            assertEquals(capturedAppKey, appKey);
                            assertNotNull(capturedId);
                        }).forWritable( w -> Mockito.when(w.save(appKeyCaptor.capture(), resourceCaptor.capture())).thenReturn(Mono.just(Void.create())))
                        .withWritableVerifier( v -> {
                            Mockito.verify(v, Mockito.times(0)).save(anyString(), any(Resource.class));
                        }).build();

        // When & Then
        StepVerifier.create(resourceService.create(appKey, resource, context))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(ConflictError.class, e);
                    var error = ((ConflictError) e).getError();
                    assertEquals("CONFLICT", error.code());
                    assertNull(error.details());
                })
                .verify();
        mocker.verify();
    }
}
