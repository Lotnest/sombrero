package dev.lotnest.sombrero.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface Command {

    void execute(@NotNull SlashCommandEvent event);

    String getName();

    String getDescription();

    String getUsage();

    CommandData getCommandData();
}
