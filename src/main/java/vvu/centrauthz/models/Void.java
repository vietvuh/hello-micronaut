package vvu.centrauthz.models;

import lombok.Builder;

@Builder(toBuilder = true)
public record Void() {
    public static Void create() {
        return Void.builder().build();
    }
}
