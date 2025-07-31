package vvu.centrauthz.domains.resources.validators;

import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.models.Error;
import vvu.centrauthz.utilities.StringTools;
import vvu.centrauthz.utilities.validators.Validatable;

import java.util.Objects;

public class AppKeyValidator implements Validatable {

    private final String appKey;
    private final Resource resource;

    public AppKeyValidator(String appKey, Resource resource) {
        this.appKey = Objects.requireNonNull(appKey);
        this.resource = Objects.requireNonNull(resource);

        if (StringTools.isBlank(appKey)) {
            throw new IllegalArgumentException("Application key cannot be empty");
        }

    }

    @Override
    public void validate() {
        if (!Objects.equals(appKey, resource.applicationKey())) {
            var error = Error.builder()
                    .code("INVALID_APPLICATION_KEY")
                    .message("Application key does not match resource")
                    .build();
            throw new BadRequestError(error);
        }
    }

    public static AppKeyValidator of(String appKey, Resource resource) {
        return new AppKeyValidator(appKey, resource);
    }
}
