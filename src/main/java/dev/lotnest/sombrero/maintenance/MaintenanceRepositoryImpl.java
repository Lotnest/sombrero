package dev.lotnest.sombrero.maintenance;

import dev.lotnest.sombrero.crud.BaseCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository("maintenanceRepository")
public interface MaintenanceRepositoryImpl extends BaseCrudRepositoryImpl<MaintenanceRecord, Long>, MaintenanceRepository {
}
