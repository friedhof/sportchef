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
        Event event = new Event(eventId, title, location, date, time, cssBackgroundColor);
        return event;
    }
    public Event buildWithVersion() {
        Event event = new Event(eventId, title, location, date, time, cssBackgroundColor,version);
        return event;
    }
}
