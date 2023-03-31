package dev.lotnest.sombrero.command.impl.music;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.util.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SummonCommand extends Command {

    private final Utils utils;

    public SummonCommand(@NotNull Utils utils) {
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

                if (!botMember.hasPermission(Permission.VOICE_CONNECT)) {
                    messageSender.sendNoPermissionMessage(Permission.VOICE_CONNECT, event);
                    return;
                }

                if (!utils.isMemberConnectedToVoiceChannel(member)) {
                    messageSender.sendMemberNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                utils.summonBotToVoiceChannel(event);
            }
        }
    }

    @Override
    public String getName() {
        return "summon";
    }

    @Override
    public String getDescription() {
        return "Summons the bot to your voice channel.";
    }
}
