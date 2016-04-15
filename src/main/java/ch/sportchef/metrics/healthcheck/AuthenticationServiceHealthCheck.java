package ch.sportchef.metrics.healthcheck;

import ch.sportchef.business.authentication.control.AuthenticationService;
import ch.sportchef.business.authentication.entity.Role;
import ch.sportchef.business.user.entity.User;
import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;

public class AuthenticationServiceHealthCheck extends HealthCheck {

    private final AuthenticationService authenticationService;

    private final User user;

    private final Role role;

    public AuthenticationServiceHealthCheck(@NotNull final AuthenticationService authenticationService,
                                            @NotNull final User user) {
        this.authenticationService = authenticationService;
        this.user = user;
        this.role = Role.USER;
    }

    @Override
    protected Result check() throws Exception {
        try {
            final boolean isUserInRole = authenticationService.isUserInRole(user, role);
            return isUserInRole ? Result.healthy() : Result.unhealthy(String.format("user %s is not in role %s", user, role));
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
