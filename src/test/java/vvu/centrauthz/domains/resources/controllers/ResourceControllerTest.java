package vvu.centrauthz.domains.resources.controllers;

import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.ResourceForPatch;
import vvu.centrauthz.domains.resources.services.ResourceService;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.models.Void;
import vvu.centrauthz.utilities.Context;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class ResourceControllerTest {

    @Test
    void testGetResource() {
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceController controller = new ResourceController(service, validator);
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var expectedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type(UUID.randomUUID().toString().split("-")[0])
                .applicationKey(appKey)
                .build();

        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var idCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.when(service.get(appKeyCaptor.capture() ,idCaptor.capture())).thenReturn(Mono.just(expectedResource));

        StepVerifier.create(controller.getResource(appKey, expectedResource.id()))
                .assertNext( response -> {
                    assertEquals(HttpStatus.OK, response.status());
                    assertSame(expectedResource, response.body());
                })
                .verifyComplete();
        assertEquals(expectedResource.id(), idCaptor.getValue());
        assertEquals(appKey, appKeyCaptor.getValue());
        Mockito.verify(service, Mockito.times(1)).get(anyString(), any(UUID.class));
    }


    @Test
    void updateResource_whenNoId_success() {
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceController controller = new ResourceController(service, validator);
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var expectedResource = Resource.builder()
                .type(UUID.randomUUID().toString().split("-")[0])
                .applicationKey(appKey)
                .build();
        var id = UUID.randomUUID();

        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        Mockito.when(validator.validate(resourceCaptor.capture())).thenReturn(Mono.just(expectedResource));

        Mockito.when(service.save(appKeyCaptor.capture() ,resourceCaptor.capture(), contextCaptor.capture())).thenReturn(Mono.just(Void.create()));

        StepVerifier.create(controller.updateResource(appKey, id, expectedResource))
                .assertNext( response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.status());
                    assertNull(response.body());
                })
                .verifyComplete();
        assertNotNull(contextCaptor.getValue());
        assertEquals(appKey, appKeyCaptor.getValue());
        assertEquals(id, resourceCaptor.getAllValues().getLast().id());
        Mockito.verify(service, Mockito.times(1)).save(anyString(), any(Resource.class), any(Context.class));
        Mockito.verify(validator, Mockito.times(1)).validate(any(Resource.class));
    }

    @Test
    void updateResource_whenIdMatched_success() {
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceController controller = new ResourceController(service, validator);
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var expectedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type(UUID.randomUUID().toString().split("-")[0])
                .applicationKey(appKey)
                .build();

        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        Mockito.when(service.save(appKeyCaptor.capture() ,resourceCaptor.capture(), contextCaptor.capture())).thenReturn(Mono.just(Void.create()));
        Mockito.when(validator.validate(resourceCaptor.capture())).thenReturn(Mono.just(expectedResource));

        StepVerifier.create(controller.updateResource(appKey, expectedResource.id(), expectedResource))
                .assertNext( response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.status());
                    assertNull(response.body());
                })
                .verifyComplete();
        assertNotNull(contextCaptor.getValue());
        assertEquals(appKey, appKeyCaptor.getAllValues().getLast());
        Mockito.verify(service, Mockito.times(1)).save(anyString(), any(Resource.class), any(Context.class));
    }

    @Test
    void updateResource_whenIdNotMatched_BadRequest() {
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceController controller = new ResourceController(service, validator);
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var expectedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type(UUID.randomUUID().toString().split("-")[0])
                .applicationKey(appKey)
                .build();

        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        Mockito.when(service.save(appKeyCaptor.capture() ,resourceCaptor.capture(), contextCaptor.capture())).thenReturn(Mono.just(Void.create()));
        Mockito.when(validator.validate(resourceCaptor.capture())).thenReturn(Mono.just(expectedResource));

        StepVerifier.create(controller.updateResource(appKey, UUID.randomUUID(), expectedResource))
                .expectErrorSatisfies( e -> {
                    assertInstanceOf(BadRequestError.class, e);
                    assertEquals("INVALID_ID", ((BadRequestError)e).getError().code());
                })
                .verify();
        Mockito.verify(service, Mockito.times(0)).save(anyString(), any(Resource.class), any(Context.class));
        Mockito.verify(validator, Mockito.times(0)).validate(any(Resource.class));
    }

    @Test
    void testPatchResource() {
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceController controller = new ResourceController(service, validator);
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();

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
        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        var resourceCaptor = ArgumentCaptor.forClass(ResourceForPatch.class);

        Mockito.when(validator.validate(resourceCaptor.capture())).thenReturn(Mono.just(patcher));

        Mockito.when(service.patch(
                        appKeyCaptor.capture(),
                        idCaptor.capture(),
                        resourceCaptor.capture(),
                        contextCaptor.capture()))
                .thenReturn(Mono.just(Void.create()));

        StepVerifier.create(controller.patchResource(appKey, id, patcher))
                .assertNext( response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.status());
                    assertNull(response.body());
                })
                .verifyComplete();
        assertNotNull(contextCaptor.getValue());
        assertEquals(appKey, appKeyCaptor.getValue());
        assertEquals(id, idCaptor.getValue());
        assertEquals(patcher, resourceCaptor.getAllValues().getLast());
        Mockito.verify(service, Mockito.times(1)).patch(anyString(), any(UUID.class), any(ResourceForPatch.class), any(Context.class));
        Mockito.verify(validator, Mockito.times(1)).validate(any(ResourceForPatch.class));

    }

    @Test
    void testDeleteResource() {
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceController controller = new ResourceController(service, validator);
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();
        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var idCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.when(service.remove(
                        appKeyCaptor.capture(),
                        idCaptor.capture()))
                .thenReturn(Mono.just(Void.create()));
        StepVerifier.create(controller.deleteResource(appKey, id))
                .assertNext( response -> {
                    assertEquals(HttpStatus.NO_CONTENT, response.status());
                    assertNull(response.body());
                })
                .verifyComplete();
        assertEquals(appKey, appKeyCaptor.getValue());
        assertEquals(id, idCaptor.getValue());
        Mockito.verify(service, Mockito.times(1)).remove(anyString(), any(UUID.class));
    }

    @Test
    void testCreateResource() {
        ResourceService service = Mockito.mock(ResourceService.class);
        ResourceValidator validator = Mockito.mock(ResourceValidator.class);
        ResourceController controller = new ResourceController(service, validator);

        var appKey = UUID.randomUUID().toString().split("-")[0];
        var expectedResource = Resource.builder()
                .id(UUID.randomUUID())
                .type(UUID.randomUUID().toString().split("-")[0])
                .applicationKey(appKey)
                .build();

        var appKeyCaptor = ArgumentCaptor.forClass(String.class);
        var contextCaptor = ArgumentCaptor.forClass(Context.class);
        var resourceCaptor = ArgumentCaptor.forClass(Resource.class);

        Mockito.when(validator.validate(resourceCaptor.capture())).thenReturn(Mono.just(expectedResource));

        Mockito.when(service.create(
                        appKeyCaptor.capture(),
                        resourceCaptor.capture(),
                        contextCaptor.capture()))
                .thenReturn(Mono.just(expectedResource));

        StepVerifier.create(controller.createResource(appKey, expectedResource))
                .assertNext( response -> {
                    assertEquals(HttpStatus.CREATED, response.status());
                    assertSame(expectedResource, response.body());
                })
                .verifyComplete();
        assertNotNull(contextCaptor.getValue());
        assertEquals(appKey, appKeyCaptor.getValue());
        assertEquals(expectedResource, resourceCaptor.getValue());
        Mockito.verify(service, Mockito.times(1)).create(anyString(), any(Resource.class), any(Context.class));
        Mockito.verify(validator, Mockito.times(1)).validate(any(Resource.class));
    }
}