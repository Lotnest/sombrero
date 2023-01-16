package dev.lotnest.command;

import dev.lotnest.command.impl.general.HelpCommand;
import dev.lotnest.command.impl.general.PingCommand;
import dev.lotnest.command.impl.music.NowPlayingCommand;
import dev.lotnest.command.impl.music.PlayCommand;
import dev.lotnest.command.impl.music.SkipCommand;
import dev.lotnest.command.impl.music.SummonCommand;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommandManager {

    private final Set<Command> commands = new HashSet<>();

    public CommandManager() {
        // Music
        addCommand(new PlayCommand());
        addCommand(new SummonCommand());
        addCommand(new SkipCommand());
        addCommand(new NowPlayingCommand());

        // General
        addCommand(new HelpCommand());
        addCommand(new PingCommand());
    }

    private void addCommand(@NotNull Command command) {
        boolean commandFound = commands.stream()
                .anyMatch(matchedCommand -> matchedCommand.getName().equals(command.getName()));

        if (commandFound) {
            throw new IllegalArgumentException(String.format("Command named '%s' already exists", command.getName()));
        }

        commands.add(command);
    }
}
