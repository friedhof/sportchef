package ch.sportchef.metrics;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.lang.management.ManagementFactory;
import java.util.Map;

@Singleton
@Startup
public class MetricsJvmConfiguration {

    @Inject
    private MetricRegistry metricRegistry;

    @Inject
    private HealthCheckRegistry healthCheckRegistry;

    @PostConstruct
    private void registerMetrics() {
        registerAll("jvm.gc", new GarbageCollectorMetricSet(), metricRegistry);
        registerAll("jvm.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), metricRegistry);
        registerAll("jvm.memory", new MemoryUsageGaugeSet(), metricRegistry);
        registerAll("jvm.threads", new ThreadStatesGaugeSet(), metricRegistry);
        metricRegistry.register("jvm.fileDescriptorCountRatio", new FileDescriptorRatioGauge());
    }

    private void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry) {
        for (Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry);
            } else {
                registry.register(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
    }

}
