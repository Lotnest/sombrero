package dev.lotnest.sombrero.maintenance;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface MaintenanceService {

    void setMaintenance(boolean isMaintenance, @Nullable String reason);

    boolean isMaintenance();

    Optional<MaintenanceRecord> findLastMaintenanceRecord();
}
