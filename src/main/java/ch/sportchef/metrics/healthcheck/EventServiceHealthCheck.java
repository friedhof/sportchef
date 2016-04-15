package ch.sportchef.metrics.healthcheck;

import ch.sportchef.business.event.control.EventService;
import ch.sportchef.business.event.entity.Event;
import com.codahale.metrics.health.HealthCheck;

import javax.validation.constraints.NotNull;
import java.util.List;

public class EventServiceHealthCheck extends HealthCheck {

    private final EventService eventService;

    public EventServiceHealthCheck(@NotNull final EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    protected Result check() throws Exception {
        try {
            final List<Event> events = eventService.findAll();
            return events != null ? Result.healthy() : Result.unhealthy("can't access events");
        } catch (final Throwable error) {
            return Result.unhealthy(error.getMessage());
        }
    }

}
