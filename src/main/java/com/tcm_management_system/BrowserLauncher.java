package com.tcm_management_system;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
public class BrowserLauncher implements ApplicationListener<ApplicationReadyEvent> {

    private final Environment environment;

    public BrowserLauncher(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Spring Boot sets this automatically once the embedded server is
        // actually listening — reading it instead of hardcoding "8080"
        // means this keeps working correctly even if the port ever changes.
        String port = environment.getProperty("local.server.port", "8080");
        String url = "http://localhost:" + port;

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Could not auto-launch browser. Please open manually: " + url);
            }
        } catch (Exception e) {
            // Never let a browser-launch failure crash the app — the server
            // is already running and usable even if this step fails.
            System.err.println("Failed to launch browser automatically: " + e.getMessage());
            System.out.println("Please open manually: " + url);
        }
    }
}