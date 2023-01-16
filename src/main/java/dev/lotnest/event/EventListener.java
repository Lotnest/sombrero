package dev.lotnest.event;

import dev.lotnest.command.Command;
import dev.lotnest.command.CommandManager;
import dev.lotnest.music.MusicManager;
import dev.lotnest.util.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class EventListener extends ListenerAdapter {

    private static JDA jda;

    private final CommandManager commandManager = new CommandManager();

    public static @NotNull Optional<JDA> getJda() {
        return Optional.ofNullable(jda);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        log.info("Bot started successfully with {} commands.", commandManager.getCommands().size());

        jda = event.getJDA();
        jda.updateCommands()
                .addCommands(commandManager.getCommands().stream()
                        .map(Command::getCommandData)
                        .collect(Collectors.toSet()))
                .queue();
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        commandManager.getCommands().stream()
                .filter(command -> command.getName().equals(event.getName()))
                .forEach(command -> {
                    try {
                        command.execute(event);
                    } catch (Exception exception) {
                        Utils.sendErrorOccurredMessage(event, exception);
                    }
                });
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        MusicManager.getInstance().getMusicManagers()
                .values()
                .forEach(guildMusicManager -> {
                    guildMusicManager.getAudioPlayer()
                            .destroy();
                    guildMusicManager.getMusicScheduler()
                            .getSongQueue()
                            .clear();
                });
    }
}
