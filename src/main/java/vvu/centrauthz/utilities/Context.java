package vvu.centrauthz.utilities;

import lombok.Builder;
import vvu.centrauthz.models.User;

import java.util.Objects;

@Builder(toBuilder = true)
public record Context(User user, String appKey) {
    public Context {
        if (Objects.isNull(user)) {
            user = User.builder().build();
        }
    }
}
