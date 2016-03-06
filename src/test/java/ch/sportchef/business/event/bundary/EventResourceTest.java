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
package ch.sportchef.business.event.bundary;

import ch.sportchef.business.event.boundary.EventImageResource;
import ch.sportchef.business.event.boundary.EventResource;
import ch.sportchef.business.event.control.EventImageService;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;
import ch.sportchef.business.event.entity.EventBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.junit.NeedleBuilders;
import org.needle4j.junit.NeedleRule;
import org.needle4j.mock.EasyMockProvider;

import javax.inject.Inject;
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
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventResourceTest {

    @Rule
    public NeedleRule needleRule = NeedleBuilders.needleMockitoRule().build();

    private EventResource eventResource;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private EventService eventServiceMock;

    @Inject
    private EventImageService eventImageServiceMock;

    @Inject
    private UriInfo uriInfoMock;

    @Inject
    private UriBuilder uriBuilderMock;

    @Before
    public void setup() {
        eventResource = new EventResource(1L, eventServiceMock, eventImageServiceMock);
    }

    @Test
    public void findWithSuccess() {
        // arrange
        final Event testEvent = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();
        expect(eventServiceMock.findByEventId(anyObject())).andStubReturn(Optional.of(testEvent));
        mockProvider.replayAll();

        // act
        final Event event = eventResource.find();

        // assert
        assertThat(event, is(testEvent));
        mockProvider.verifyAll();
    }

    @Test(expected=NotFoundException.class)
    public void findWithNotFound() {
        // arrange
        expect(eventServiceMock.findByEventId(anyObject())).andStubReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        eventResource.find();
    }

    @Test
    public void updateWithSuccess() throws URISyntaxException {
        // arrange
        final Event testEvent = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();
        final String location = "http://localhost:8080/sportchef/api/events/1";
        final URI uri = new URI(location);

        expect(eventServiceMock.findByEventId(testEvent.getEventId())).andStubReturn(Optional.of(testEvent));
        expect(eventServiceMock.update(anyObject())).andStubReturn(testEvent);
        expect(uriInfoMock.getAbsolutePathBuilder()).andStubReturn(uriBuilderMock);
        expect(uriBuilderMock.build()).andStubReturn(uri);
        mockProvider.replayAll();

        // act
        final Response response = eventResource.update(testEvent, uriInfoMock);
        final Event event = (Event) response.getEntity();

        //assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        assertThat(event, is(testEvent));
        mockProvider.verifyAll();
    }

    @Test(expected=NotFoundException.class)
    public void updateWithNotFound() {
        // arrange
        final Event testEvent = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();

        expect(eventServiceMock.findByEventId(testEvent.getEventId())).andStubReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        eventResource.update(testEvent, uriInfoMock);
    }

    @Test
    public void deleteWithoutImageSuccess() {
        // arrange
        final Event testEvent = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();

        expect(eventServiceMock.findByEventId(testEvent.getEventId())).andStubReturn(Optional.of(testEvent));
        eventImageServiceMock.deleteImage(testEvent.getEventId());
        expectLastCall().andThrow(new NotFoundException());
        mockProvider.replayAll();

        // act
        final Response response = eventResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        mockProvider.verifyAll();
    }

    @Test
    public void deleteWithImageSuccess() {
        // arrange
        final Event testEvent = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();

        expect(eventServiceMock.findByEventId(testEvent.getEventId())).andStubReturn(Optional.of(testEvent));
        mockProvider.replayAll();

        // act
        final Response response = eventResource.delete();

        //assert
        assertThat(response.getStatus(), is(NO_CONTENT.getStatusCode()));
        mockProvider.verifyAll();
    }

    @Test(expected=NotFoundException.class)
    public void deleteWithNotFound() {
        // arrange
        expect(eventServiceMock.findByEventId(anyObject())).andStubReturn(Optional.empty());
        mockProvider.replayAll();

        // act
        eventResource.delete();
    }

    @Test
    public void image() {
        // arrange
        final Event testEvent = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();

        expect(eventServiceMock.findByEventId(testEvent.getEventId())).andStubReturn(Optional.of(testEvent));
        mockProvider.replayAll();

        // act
        final EventImageResource eventImageResource = eventResource.image();

        // assert
        assertThat(eventImageResource, notNullValue());
        mockProvider.verifyAll();
    }

}
