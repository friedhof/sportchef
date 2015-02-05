package ch.sportchef.business.event.entity;

import ch.sportchef.business.adapter.LocalDateAdapter;
import ch.sportchef.business.adapter.LocalTimeAdapter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Event {

    @Id
    @GeneratedValue
    private long eventId;

    @Version
    private long version;

    @Size(min = 1)
    private String title;

    private String location;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate date;

    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime time;

    public Event() {
        super();
    }

    public Event(final String title, final String location, final LocalDate date, final LocalTime time) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(final long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull final String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(final LocalTime time) {
        this.time = time;
    }

}
