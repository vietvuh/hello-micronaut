package vvu.centrauthz.domains.resources.validators;

import vvu.centrauthz.domains.resources.models.ResourceForPatch;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.utilities.StringTools;
import vvu.centrauthz.utilities.validators.Validatable;
import vvu.centrauthz.models.Error;

import java.util.Objects;

public class ResourceForPatchValidator implements Validatable {

    private final ResourceForPatch patcher;

    public ResourceForPatchValidator(ResourceForPatch patcher) {
        this.patcher = Objects.requireNonNull(patcher);
    }

    public void validate() {
        if (patcher.updatedFields().isEmpty()) {
            var error = Error.builder()
                    .code("NO_FIELDS_TO_UPDATE")
                    .message("No fields to update")
                    .build();
            throw new BadRequestError(error);
        }

        if (patcher.updatedFields().contains("type") && StringTools.isBlank(patcher.data().type())) {
            var error = Error.builder().code("INVALID_TYPE").message("Type is required").build();
            throw new BadRequestError(error);
        }

        if (patcher.updatedFields().contains("ownerId") && Objects.isNull(patcher.data().ownerId())) {
            var error = Error.builder().code("INVALID_OWNER_ID").message("Owner ID is required").build();
            throw new BadRequestError(error);
        }
    }

    public static ResourceForPatchValidator create(ResourceForPatch patcher) {
        return new ResourceForPatchValidator(patcher);
    }
}
