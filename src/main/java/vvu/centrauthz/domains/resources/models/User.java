package vvu.centrauthz.domains.resources.models;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;

import java.util.UUID;

@Serdeable
@Introspected
@Builder(toBuilder = true)
public record User(UUID id, String email, String name) {
}
