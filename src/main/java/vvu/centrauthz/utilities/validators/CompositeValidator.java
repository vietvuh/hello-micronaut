package vvu.centrauthz.utilities.validators;

import java.util.HashSet;
import java.util.Set;

public class CompositeValidator implements Validatable {
    private final Set<Validatable> validators = new HashSet<>();

    public CompositeValidator add(Validatable validator) {
        validators.add(validator);
        return this;
    }

    @Override
    public void validate() {
        validators.forEach(Validatable::validate);
    }
}
