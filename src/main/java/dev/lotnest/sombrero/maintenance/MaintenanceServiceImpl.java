package dev.lotnest.sombrero.maintenance;

import dev.lotnest.sombrero.util.ActivityProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("maintenanceService")
@RequiredArgsConstructor
@Getter
@ToString
@Slf4j
public class MaintenanceServiceImpl implements MaintenanceService {

    private final JDA jda;
    private final ActivityProvider activityProvider;
    private final MaintenanceRepository maintenanceRepository;
    private boolean isMaintenance;

    public void setMaintenance(boolean isMaintenance, @Nullable String reason) {
        this.isMaintenance = isMaintenance;

        jda.getPresence().setPresence(getOnlineStatusFromMaintenanceStatus(), getActivityFromMaintenanceStatus());

        findLastMaintenanceRecord().ifPresentOrElse(maintenanceRecord -> {
            if (maintenanceRecord.isMaintenance() != isMaintenance) {
                maintenanceRecord.setClosedAt(LocalDateTime.now());
                maintenanceRepository.save(maintenanceRecord);
            }
        }, () -> {
            MaintenanceRecord maintenanceRecord = MaintenanceRecord.builder()
                    .isMaintenance(isMaintenance)
                    .reason(reason)
                    .createdAt(LocalDateTime.now())
                    .build();
            maintenanceRepository.save(maintenanceRecord);
        });

        log.info("Maintenance mode is now {}.", isMaintenance ? "enabled" : "disabled");
    }

    @Override
    public Optional<MaintenanceRecord> findLastMaintenanceRecord() {
        return maintenanceRepository.findLastMaintenanceRecord();
    }

    private @NotNull OnlineStatus getOnlineStatusFromMaintenanceStatus() {
        return isMaintenance ? OnlineStatus.DO_NOT_DISTURB : OnlineStatus.ONLINE;
    }

    private @NotNull Activity getActivityFromMaintenanceStatus() {
        return isMaintenance ? activityProvider.getOngoingDevelopmentActivity() : activityProvider.getMainActivity();
    }
}
