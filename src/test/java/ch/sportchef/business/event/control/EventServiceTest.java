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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ch.sportchef.business.event.control;

import ch.sportchef.business.PersistenceManager;
import ch.sportchef.business.event.entity.Event;
import com.codahale.metrics.health.HealthCheckRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import pl.setblack.airomem.core.SimpleController;
import pl.setblack.airomem.core.VoidCommand;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PersistenceManager.class)
public class EventServiceTest {

    private static final String EVENT_TITLE = "Test Event Title";
    private static final String EVENT_LOCATION = "Test Event Location";
    private static final LocalDate EVENT_DATE = LocalDate.now();
    private static final LocalTime EVENT_TIME = LocalTime.now();

    @Test
    public void create() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final HealthCheckRegistry healthCheckRegistryMock = mock(HealthCheckRegistry.class);
        final EventService eventService = new EventService(healthCheckRegistryMock);
        final Event eventToCreate = Event.builder()
                .title(EVENT_TITLE)
                .location(EVENT_LOCATION)
                .date(EVENT_DATE)
                .time(EVENT_TIME)
                .build();

        // act
        eventService.create(eventToCreate);

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void update() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final HealthCheckRegistry healthCheckRegistryMock = mock(HealthCheckRegistry.class);
        final EventService eventService = new EventService(healthCheckRegistryMock);
        final Event eventToUpdate = Event.builder()
                .title(EVENT_TITLE)
                .location(EVENT_LOCATION)
                .date(EVENT_DATE)
                .time(EVENT_TIME)
                .build();

        // act
        eventService.update(eventToUpdate);

        // assert
        verify(simpleControllerMock, times(1)).executeAndQuery(anyObject());
    }

    @Test
    public void findByEventId() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final EventRepository eventRepositoryMock = mock(EventRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(eventRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final HealthCheckRegistry healthCheckRegistryMock = mock(HealthCheckRegistry.class);
        final EventService eventService = new EventService(healthCheckRegistryMock);

        // act
        eventService.findByEventId(1L);

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(eventRepositoryMock, times(1)).findByEventId(1L);
    }

    @Test
    public void findAll() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        final EventRepository eventRepositoryMock = mock(EventRepository.class);
        when(simpleControllerMock.readOnly()).thenReturn(eventRepositoryMock);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final HealthCheckRegistry healthCheckRegistryMock = mock(HealthCheckRegistry.class);
        final EventService eventService = new EventService(healthCheckRegistryMock);

        // act
        eventService.findAll();

        // assert
        verify(simpleControllerMock, times(1)).readOnly();
        verify(eventRepositoryMock, times(1)).findAll();
    }

    @Test
    public void delete() {
        // arrange
        final SimpleController<Serializable> simpleControllerMock = mock(SimpleController.class);
        mockStatic(PersistenceManager.class);
        when(PersistenceManager.createSimpleController(any(), any())).thenReturn(simpleControllerMock);
        final HealthCheckRegistry healthCheckRegistryMock = mock(HealthCheckRegistry.class);
        final EventService eventService = new EventService(healthCheckRegistryMock);

        // act
        eventService.delete(1L);

        // assert
        verify(simpleControllerMock, times(1)).execute(any(VoidCommand.class));
    }

}