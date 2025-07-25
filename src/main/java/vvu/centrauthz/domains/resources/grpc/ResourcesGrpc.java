package vvu.centrauthz.domains.resources.grpc;

import io.micronaut.json.JsonMapper;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import resources.ResourcesServiceGrpc;
import resources.Resources.*;
import io.grpc.stub.*;
import vvu.centrauthz.domains.resources.grpc.utilities.GrpcContext;
import vvu.centrauthz.domains.resources.grpc.utilities.GrpcUtils;
import vvu.centrauthz.domains.resources.services.ResourceService;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.exceptions.EUtils;
import vvu.centrauthz.utilities.Context;
import vvu.centrauthz.utilities.StringTools;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Singleton
public class ResourcesGrpc extends ResourcesServiceGrpc.ResourcesServiceImplBase {

    private final ResourceService resourceService;
    private final Validator validator;
    private final JsonMapper jsonMapper;

    public ResourcesGrpc(Validator validator, JsonMapper jsonMapper, ResourceService resourceService) {
        this.validator = validator;
        this.resourceService = resourceService;
        this.jsonMapper = jsonMapper;
    }

    private void idIsRequired(String id) {
        if (StringUtils.isBlank(id)) {
            throw EUtils.createBadRequestError("ID is required");
        }
    }

    private void applicationKeyIsRequired(String applicationKey) {
        if (StringUtils.isBlank(applicationKey)) {
            throw EUtils.createBadRequestError("Application key is required");
        }
    }

    private void validateResource(vvu.centrauthz.domains.resources.models.Resource resource) {
        try {
            validator.validate(resource);
        } catch (ConstraintViolationException e) {
            var details = StringTools.exceptionToMap(e);
            var error = vvu.centrauthz.models.Error.builder()
                    .code("VALIDATION_ERROR")
                    .details(details)
                    .build();
            throw new BadRequestError(error);
        }
    }

    @Override
    public void getResource(
            GetResourceRequest request,
            StreamObserver<GetResourceResponse> responseObserver) {
        GrpcContext.create(responseObserver)
                .withLogger(log).execute( () -> {
                    idIsRequired(request.getId());
                    applicationKeyIsRequired(request.getApplicationKey());
                    var id = GrpcUtils.idToUUID(request);
                    var applicationKey = request.getApplicationKey();
                    if (StringUtils.isBlank(applicationKey)) {
                        return CompletableFuture.failedFuture(EUtils.createBadRequestError("Application key is required"));
                    }
                    return resourceService
                            .get(applicationKey, id, Context.from(id,applicationKey))
                            .toFuture().thenApply( v -> GrpcUtils.toGetResourceResponse(jsonMapper, v));
                });
    }

    @Override
    public void putResource(
            PutResourceRequest request,
            StreamObserver<PutResourceResponse> responseObserver) {
        GrpcContext.create(responseObserver)
                .withLogger(log).execute( () -> {
                    var resource = GrpcUtils.convert(jsonMapper, request.getResource());
                    validateResource(resource);
                    idIsRequired(request.getResource().getId());
                    applicationKeyIsRequired(request.getResource().getApplicationKey());

                    return resourceService
                            .save(resource.applicationKey(), resource, Context.from(resource.id(),resource.applicationKey()))
                            .toFuture().thenApply( v -> PutResourceResponse.newBuilder().build());
                });
    }

}
