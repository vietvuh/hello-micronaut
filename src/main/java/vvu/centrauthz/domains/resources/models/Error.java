package vvu.centrauthz.domains.resources.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import io.micronaut.serde.annotation.Serdeable;
import java.util.Map;

/**
 * Error model for API error responses.
 */
@Serdeable
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record Error(
    String code,
    String message,
    Map<String, String> details
) {
}
