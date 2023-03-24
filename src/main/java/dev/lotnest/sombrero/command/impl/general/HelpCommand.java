package dev.lotnest.sombrero.command.impl.general;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.util.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements Command {

    private final CommandData commandData;

    @SneakyThrows
    public HelpCommand() {
        commandData = new CommandData(getName(), getDescription());
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {
        Utils.sendHelpMessage(event);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows the help for Sombrero Bot.";
    }

    @Override
    public String getUsage() {
        return Utils.getUsageFormatted(this);
    }

    @Override
    public CommandData getCommandData() {
        return commandData;
    }
}
