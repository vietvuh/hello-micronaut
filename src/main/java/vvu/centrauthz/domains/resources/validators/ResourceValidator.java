package vvu.centrauthz.domains.resources.validators;

import vvu.centrauthz.domains.resources.models.Resource;
import vvu.centrauthz.utilities.validators.CompositeValidator;
import vvu.centrauthz.utilities.validators.Validatable;

import java.util.UUID;

public class ResourceValidator implements Validatable {
    private final CompositeValidator validator = new CompositeValidator();

    public ResourceValidator(Resource resource, String appKey) {
        validator.add(new AppKeyValidator(appKey, resource));
    }

    public ResourceValidator(Resource resource, String appKey, UUID id) {
        this(resource, appKey);
        validator.add(new IdValidator(id, resource));
    }


    @Override
    public void validate() {
        validator.validate();
    }

    public static ResourceValidator create(Resource resource, String appKey) {
        return new ResourceValidator(resource, appKey);
    }

    public static ResourceValidator create(Resource resource, String appKey, UUID id) {
        return new ResourceValidator(resource, appKey, id);
    }
}
