package dev.lotnest.command.impl.music;

import dev.lotnest.command.Command;
import dev.lotnest.util.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

@Getter
public class SummonCommand implements Command {

    private final CommandData commandData;

    public SummonCommand() {
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

                Member member = event.getMember();
                if (member == null) {
                    return;
                }

                if (!botMember.hasPermission(Permission.VOICE_CONNECT)) {
                    Utils.sendNoPermissionMessage(Permission.VOICE_CONNECT, event);
                    return;
                }

                if (!Utils.isMemberConnectedToVoiceChannel(event)) {
                    Utils.sendMemberNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                Utils.summonBotToVoiceChannel(event);
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

    @Override
    public String getUsage() {
        return Utils.getUsageFormatted(this);
    }
}
