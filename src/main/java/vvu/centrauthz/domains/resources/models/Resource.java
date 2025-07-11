package vvu.centrauthz.domains.resources.models;

import lombok.Builder;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Resource model representing a resource with its properties and audit information.
 * Audit fields are flattened directly into this record.
 */
@Serdeable
@Introspected
@Builder(toBuilder = true)
public record Resource(
    @NonNull
    UUID id,
    
    @NonNull
    String type,
    
    @NonNull
    UUID ownerId,
    
    @Nullable
    UUID parentId,
    
    @Nullable
    List<UUID> sharedWith,
    
    @Nullable
    List<String> tags,
    
    @Nullable
    Map<String, Object> details,
    
    // Audit fields - flattened
    @Nullable
    Long createdAt,
    
    @Nullable
    UUID createdBy,
    
    @Nullable
    Long updatedAt,
    
    @Nullable
    UUID updatedBy
) {
}
