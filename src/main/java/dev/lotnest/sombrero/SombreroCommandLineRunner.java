package dev.lotnest.sombrero;

import dev.lotnest.sombrero.maintenance.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SombreroCommandLineRunner implements CommandLineRunner {

    @NotNull
    private final MaintenanceService maintenanceService;

    @Override
    public void run(String... args) {
        boolean isMaintenance = false;

        for (String arg : args) {
            if (arg.toLowerCase().matches("^-maintenance=true$")) {
                isMaintenance = Boolean.parseBoolean(arg.split("=")[1]);
            }
        }

        maintenanceService.setMaintenance(isMaintenance, null);
    }
}
