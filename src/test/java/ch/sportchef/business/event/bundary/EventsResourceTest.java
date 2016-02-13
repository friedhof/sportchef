package ch.sportchef.business.event.bundary;

import ch.sportchef.business.event.boundary.EventService;
import ch.sportchef.business.event.boundary.EventsResource;
import ch.sportchef.business.event.entity.Event;
import ch.sportchef.test.UnitTests;
import de.akquinet.jbosscc.needle.annotation.ObjectUnderTest;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

import static javax.ws.rs.core.Response.Status.CREATED;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
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

}
