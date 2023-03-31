package dev.lotnest.sombrero.command;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class CommandManager {

    private final List<Command> commands;

    public CommandManager(@NotNull List<Command> commands) {
        this.commands = commands;
    }

    public @NotNull List<Command> getEnabledCommands() {
        return commands.stream()
                .filter(Command::isEnabled)
                .toList();
    }

    public @NotNull List<Command> getDisabledCommands() {
        return commands.stream()
                .filter(command -> !command.isEnabled())
                .toList();
    }
}
