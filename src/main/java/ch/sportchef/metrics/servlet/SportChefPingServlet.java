package ch.sportchef.metrics.servlet;

import com.codahale.metrics.servlets.PingServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "PingServlet" , urlPatterns = { "/metrics/ping" })
public class SportChefPingServlet extends PingServlet {
}
