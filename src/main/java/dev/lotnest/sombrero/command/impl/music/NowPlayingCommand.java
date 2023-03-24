package dev.lotnest.sombrero.command.impl.music;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.music.MusicManager;
import dev.lotnest.sombrero.music.MusicScheduler;
import dev.lotnest.sombrero.util.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class NowPlayingCommand implements Command {

    private final CommandData commandData;

    @SneakyThrows
    public NowPlayingCommand() {
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

                if (!Utils.isBotConnectedToVoiceChannel(botMember)) {
                    Utils.sendBotNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                MusicScheduler musicScheduler = MusicManager.getInstance()
                        .getGuildMusicManager(guild)
                        .getMusicScheduler();
                if (musicScheduler.getAudioPlayer().getPlayingTrack() == null) {
                    Utils.sendNoSongPlayingMessage(event);
                    return;
                }

                Utils.sendNowPlayingDetailedMessage(event);
            }
        }
    }

    @Override
    public String getName() {
        return "now_playing";
    }

    @Override
    public String getDescription() {
        return "Shows the currently playing song.";
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
