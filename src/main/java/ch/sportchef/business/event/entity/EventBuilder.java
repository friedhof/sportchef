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
package ch.sportchef.business.event.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventBuilder {
    private Long eventId;
    private String title;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String cssBackgroundColor = "#000000";
    private Long version;

    private EventBuilder() {
    }

    public static EventBuilder anEvent() {
        return new EventBuilder();
    }

    public static EventBuilder fromEvent(final Event event) {
        return anEvent()
                .withTitle(event.getTitle())
                .withLocation(event.getLocation())
                .withEventId(event.getEventId())
                .withDate(event.getDate())
                .withTime(event.getTime())
                .withCssBackgroundColor(event.getCssBackgroundColor())
                .withVersion(event.getVersion());
    }

    public EventBuilder withVersion(Long version) {
        this.version = version;
        return this;
    }


    public EventBuilder withEventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public EventBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public EventBuilder withDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public EventBuilder withTime(LocalTime time) {
        this.time = time;
        return this;
    }

    public EventBuilder withCssBackgroundColor(String cssBackgroundColor) {
        this.cssBackgroundColor = cssBackgroundColor;
        return this;
    }

    public EventBuilder but() {
        return anEvent().withEventId(eventId).withTitle(title).withLocation(location).withDate(date).withTime(time).withCssBackgroundColor(cssBackgroundColor);
    }

    public Event build() {
        return new Event(eventId, title, location, date, time, cssBackgroundColor);
    }
    public Event buildWithVersion() {
        return new Event(eventId, title, location, date, time, cssBackgroundColor,version);
    }
}
