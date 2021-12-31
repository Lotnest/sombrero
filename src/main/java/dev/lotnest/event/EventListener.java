package dev.lotnest.event;

import dev.lotnest.Sombrero;
import dev.lotnest.command.CommandManager;
import dev.lotnest.util.Utils;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class EventListener extends ListenerAdapter {

    private final CommandManager commandManager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Sombrero.LOGGER.info("Bot has started successfully with {} commands.", commandManager.getCommands().size());

        commandManager.getCommands().forEach(command -> {
            CommandData commandData = new CommandData(command.getName(), command.getDescription());
            commandData.addOption(OptionType.STRING, "query", Utils.QUERY_INFORMATION, true);
            event.getJDA().upsertCommand(commandData).queue();
        });
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        commandManager.getCommands().stream()
                .filter(command -> command.getName().equals(event.getName()))
                .forEach(command -> command.execute(event));
    }
}
