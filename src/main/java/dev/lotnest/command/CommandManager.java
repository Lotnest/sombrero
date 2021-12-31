package dev.lotnest.command;

import dev.lotnest.command.impl.PlayCommand;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommandManager {

    private final Set<ICommand> commands = new HashSet<>();

    public CommandManager() {
        addCommand(new PlayCommand());
    }

    private void addCommand(@NotNull ICommand command) {
        boolean commandFound = commands.stream()
                .anyMatch(matchedCommand -> matchedCommand.getName().equals(command.getName()));

        if (commandFound) {
            throw new IllegalArgumentException(String.format("Command named '%s' already exists", command.getName()));
        }

        commands.add(command);
    }
}
