package vvu.centrauthz.domains.resources.controllers;

import jakarta.inject.Singleton;
import jakarta.validation.Validator;
import reactor.core.publisher.Mono;
import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.domains.resources.models.ResourceForPatch;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.models.Error;
import vvu.centrauthz.utilities.StringTools;
import vvu.centrauthz.utilities.ValidationUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Singleton
public class ResourceValidator {
    private final Validator validator;

    public ResourceValidator(Validator validator) {
        this.validator = validator;
    }

    private BadRequestError validationError(Map<String, String> details) {
        var error = Error.builder()
                .code("VALIDATION_ERROR")
                .details(details)
                .build();
        return new BadRequestError(error);
    }

    public Mono<Resource> validate(Resource resource) {

        Map<String, String> details = new HashMap<>();

        if (StringTools.isBlank(resource.applicationKey())) {
            details.put("applicationKey", "Application key is required");
        }

        if (StringTools.isBlank(resource.type())) {
            details.put("type", "type is required");
        }

        if (Objects.isNull(resource.ownerId())) {
            details.put("ownerId", "ownerId is required");
        }

        if (!details.isEmpty()) {
            return Mono.error(validationError(details));
        }

        return ValidationUtils.validate(validator, resource);
    }

    public  Mono<ResourceForPatch> validate(ResourceForPatch resource) {

        Map<String, String> details = new HashMap<>();

        if (Objects.isNull(resource.updatedFields()) || resource.updatedFields().isEmpty()) {
            details.put("updatedFields", "updatedFields is required");
        }

        if (Objects.isNull(resource.data())) {
            details.put("data", "data is required");
        }

        if (!details.isEmpty()) {
            return Mono.error(validationError(details));
        }

        return ValidationUtils.validate(validator, resource);
    }

}
