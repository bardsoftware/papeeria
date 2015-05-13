package com.bardsoftware.papeeria.sufler;

import com.bardsoftware.papeeria.sufler.api.SuflerServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class JettyLauncher {
    public static void startServer() {
        try {
            Server server = new Server();
            ServerConnector c = new ServerConnector(server);
            c.setIdleTimeout(1000);
            c.setAcceptQueueSize(10);
            c.setPort(8080);
            c.setHost("localhost");
            ServletContextHandler handler = new ServletContextHandler(server, "", true, false);
            ServletHolder servletHolder = new ServletHolder(SuflerServlet.class);
            handler.addServlet(servletHolder, "/sufler");
            server.addConnector(c);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startServer();
    }
}
