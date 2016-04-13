package ch.sportchef.metrics.servlet;

import com.codahale.metrics.servlets.MetricsServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "MetricsServlet" , urlPatterns = { "/metrics/metrics" })
public class SportChefMetricsServlet extends MetricsServlet {
}
