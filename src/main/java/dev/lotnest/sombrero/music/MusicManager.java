package dev.lotnest.sombrero.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lotnest.sombrero.util.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
public class MusicManager {

    private static MusicManager instance;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private MusicManager() {
        musicManagers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public GuildMusicManager getGuildMusicManager(@NotNull Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getMusicHandler());
            return guildMusicManager;
        });
    }

    public void play(@NotNull SlashCommandEvent event, @NotNull String audioTrackURL) {
        Guild guild = event.getGuild();
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            audioPlayerManager.loadItemOrdered(musicManager, audioTrackURL, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(@NotNull AudioTrack audioTrack) {
                    log.info("({}) ({}) Track loaded: {}", event.getGuild().getId(), audioTrack.getInfo().identifier,
                            audioTrack.getInfo().title);
                    musicManager.getMusicScheduler().queueSong(new Song(audioTrack, event));
                }

                @Override
                public void playlistLoaded(@NotNull AudioPlaylist audioPlaylist) {
                    log.info("({}) Playlist loaded: {}", event.getGuild().getId(), audioPlaylist.getName());
                    if (audioPlaylist.isSearchResult()) {
                        AudioTrack audioTrack = audioPlaylist.getTracks().get(0);
                        musicManager.getMusicScheduler().queueSong(new Song(audioTrack, event));
                    } else {
                        audioPlaylist.getTracks().forEach(audioTrack -> musicManager.getMusicScheduler().queueSong(new Song(audioTrack, event)));
                    }
                }

                @Override
                public void noMatches() {
                    Utils.sendNoResultsMessage(Utils.PLAY_BOLD, event);
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
