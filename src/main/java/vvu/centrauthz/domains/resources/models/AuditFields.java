package vvu.centrauthz.domains.resources.models;

import lombok.Builder;
import io.micronaut.serde.annotation.Serdeable;
import java.util.UUID;

/**
 * Audit fields for tracking creation and modification information.
 * Timestamps are stored as epoch time in milliseconds.
 */
@Serdeable
@Builder(toBuilder = true)
public record AuditFields(
    Long createdAt,
    UUID createdBy,
    Long updatedAt,
    UUID updatedBy
) {
}
