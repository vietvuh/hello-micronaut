package vvu.centrauthz.domains.resources.models;

import lombok.Builder;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

/**
 * Error model for API error responses.
 */
@Serdeable
@Builder(toBuilder = true)
public record Error(
    String code,
    String message,
    Map<String, String> details
) {
}
