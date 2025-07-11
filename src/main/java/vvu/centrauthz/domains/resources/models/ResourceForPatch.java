package vvu.centrauthz.domains.resources.models;

import lombok.Builder;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Resource patch model for partial updates.
 */
@Serdeable
@Introspected
@Builder(toBuilder = true)
public record ResourceForPatch(
    @NonNull
    List<String> updatedFields,
    
    @NonNull
    ResourcePatchData data
) {
    
    /**
     * Data object for resource patch operations.
     */
    @Serdeable
    @Introspected
    @Builder(toBuilder = true)
    public record ResourcePatchData(
        @Nullable
        String type,
        
        @Nullable
        UUID ownerId,
        
        @Nullable
        UUID parentId,
        
        @Nullable
        List<UUID> sharedWith,
        
        @Nullable
        List<String> tags,
        
        @Nullable
        java.util.Map<String, Object> details
    ) {
    }
}
