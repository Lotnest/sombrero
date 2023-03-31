package dev.lotnest.sombrero.command.impl.music;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.music.MusicManager;
import dev.lotnest.sombrero.music.MusicScheduler;
import dev.lotnest.sombrero.util.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class NowPlayingCommand extends Command {

    private final Utils utils;
    private final MusicManager musicManager;

    public NowPlayingCommand(@NotNull Utils utils, MusicManager musicManager) {
        super(utils.messageSender());
        this.utils = utils;
        this.musicManager = musicManager;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            Guild guild = event.getGuild();
            if (guild != null) {
                Member botMember = guild.getSelfMember();

                if (!utils.isBotConnectedToVoiceChannel(botMember)) {
                    messageSender.sendBotNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                MusicScheduler musicScheduler = musicManager.getGuildMusicManager(guild).musicScheduler();
                if (musicScheduler.getAudioPlayer().getPlayingTrack() == null) {
                    messageSender.sendNoSongPlayingMessage(event);
                    return;
                }

                messageSender.sendNowPlayingDetailedMessage(musicManager, event);
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
}
