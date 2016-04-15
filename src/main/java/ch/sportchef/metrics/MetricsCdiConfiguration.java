package ch.sportchef.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.astefanutti.metrics.cdi.MetricsConfiguration;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

public class MetricsCdiConfiguration {

    @Produces
    @ApplicationScoped
    private final MetricRegistry metricRegistry = new MetricRegistry();

    @Produces
    @ApplicationScoped
    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

    static void configure(@Observes final MetricsConfiguration metrics) {
        metrics.useAbsoluteName(true);
    }

}
