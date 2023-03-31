package dev.lotnest.sombrero.music;

import com.google.common.collect.Queues;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lotnest.sombrero.message.MessageSender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;

@Component
@Getter
@Slf4j
public class MusicManager {

    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final MessageSender messageSender;

    public MusicManager(@NotNull AudioPlayerManager audioPlayerManager, @NotNull Map<Long, GuildMusicManager> musicManagers,
                        @NotNull MessageSender messageSender) {
        this.audioPlayerManager = audioPlayerManager;
        this.musicManagers = musicManagers;
        this.messageSender = messageSender;

        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public GuildMusicManager getGuildMusicManager(@NotNull Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
            Queue<Song> songQueue = Queues.newLinkedBlockingQueue();
            MusicScheduler musicScheduler = new MusicScheduler(this, audioPlayer, songQueue, messageSender);
            MusicHandler musicHandler = new MusicHandler(audioPlayer);
            GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayer, musicScheduler, musicHandler);

            guild.getAudioManager().setSendingHandler(guildMusicManager.musicHandler());
            return guildMusicManager;
        });
    }

    public void play(@NotNull SlashCommandInteractionEvent event, @NotNull String audioTrackURL) {
        Guild guild = event.getGuild();
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            audioPlayerManager.loadItemOrdered(musicManager, audioTrackURL, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(@NotNull AudioTrack audioTrack) {
                    musicManager.musicScheduler().queueSong(new Song(audioTrack, event));
                    log.info("({}) ({}) Track loaded: {}", event.getGuild().getId(), audioTrack.getInfo().identifier,
                            audioTrack.getInfo().title);
                }

                @Override
                public void playlistLoaded(@NotNull AudioPlaylist audioPlaylist) {
                    if (audioPlaylist.isSearchResult()) {
                        AudioTrack audioTrack = audioPlaylist.getTracks().get(0);
                        musicManager.musicScheduler().queueSong(new Song(audioTrack, event));
                    } else {
                        audioPlaylist.getTracks().forEach(audioTrack -> musicManager.musicScheduler().queueSong(new Song(audioTrack, event)));
                    }

                    log.info("({}) Playlist loaded: {}", event.getGuild().getId(), audioPlaylist.getName());
                }

                @Override
                public void noMatches() {
                    messageSender.sendNoResultsFoundMessage(event);
                }

                @Override
                public void loadFailed(@NotNull FriendlyException exception) {
                    exception.printStackTrace();
                    messageSender.sendLoadFailedMessage(event);
                }
            });
        }
    }
}
