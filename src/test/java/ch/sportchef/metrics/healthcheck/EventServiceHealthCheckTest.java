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
package ch.sportchef.metrics.healthcheck;

import ch.sportchef.business.event.control.EventService;
import com.codahale.metrics.health.HealthCheck;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventServiceHealthCheckTest {

    @Test
    public void checkHealthy() throws Exception {
        // arrange
        final EventService eventServiceMock = mock(EventService.class);
        when(eventServiceMock.findAll()).thenReturn(new ArrayList<>(0));
        final EventServiceHealthCheck healthCheck = new EventServiceHealthCheck(eventServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.healthy()));
    }

    @Test
    public void checkUnhealthy() throws Exception {
        // arrange
        final EventService eventServiceMock = mock(EventService.class);
        when(eventServiceMock.findAll()).thenReturn(null);
        final EventServiceHealthCheck healthCheck = new EventServiceHealthCheck(eventServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.unhealthy("Can't access events!")));
    }

    @Test
    public void checkException() throws Exception {
        // arrange
        final EventService eventServiceMock = mock(EventService.class);
        when(eventServiceMock.findAll()).thenThrow(new RuntimeException("Test Message"));
        final EventServiceHealthCheck healthCheck = new EventServiceHealthCheck(eventServiceMock);

        // act
        final HealthCheck.Result result = healthCheck.check();

        // assert
        assertThat(result, is(HealthCheck.Result.unhealthy("Test Message")));
    }

}