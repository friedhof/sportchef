package ch.sportchef.metrics.servlet;

import com.codahale.metrics.servlets.HealthCheckServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "HealthCheckServlet" , urlPatterns = { "/metrics/healthcheck" })
public class SportChefHealthCheckServlet extends HealthCheckServlet {
}
