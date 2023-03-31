package dev.lotnest.sombrero.maintenance;

import dev.lotnest.sombrero.crud.BaseCrudRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository extends BaseCrudRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findAllByIsMaintenance(boolean isMaintenance);

    List<MaintenanceRecord> findAllByReasonContainingIgnoreCase(@NotNull String reason);

    List<MaintenanceRecord> findAllByCreatedAt(@NotNull LocalDateTime createdAt);

    List<MaintenanceRecord> findAllByClosedAt(@Nullable LocalDateTime closedAt);

    List<MaintenanceRecord> findAllByCreatedAtBetween(@NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate);

    List<MaintenanceRecord> findAllByClosedAtBetween(@NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate);

    Optional<MaintenanceRecord> findFirstByOrderByCreatedAtDesc();

    Optional<MaintenanceRecord> findFirstByOrderByClosedAtDesc();

    default Optional<MaintenanceRecord> findLastMaintenanceRecord() {
        return findFirstByOrderByClosedAtDesc().or(this::findFirstByOrderByCreatedAtDesc);
    }
}
