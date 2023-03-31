package dev.lotnest.sombrero.command.impl.general;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.message.MessageSender;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PingCommand extends Command {

    public PingCommand(@NotNull MessageSender messageSender) {
        super(messageSender);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            Guild guild = event.getGuild();
            if (guild != null) {
                messageSender.sendPingMessage(event);
            }
        }
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Shows the latency for Sombrero and Discord's API.";
    }
}
