package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.utilities.Context;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("ResourceService get method tests")
class ResourceServiceGetTest {

    @Test
    void get_whenHasResource_shouldReturnResource() {
        var expectedResource = Resource.builder().build();
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();
        var context = Context.from(UUID.randomUUID(), appKey);
        var mocker = ResourceServiceMocker.create();
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKey, id)).thenReturn(Mono.just(expectedResource)))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(appKey, id);
                })
                .build();

        // When & Then
        StepVerifier.create(resourceService.get(appKey, id, context))
                .expectNext(expectedResource)
                .verifyComplete();
        mocker.verify();
    }

    @Test
    void get_whenNoResource_NotFoundError() {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();
        var mocker = ResourceServiceMocker.create();
        var context = Context.from(UUID.randomUUID(), appKey);
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKey, id)).thenReturn(Mono.empty()))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(appKey, id);
                })
                .build();

        // When & Then
        StepVerifier.create(resourceService.get(appKey, id, context))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(NotFoundError.class, e);
                    var error = ((NotFoundError) e).getError();
                    assertEquals("NOT_FOUND", error.code());
                    assertEquals(String.format("Resource with ID %s not found for application %s", id, appKey), error.message());
                    assertNull(error.details());
                }).verify();
        mocker.verify();
    }


    @Test
    void get_whenUnexpectedErrorThrown_RuntimeError() {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();
        var theError = new RuntimeException();
        var mocker = ResourceServiceMocker.create();
        var context = Context.from(UUID.randomUUID(), appKey);

        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKey, id)).thenThrow(theError))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(appKey, id);
                })
                .build();

        StepVerifier.create(resourceService.get(appKey, id, context))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(RuntimeException.class, e);
                    Assertions.assertSame(theError, e);
                })
                .verify();

    }

    @Test
    void get_whenUnexpectedErrorReturned_RuntimeError() {
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();
        var theError = new RuntimeException();
        var mocker = ResourceServiceMocker.create();
        var context = Context.from(UUID.randomUUID(), appKey);
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKey, id)).thenReturn(Mono.error(theError)))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(appKey, id);
                })
                .build();

        StepVerifier.create(resourceService.get(appKey, id, context))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(RuntimeException.class, e);
                    Assertions.assertSame(theError, e);
                })
                .verify();

    }

}
