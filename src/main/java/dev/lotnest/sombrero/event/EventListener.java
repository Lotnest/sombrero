package dev.lotnest.sombrero.event;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.command.CommandManager;
import dev.lotnest.sombrero.maintenance.MaintenanceService;
import dev.lotnest.sombrero.maintenance.MaintenanceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener extends ListenerAdapter {

    private final ApplicationContext applicationContext;
    private CommandManager commandManager;
    private MaintenanceService maintenanceService;
    private ApplicationInfo applicationInfo;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (commandManager == null) {
            commandManager = applicationContext.getBean(CommandManager.class);
        }

        if (maintenanceService == null) {
            maintenanceService = applicationContext.getBean(MaintenanceServiceImpl.class);
        }

        if (applicationInfo == null) {
            applicationInfo = applicationContext.getBean(ApplicationInfo.class);
        }

        log.info("Registering commands: {}", commandManager.getCommands().stream()
                .map(Command::getName)
                .map(commandName -> "/" + commandName)
                .collect(Collectors.joining(", ")));

        event.getJDA().updateCommands()
                .addCommands(commandManager.getCommands().stream()
                        .filter(Objects::nonNull)
                        .map(Command::getData)
                        .collect(Collectors.toSet()))
                .queue();

        log.info("Sombrero started successfully with {} commands.", commandManager.getCommands().size());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (maintenanceService.isMaintenance() && isBotOwner(event)) {
            event.deferReply(true)
                    .addContent("Sombrero is currently under maintenance, please check back later.")
                    .queue();
            return;
        }

        commandManager.getCommands().stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getName()))
                .filter(command -> {
                    if (command.isEnabled()) {
                        return true;
                    } else {
                        if (isBotOwner(event)) {
                            return true;
                        }

                        event.deferReply(true)
                                .addContent("This command is currently disabled, please check back later.")
                                .queue();
                        return false;
                    }
                })
                .forEach(command -> command.executeAsyncWithExceptionHandler(event));
    }

    private boolean isBotOwner(@NotNull SlashCommandInteractionEvent event) {
        return event.getMember() != null && event.getMember().getId().equals(applicationInfo.getOwner().getId());
    }
}
