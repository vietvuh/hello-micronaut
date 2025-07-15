package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.ResourceForPatch;
import vvu.centrauthz.models.User;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.utilities.Context;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("ResourceService patch method tests")
public class ResourceServicePatchTest {
    @Test
    void patch_resourceNotExisting_NotFoundError() {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var eMessage = UUID.randomUUID().toString();
        var updatedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .ownerId(UUID.randomUUID())
                .applicationKey(appKey)
                .ownerId(UUID.randomUUID()).build();

        var tags = List.of("tag1", "tag2", "tag3");
        var patchedData = ResourceForPatch.ResourcePatchData
                .builder()
                .tags(tags)
                .ownerId(UUID.randomUUID())
                .build();

        var patcher = ResourceForPatch.builder()
                .updatedFields(List.of("ownerId", "tags"))
                .data(patchedData).build();
        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var idCaptor = ArgumentCaptor.forClass(UUID.class);

        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();
        var mocker = ResourceServiceMocker.create();
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKeyCaptor.capture(), idCaptor.capture())).thenReturn(Mono.error(EUtils.createNotFoundError(eMessage))))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(anyString(), any(UUID.class));
                })
                .forWritable( w -> Mockito.when(w.save(anyString(), any(Resource.class))).thenReturn(Mono.just(Void.create())))
                .withWritableVerifier( v -> {
                    Mockito.verify(v, Mockito.never()).save(anyString(), any(Resource.class));
                })
                .build();

        // When & Then
        StepVerifier.create(resourceService.patch(appKey, updatedResource.id(),patcher, context))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(NotFoundError.class, e);
                    var error = ((NotFoundError) e).getError();
                    assertEquals("NOT_FOUND", error.code());
                    assertEquals(eMessage, error.message());
                    assertNull(error.details());
                }).verify();
        mocker.verify();
    }

    @Test
    void patch_resourceExisting_PatchIt() {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var updatedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type("RESOURCE")
                .ownerId(UUID.randomUUID())
                .applicationKey(appKey)
                .ownerId(UUID.randomUUID()).build();

        var tags = List.of("tag1", "tag2", "tag3");
        var patchedData = ResourceForPatch.ResourcePatchData
                .builder()
                .tags(tags)
                .ownerId(UUID.randomUUID())
                .build();

        var patcher = ResourceForPatch.builder()
                .updatedFields(List.of("ownerId", "tags"))
                .data(patchedData).build();
        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var idCaptor = ArgumentCaptor.forClass(UUID.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        var user = User.builder().id(UUID.randomUUID()).build();
        var context = Context.builder().user(user).build();
        var mocker = ResourceServiceMocker.create();
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKeyCaptor.capture(), idCaptor.capture())).thenReturn(Mono.just(updatedResource)))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(anyString(), any(UUID.class));
                    assertEquals(appKeyCaptor.getAllValues().getFirst(), appKey);
                    assertEquals(idCaptor.getAllValues().getFirst(), updatedResource.id());
                })
                .forWritable( w -> Mockito.when(w.save(appKeyCaptor.capture(), resourceCaptor.capture())).thenReturn(Mono.just(Void.create())))
                .withWritableVerifier( v -> {
                    Mockito.verify(v, Mockito.times(1)).save(anyString(), any(Resource.class));
                    assertEquals(appKeyCaptor.getAllValues().getLast(), appKey);
                    assertEquals(resourceCaptor.getAllValues().getLast().id(), updatedResource.id());
                    assertEquals(resourceCaptor.getAllValues().getLast().ownerId(), patchedData.ownerId());
                    assertSame(resourceCaptor.getAllValues().getLast().tags(), patchedData.tags());
                })
                .build();

        // When & Then
        StepVerifier.create(resourceService.patch(appKey, updatedResource.id(),patcher, context))
                .expectNext(Void.create()).verifyComplete();
        mocker.verify();
    }
}
