package dev.lotnest.sombrero.command.impl.music;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.util.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class DisconnectCommand extends Command {

    private final Utils utils;

    public DisconnectCommand(@NotNull Utils utils) {
        super(utils.messageSender());
        this.utils = utils;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            Guild guild = event.getGuild();
            if (guild != null) {
                Member botMember = guild.getMember(event.getJDA().getSelfUser());
                if (botMember == null) {
                    return;
                }

                Member member = event.getMember();
                if (member == null) {
                    return;
                }

                if (!utils.isMemberConnectedToVoiceChannel(member)) {
                    messageSender.sendMemberNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                if (!utils.isBotConnectedToVoiceChannel(botMember)) {
                    messageSender.sendBotNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                if (!utils.isMemberConnectedToSameVoiceChannelAsBot(member, botMember)) {
                    messageSender.sendMemberNotConnectedToSameVoiceChannelAsBotMessage(event);
                    return;
                }

                utils.disconnectBotFromVoiceChannel(event);
            }
        }
    }

    @Override
    public String getName() {
        return "disconnect";
    }

    @Override
    public String getDescription() {
        return "Disconnects the bot from your voice channel.";
    }
}
