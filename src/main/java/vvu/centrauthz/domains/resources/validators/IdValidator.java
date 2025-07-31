package vvu.centrauthz.domains.resources.validators;

import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.models.Error;
import vvu.centrauthz.utilities.validators.Validatable;

import java.util.Objects;
import java.util.UUID;

public class IdValidator implements Validatable {

    private final UUID id;
    private final Resource resource;

    public IdValidator(UUID id, Resource resource) {
        this.id = Objects.requireNonNull(id);
        this.resource = Objects.requireNonNull(resource);
    }

    @Override
    public void validate() {
        if (!Objects.equals(id, resource.id())) {
            var error = Error.builder()
                    .code("INVALID_ID")
                    .message("Resource ID in path does not match resource ID in body")
                    .build();
            throw new BadRequestError(error);
        }
    }
}
