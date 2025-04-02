package com.example.auto_ria.setup;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.services.user.UsersServiceSQL;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StartupValidator {

    private final UsersServiceSQL usersServiceMySQL;
    private final SystemStateService systemStateService;
    private final Environment environment;
    private final ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void checkInitialSetup() {
        boolean isProd = environment.acceptsProfiles(Profiles.of("prod"));

        if (isProd) {
            boolean adminExists = usersServiceMySQL.countByRole(ERole.ADMIN) > 0;
            boolean managerExists = usersServiceMySQL.countByRole(ERole.MANAGER) > 0;

            if (adminExists && managerExists) {
                systemStateService.setSystemReady(true);
                System.out.println("System is fully set up. API is unlocked.");
            } else {
                systemStateService.setSystemReady(false);
                System.err.println("SYSTEM SETUP INCOMPLETE! APP WILL SHUT DOWN.");
                System.err.println("You must create at least ONE ADMIN and ONE MANAGER before running in production.");

                shutdownApplication();
            }
        } else {
            systemStateService.setSystemReady(true);
            System.out.println("Running in Dev/Staging mode. API is fully open.");
        }
    }

    private void shutdownApplication() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }

            System.err.println("Shutting down application...");
            applicationContext.publishEvent(new ContextClosedEvent(applicationContext));
            System.exit(1);
        }).start();
    }
}
