package vvu.centrauthz.domains.resources.controllers;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.ResourceForPatch;
import vvu.centrauthz.domains.resources.services.ResourceService;
import vvu.centrauthz.utilities.Context;
import vvu.centrauthz.exceptions.BadRequestError;
import java.util.Objects;
import java.util.UUID;

/**
 * Reactive RESTful API controller for resource management.
 * All endpoints currently return NOT_IMPLEMENTED errors.
 * Validation is handled by Micronaut validation framework.
 */
@Controller("/v0/applications/{applicationKey}/resources")
public class ResourceController {

    private final ResourceService service;
    private final ResourceValidator validator;

    public ResourceController(ResourceService service, ResourceValidator validator) {
        this.validator = validator;
        this.service = service;
    }

    /**
     * Get a resource by ID.
     *
     * @param applicationKey Application key (minimum 3 characters)
     * @param id             Unique identifier for the resource
     * @return Mono containing the resource
     */
    @Get("/{id}")
    public Mono<HttpResponse<Resource>> getResource(
            @PathVariable @NonNull String applicationKey,
            @PathVariable @NonNull UUID id) {

        return service.get(applicationKey, id).map(HttpResponse::ok);
    }

    /**
     * Update a resource.
     *
     * @param applicationKey Application key (minimum 3 characters)
     * @param id             Unique identifier for the resource
     * @param resource       Resource data to update
     * @return Mono containing the updated resource
     */
    @Put("/{id}")
    public Mono<HttpResponse<Resource>> updateResource(
            @PathVariable @NonNull String applicationKey,
            @PathVariable @NonNull UUID id,
            @Body @Valid @NonNull Resource resource) {

        if (Objects.nonNull(resource.id()) && !resource.id().equals(id)) {
            return Mono.error(new BadRequestError("INVALID_ID", "Resource ID in path does not match resource ID in body"));
        }

        return validator.validate(resource)
                .map(r -> r.toBuilder().id(id).build())
                .flatMap(r -> service.save(applicationKey, r, Context.builder().build()))
                .map( v -> HttpResponse.noContent());
    }

    /**
     * Partially update a resource.
     *
     * @param applicationKey Application key (minimum 3 characters)
     * @param id             Unique identifier for the resource
     * @param resourcePatch  Resource patch data
     * @return Mono containing the updated resource
     */
    @Patch("/{id}")
    public Mono<HttpResponse<Resource>> patchResource(
            @PathVariable @NonNull String applicationKey,
            @PathVariable @NonNull UUID id,
            @Body @NonNull ResourceForPatch resourcePatch) {

        return  validator.validate(resourcePatch)
                .flatMap(patcher -> service.patch(applicationKey, id, patcher, Context.builder().build()))
                .map( v -> HttpResponse.noContent());
    }

    /**
     * Delete a resource.
     *
     * @param applicationKey Application key (minimum 3 characters)
     * @param id             Unique identifier for the resource
     * @return Mono indicating completion
     */
    @Delete("/{id}")
    public Mono<HttpResponse<Void>> deleteResource(
            @PathVariable @NonNull String applicationKey,
            @PathVariable @NonNull UUID id) {

        return service.remove(applicationKey, id).map(v -> HttpResponse.noContent());
    }

    /**
     * Create a new resource.
     *
     * @param applicationKey Application key (minimum 3 characters)
     * @param resource       Resource data to create
     * @return Mono containing the created resource
     */
    @Post
    public Mono<HttpResponse<Resource>> createResource(
            @PathVariable @NonNull String applicationKey,
            @Body @NonNull Resource resource) {
        return validator.validate(resource).flatMap(r -> service
                        .create(applicationKey, r, Context.builder().build()))
                .map(HttpResponse::created);
    }
}
