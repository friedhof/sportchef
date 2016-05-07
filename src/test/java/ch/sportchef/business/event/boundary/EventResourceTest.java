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
package ch.sportchef.business.event.boundary;

import ch.sportchef.business.event.control.EventImageService;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventResourceTest {

    private EventResource eventResource;
    private EventService eventServiceMock;
    private EventImageService eventImageServiceMock;

    @Before
    public void setup() {
        eventServiceMock = mock(EventService.class);
        eventImageServiceMock = mock(EventImageService.class);
        eventResource = new EventResource(1L, eventServiceMock, eventImageServiceMock);
    }

    @Test
    public void findWithSuccess() {
        // arrange
        final Event testEvent = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        when(eventServiceMock.findByEventId(1L))
                .thenReturn(Optional.of(testEvent));

        // act
        final Event event = eventResource.find();

        // assert
        assertThat(event, is(testEvent));
        verify(eventServiceMock, times(1)).findByEventId(1L);
    }

    @Test(expected=NotFoundException.class)
    public void findWithNotFound() {
        // arrange
        when(eventServiceMock.findByEventId(1L))
                .thenReturn(Optional.empty());

        // act
        eventResource.find();
    }

    @Test
    public void updateWithSuccess() throws URISyntaxException {
        // arrange
        final Event testEvent = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        when(eventServiceMock.findByEventId(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        when(eventServiceMock.update(anyObject())).thenReturn(testEvent);
        final UriInfo uriInfoMock = mock(UriInfo.class);
        final UriBuilder uriBuilderMock = mock(UriBuilder.class);
        when(uriInfoMock.getAbsolutePathBuilder()).thenReturn(uriBuilderMock);
        final String location = "http://localhost:8080/sportchef/api/events/1";
        final URI uri = new URI(location);
        when(uriBuilderMock.build()).thenReturn(uri);

        // act
        final Response response = eventResource.update(testEvent, uriInfoMock);
        final Event event = (Event) response.getEntity();

        //assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertThat(event, is(testEvent));
        verify(eventServiceMock, times(1)).findByEventId(testEvent.getEventId());
        verify(eventServiceMock, times(1)).update(anyObject());
        verify(uriInfoMock, times(1)).getAbsolutePathBuilder();
        verify(uriBuilderMock, times(1)).build();
    }

    @Test(expected=NotFoundException.class)
    public void updateWithNotFound() {
        // arrange
        final Event testEvent = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        final UriInfo uriInfoMock = mock(UriInfo.class);
        when(eventServiceMock.findByEventId(testEvent.getEventId())).thenReturn(Optional.empty());

        // act
        eventResource.update(testEvent, uriInfoMock);
    }

    @Test
    public void deleteWithoutImageSuccess() {
        // arrange
        final Event testEvent = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        when(eventServiceMock.findByEventId(testEvent.getEventId())).thenReturn(Optional.of(testEvent));
        doThrow(new NotFoundException()).when(eventImageServiceMock).deleteImage(testEvent.getEventId());

        // act
        final Response response = eventResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        verify(eventServiceMock, times(1)).findByEventId(testEvent.getEventId());
        verify(eventImageServiceMock, times(1)).deleteImage(testEvent.getEventId());
    }

    @Test
    public void deleteWithImageSuccess() {
        // arrange
        final Event testEvent = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        when(eventServiceMock.findByEventId(testEvent.getEventId())).thenReturn(Optional.of(testEvent));

        // act
        final Response response = eventResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        verify(eventServiceMock, times(1)).findByEventId(testEvent.getEventId());
    }

    @Test(expected=NotFoundException.class)
    public void deleteWithNotFound() {
        // arrange
        when(eventServiceMock.findByEventId(anyObject())).thenReturn(Optional.empty());

        // act
        eventResource.delete();
    }

    @Test
    public void image() {
        // arrange
        final Event testEvent = Event.builder()
                .eventId(1L)
                .title("Testevent")
                .location("Testlocation")
                .date(LocalDate.of(2099, Month.DECEMBER, 31))
                .time(LocalTime.of(22, 0))
                .build();
        when(eventServiceMock.findByEventId(testEvent.getEventId())).thenReturn(Optional.of(testEvent));

        // act
        final EventImageResource eventImageResource = eventResource.image();

        // assert
        assertThat(eventImageResource, notNullValue());
        verify(eventServiceMock, times(1)).findByEventId(testEvent.getEventId());
    }

}
