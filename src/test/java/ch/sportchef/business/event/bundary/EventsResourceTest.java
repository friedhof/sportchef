package ch.sportchef.business.event.bundary;

import ch.sportchef.business.event.boundary.EventResource;
import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.boundary.EventsResource;
import ch.sportchef.business.event.entity.Event;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.junit.Rule;
import org.junit.Test;

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
        final Event eventToCreate = new Event(0L, "Testevent", "Testlocation",
                LocalDate.of(2099, Month.DECEMBER, 31), LocalTime.of(22, 0));
        final Event savedEvent = new Event(1L, "Testevent", "Testlocation",
                LocalDate.of(2099, Month.DECEMBER, 31), LocalTime.of(22, 0));
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
        final Event event1 = new Event(1L, "Testevent", "Testlocation",
                LocalDate.of(2099, Month.DECEMBER, 31), LocalTime.of(22, 0));
        final Event event2 = new Event(2L, "Testevent", "Testlocation",
                LocalDate.of(2099, Month.DECEMBER, 31), LocalTime.of(22, 0));
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
