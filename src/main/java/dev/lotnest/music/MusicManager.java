package dev.lotnest.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lotnest.util.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MusicManager {

    private static MusicManager INSTANCE;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private MusicManager() {
        musicManagers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public static MusicManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MusicManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(@NotNull Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.audioPlayerSendHandler);
            return guildMusicManager;
        });
    }

    public void play(@NotNull SlashCommandEvent event, @NotNull String audioTrackURL) {
        Guild guild = event.getGuild();
        if (guild != null) {
            GuildMusicManager musicManager = getMusicManager(guild);
            audioPlayerManager.loadItemOrdered(musicManager, audioTrackURL, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(@NotNull AudioTrack audioTrack) {
                    musicManager.musicScheduler.queueAudioTrack(audioTrack);
                    Utils.sendAddedToQueueMessage(event, audioTrack.getInfo().title, audioTrack.getInfo().uri, Utils.getThumbnailURL(audioTrack.getIdentifier()), getMusicManager(event.getGuild()).musicScheduler.getAudioTrackQueue().size());
                }

                @Override
                public void playlistLoaded(@NotNull AudioPlaylist audioPlaylist) {

                }

                @Override
                public void noMatches() {
                    Utils.sendNoResultsMessage(Utils.PLAY_COMMAND_NAME_BOLD_UPPERCASE, event);
                }

                @Override
                public void loadFailed(@NotNull FriendlyException exception) {
                    exception.printStackTrace();
                    Utils.sendLoadFailedMessage(event);
                }
            });
        }
    }
}
