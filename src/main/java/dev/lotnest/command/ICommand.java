package dev.lotnest.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public interface ICommand {

    void execute(@NotNull SlashCommandEvent event);

    String getName();

    String getDescription();

    String getUsage();
}
