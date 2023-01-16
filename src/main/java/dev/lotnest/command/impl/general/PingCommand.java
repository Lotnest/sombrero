package dev.lotnest.command.impl.general;

import dev.lotnest.command.Command;
import dev.lotnest.util.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements Command {

    private final CommandData commandData;

    @SneakyThrows
    public PingCommand() {
        commandData = new CommandData(getName(), getDescription());
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {
        if (event.getChannelType().equals(ChannelType.TEXT)) {
            Guild guild = event.getGuild();
            if (guild != null) {
                Member botMember = guild.getMember(event.getJDA().getSelfUser());
                if (botMember == null) {
                    return;
                }

                Utils.sendPingMessage(event);
            }
        }
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Shows the latency for the Bot and Discord's API.";
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
