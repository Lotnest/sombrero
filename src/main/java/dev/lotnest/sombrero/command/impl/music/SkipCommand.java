package dev.lotnest.sombrero.command.impl.music;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.music.MusicManager;
import dev.lotnest.sombrero.music.MusicScheduler;
import dev.lotnest.sombrero.util.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

@Getter
public class SkipCommand implements Command {

    private final CommandData commandData;

    @SneakyThrows
    public SkipCommand() {
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
                    Utils.sendNoSongsInTheQueueMessage(event);
                    return;
                }

                if (Utils.getSongQueue(guild).isEmpty()) {
                    musicScheduler.getAudioPlayer().stopTrack();
                    Utils.sendQueueHasEndedMessage(event);
                    return;
                }

                musicScheduler.playNextSong(event);
            }
        }
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips a track to the next queued one.";
    }

    @Override
    public String getUsage() {
        return Utils.getUsageFormatted(this);
    }
}
