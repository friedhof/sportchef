package ch.sportchef.metrics.contextlistener;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

import javax.inject.Inject;
import javax.servlet.annotation.WebListener;

@WebListener
public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

    @Inject
    private HealthCheckRegistry healthCheckRegistry;

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

}
