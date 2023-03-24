package dev.lotnest.sombrero.command;

import com.google.api.client.util.Sets;
import com.theokanning.openai.service.OpenAiService;
import dev.lotnest.sombrero.command.impl.general.HelpCommand;
import dev.lotnest.sombrero.command.impl.general.PingCommand;
import dev.lotnest.sombrero.command.impl.gpt.GPTCommand;
import dev.lotnest.sombrero.command.impl.music.NowPlayingCommand;
import dev.lotnest.sombrero.command.impl.music.PlayCommand;
import dev.lotnest.sombrero.command.impl.music.SkipCommand;
import dev.lotnest.sombrero.command.impl.music.SummonCommand;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Getter
public class CommandManager {

    private final @NotNull OpenAiService openAiService;
    private final @NotNull Set<Command> commands;

    public CommandManager(@NotNull OpenAiService openAiService) {
        this.openAiService = openAiService;
        commands = Sets.newHashSet();

        // Music
        addCommand(new PlayCommand());
        addCommand(new SummonCommand());
        addCommand(new SkipCommand());
        addCommand(new NowPlayingCommand());

        // General
        addCommand(new HelpCommand());
        addCommand(new PingCommand());

        // GPT
        addCommand(new GPTCommand(openAiService));
    }

    private void addCommand(@NotNull Command command) {
        if (commands.stream()
                .anyMatch(matchedCommand -> matchedCommand.getName().equals(command.getName()))) {
            throw new IllegalArgumentException(String.format("Command named '%s' already exists", command.getName()));
        }

        commands.add(command);
    }
}
