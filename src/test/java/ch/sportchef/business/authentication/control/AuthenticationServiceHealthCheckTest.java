/*
 * SportChef – Sports Competition Management Software
 * Copyright (C) 2016 Marcus Fihlon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.authentication.control;

import com.codahale.metrics.health.HealthCheck;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationServiceHealthCheckTest {

    @Test
    public void checkHealthy() throws Exception {
        // arrange
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validateChallenge(anyString(), anyString())).thenReturn(Optional.of("ok"));
        final AuthenticationServiceHealthCheck healthCheck = new AuthenticationServiceHealthCheck(authenticationServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.healthy()));
    }

    @Test
    public void checkUnhealthy() throws Exception {
        // arrange
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validateChallenge(anyString(), anyString())).thenReturn(Optional.empty());
        final AuthenticationServiceHealthCheck healthCheck = new AuthenticationServiceHealthCheck(authenticationServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.unhealthy("Problems in AuthenticationService: Can't validate challenge!")));
    }

    @Test
    public void checkException() throws Exception {
        // arrange
        final AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validateChallenge(anyString(), anyString())).thenThrow(new RuntimeException("Test Message"));
        final AuthenticationServiceHealthCheck healthCheck = new AuthenticationServiceHealthCheck(authenticationServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.unhealthy("Test Message")));
    }

}