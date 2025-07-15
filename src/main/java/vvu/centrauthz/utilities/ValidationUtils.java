package vvu.centrauthz.utilities;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import vvu.centrauthz.exceptions.BadRequestError;
import vvu.centrauthz.models.Error;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ValidationUtils {

    ValidationUtils() {
      throw new IllegalStateException("Utility class");
    }

    public static <T> Mono<T> validate(Validator validator, T resource) {

        Set<ConstraintViolation<T>> violations = validator.validate(resource);

        if (violations.isEmpty()) {
            log.info("Resource {} is valid", resource);
            return Mono.just(resource);
        }

        var details = violations
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        var error = Error.builder()
                .code("VALIDATION_ERROR")
                .details(details)
                .build();


        return Mono.error(new BadRequestError(error));
    }

}
