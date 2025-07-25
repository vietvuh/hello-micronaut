package vvu.centrauthz.utilities;

import lombok.Builder;
import vvu.centrauthz.models.User;

import java.util.Objects;
import java.util.UUID;

@Builder(toBuilder = true)
public record Context(User user, String appKey) {
    public Context {
        if (Objects.isNull(user)) {
            user = User.builder().build();
        }
    }

    public static Context from(UUID userId, String appKey) {
        var user = User.builder().id(userId).build();
        return Context.builder()
            .user(user)
            .appKey(appKey)
            .build();
    }

}
