package ch.sportchef.metrics.healthcheck;

import ch.sportchef.business.authentication.control.AuthenticationService;
import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class AuthenticationServiceHealthCheck extends HealthCheck {

    private final AuthenticationService authenticationService;

    public AuthenticationServiceHealthCheck(@NotNull final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected Result check() throws Exception {
        try {
            final Optional<String> token = authenticationService.validateChallenge("foo@bar", "foobar");
            return token.isPresent() ? Result.healthy() :
                    Result.unhealthy(String.format("Problems in AuthenticationService: Can't validate challenge!"));
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
