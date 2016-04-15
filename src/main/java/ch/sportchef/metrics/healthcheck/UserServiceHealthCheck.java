package ch.sportchef.metrics.healthcheck;

import ch.sportchef.business.user.control.UserService;
import ch.sportchef.business.user.entity.User;
import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;
import java.util.List;

public class UserServiceHealthCheck extends HealthCheck {

    private final UserService userService;

    public UserServiceHealthCheck(@NotNull final UserService userService) {
        this.userService = userService;
    }

    @Override
    protected Result check() throws Exception {
        try {
            final List<User> users = userService.findAll();
            return users != null ? Result.healthy() : Result.unhealthy("can't access users");
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
