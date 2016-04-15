package ch.sportchef.metrics.servlet;

import com.codahale.metrics.servlets.AdminServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "AdminServlet" , urlPatterns = { "/metrics" })
public class SportChefAdminServlet extends AdminServlet {
}
