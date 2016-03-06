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

import ch.sportchef.business.event.boundary.EventResource;
import ch.sportchef.business.event.boundary.EventsResource;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;
import ch.sportchef.business.event.entity.EventBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.needle4j.annotation.ObjectUnderTest;
import org.needle4j.junit.NeedleRule;
import org.needle4j.mock.EasyMockProvider;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventsResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    @ObjectUnderTest
    private EventsResource eventsResource;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private EventService eventServiceMock;

    @Inject
    private UriInfo uriInfoMock;

    @Inject
    private UriBuilder uriBuilderMock;

    @Test
    public void saveWithSuccess() throws URISyntaxException {
        // arrange
        final Event eventToCreate = EventBuilder.anEvent()
                .withEventId(0L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();
        final Event savedEvent = EventBuilder.fromEvent(eventToCreate)
                .withEventId(1L)
                .build();
        final String location = "http://localhost:8080/sportchef/api/events/1";
        final URI uri = new URI(location);

        expect(eventServiceMock.create(eventToCreate)).andStubReturn(savedEvent);
        expect(uriInfoMock.getAbsolutePathBuilder()).andStubReturn(uriBuilderMock);
        expect(uriBuilderMock.path(anyString())).andStubReturn(uriBuilderMock);
        expect(uriBuilderMock.build()).andStubReturn(uri);
        mockProvider.replayAll();

        // act
        final Response response = eventsResource.save(eventToCreate, uriInfoMock);

        //assert
        assertThat(response.getStatus(), is(CREATED.getStatusCode()));
        assertThat(response.getHeaderString("Location"), is(location));
        mockProvider.verifyAll();
    }

    @Test
    public void findAll() {
        // arrange
        final Event event1 = EventBuilder.anEvent()
                .withEventId(1L)
                .withTitle("Testevent")
                .withLocation("Testlocation")
                .withDate(LocalDate.of(2099, Month.DECEMBER, 31))
                .withTime(LocalTime.of(22, 0))
                .build();
        final Event event2 = EventBuilder.fromEvent(event1)
                .withEventId(2L)
                .build();
        final List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        expect(eventServiceMock.findAll()).andStubReturn(events);
        mockProvider.replayAll();

        // act
        final Response response = eventsResource.findAll();
        final List<Event> list = (List<Event>) response.getEntity();
        final Event responseEvent1 = list.get(0);
        final Event responseEvent2 = list.get(1);

        // assert
        assertThat(response.getStatus(), is(OK.getStatusCode()));
        assertThat(responseEvent1, is(event1));
        assertThat(responseEvent2, is(event2));
        mockProvider.verifyAll();
    }

    @Test
    public void find() {
        // arrange
        final long eventId = 1L;

        // act
        final EventResource eventResource = eventsResource.find(eventId);

        // assert
        assertThat(eventsResource, notNullValue());
    }
}
