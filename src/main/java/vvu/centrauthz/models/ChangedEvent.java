package vvu.centrauthz.models;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

@Serdeable
@Introspected
@Builder(toBuilder = true)
public record ChangedEvent<T>(EventType type, String path, T value, T originalValue, Long timestamp) {
    public enum EventType {
        CREATED,
        REMOVED,
        UPDATED,
        CHANGED
    }
}
