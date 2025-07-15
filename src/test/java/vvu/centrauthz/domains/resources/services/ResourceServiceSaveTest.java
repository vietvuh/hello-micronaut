package vvu.centrauthz.domains.resources.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.exceptions.NotFoundError;
import vvu.centrauthz.models.Void;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("ResourceService Save method tests")
public class ResourceServiceSaveTest {
    @Test
    void save_resourceNotExisting_NotFoundError() {
        var eMessage = UUID.randomUUID().toString();
        var appKey = UUID.randomUUID().toString().split("-")[0];
        var id = UUID.randomUUID();
        var mocker = ResourceServiceMocker.create();
        var resourceService = mocker
                .forReadable(r -> Mockito.when(r.get(appKey, id)).thenReturn(Mono.error(EUtils.createNotFoundError(eMessage))))
                .withReadableVerifier(r -> {
                    Mockito.verify(r, Mockito.only()).get(appKey, id);
                })
                .forWritable( w -> Mockito.when(w.save(anyString(), any(Resource.class))).thenReturn(Mono.just(Void.create())))
                .withWritableVerifier( v -> {
                    Mockito.verify(v, Mockito.never()).save(anyString(), any(Resource.class));
                })
                .build();

        // When & Then
        StepVerifier.create(resourceService.get(appKey, id))
                .expectErrorSatisfies(e -> {
                    Assertions.assertInstanceOf(NotFoundError.class, e);
                    var error = ((NotFoundError) e).getError();
                    assertEquals("NOT_FOUND", error.code());
                    assertEquals(eMessage, error.message());
                    assertNull(error.details());
                }).verify();
        mocker.verify();
    }
}
