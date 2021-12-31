package dev.lotnest.event;

import dev.lotnest.Sombrero;
import dev.lotnest.command.CommandManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

    private final CommandManager commandManager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Sombrero.LOGGER.info("Bot has started successfully with {} commands.", commandManager.getCommands().size());

        commandManager.getCommands().forEach(command -> event.getJDA().upsertCommand(command.getCommandData()).queue());
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        commandManager.getCommands().stream()
                .filter(command -> command.getName().equals(event.getName()))
                .forEach(command -> command.execute(event));
    }
}
