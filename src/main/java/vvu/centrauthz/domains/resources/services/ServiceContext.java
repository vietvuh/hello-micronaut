package vvu.centrauthz.domains.resources.services;

import lombok.Builder;
import vvu.centrauthz.domains.resources.models.User;

import java.util.Objects;

@Builder(toBuilder = true)
public record ServiceContext(User user) {
    public ServiceContext {
        if (Objects.isNull(user)) {
            user = User.builder().build();
        }
    }
}
