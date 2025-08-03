package vvu.centrauthz.domains.applications.controllers;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.applications.services.ApplicationService;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.validators.ResourceValidator;
import vvu.centrauthz.utilities.ConstantValues;
import vvu.centrauthz.utilities.Context;

import java.util.UUID;

@Controller("/v0/applications")
public class ApplicationController {

    private ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }
    @Get("/{key}")
    public Mono<HttpResponse<Resource>> getApplication(
            @Header(ConstantValues.X_USER_ID_HEADER) @Nullable UUID userId,
            @PathVariable @NotEmpty String key) {

        var context = Context.from(userId, key);
        return service.get(key, context).map(HttpResponse::ok);
    }

    @Put("/{key}")
    public Mono<HttpResponse<Resource>> updateResource(
            @Header(ConstantValues.X_USER_ID_HEADER) @Nullable UUID userId,
            @PathVariable @NonNull String key,
            @Body @Valid @NonNull Resource resource) {
        var context = Context.from(userId, key);

        return Mono.just(resource)
                .doOnNext( r -> ResourceValidator.create(r, key, r.id()).validate())
                .flatMap(r -> service.save(key, r, context))
                .map( v -> HttpResponse.noContent());
    }
}
