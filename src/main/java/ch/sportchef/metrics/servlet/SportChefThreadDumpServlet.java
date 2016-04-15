package ch.sportchef.metrics.servlet;

import com.codahale.metrics.servlets.ThreadDumpServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "ThreadDumpServlet" , urlPatterns = { "/metrics/threads" })
public class SportChefThreadDumpServlet extends ThreadDumpServlet {
}
