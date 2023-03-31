package dev.lotnest.sombrero.command.impl.general;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.message.MessageSender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand extends Command {

    public HelpCommand(@NotNull MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        messageSender.sendHelpMessage(event);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows the help for Sombrero.";
    }
}
