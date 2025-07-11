package vvu.centrauthz.domains.resources.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record Resource(
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

    @Nullable
    Long createdAt,
    
    @Nullable
    UUID createdBy,
    
    @Nullable
    Long updatedAt,
    
    @Nullable
    UUID updatedBy
) {
    public Resource patch(ResourceForPatch patchValue) {
        // Apply the patch to this resource
        var builder = this.toBuilder();

        if (patchValue.updatedFields().contains("ownerId")) {
            builder.ownerId(patchValue.data().ownerId());
        }

        if (patchValue.updatedFields().contains("parentId")) {
            builder.ownerId(patchValue.data().parentId());
        }

        if (patchValue.updatedFields().contains("sharedWith")) {
            builder.sharedWith(patchValue.data().sharedWith());
        }

        if (patchValue.updatedFields().contains("tags")) {
            builder.tags(patchValue.data().tags());
        }

        if (patchValue.updatedFields().contains("details")) {
            builder.details(patchValue.data().details());
        }

        return builder.build();
    }
}
