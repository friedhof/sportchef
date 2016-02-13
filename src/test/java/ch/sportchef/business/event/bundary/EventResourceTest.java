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
import ch.sportchef.business.event.boundary.EventService;
import ch.sportchef.business.event.entity.Event;
import ch.sportchef.test.UnitTests;
import de.akquinet.jbosscc.needle.junit.NeedleRule;
import de.akquinet.jbosscc.needle.mock.EasyMockProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Optional;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Category(UnitTests.class)
public class EventResourceTest {

    @Rule
    public NeedleRule needleRule = new NeedleRule();

    private EventResource eventResource;

    @Inject
    private EasyMockProvider mockProvider;

    @Inject
    private EventService eventServiceMock;

    @Inject
    private UriInfo uriInfoMock;

    @Inject
    private UriBuilder uriBuilderMock;

    @Before
    public void setup() {
        eventResource = new EventResource(1L, eventServiceMock);
    }

    @Test
    public void findWithSuccess() {
        // arrange
        final Event testEvent = new Event(1L, "Testevent", "Testlocation",
                LocalDate.of(2099, Month.DECEMBER, 31), LocalTime.of(22, 0));
        expect(eventServiceMock.findByEventId(anyObject())).andStubReturn(Optional.of(testEvent));
        mockProvider.replayAll();

        // act
        final Event event = eventResource.find();

        // assert
        assertThat(event, is(testEvent));
        mockProvider.verifyAll();
    }

}
