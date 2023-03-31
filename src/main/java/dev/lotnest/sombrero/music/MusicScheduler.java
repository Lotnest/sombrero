package dev.lotnest.sombrero.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.lotnest.sombrero.message.MessageSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Queue;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class MusicScheduler extends AudioEventAdapter {

    private final MusicManager musicManager;
    private final AudioPlayer audioPlayer;
    private final Queue<Song> songQueue;
    private final MessageSender messageSender;
    private boolean isMusicLoop;

    public void queueSong(@NotNull Song song) {
        SlashCommandInteractionEvent event = song.event();
        Guild guild = event.getGuild();

        if (guild != null) {
            AudioTrack nextTrack = song.audioTrack();
            if (!audioPlayer.startTrack(nextTrack, true)) {
                if (songQueue.offer(song)) {
                    messageSender.sendAddedToQueueMessage(event, nextTrack.getInfo().title, nextTrack.getInfo().uri, messageSender.getThumbnailURL(nextTrack.getIdentifier()),
                            musicManager.getGuildMusicManager(guild).musicScheduler().getSongQueue().size());
                } else {
                    messageSender.sendErrorOccurredMessage(event, true);
                }
            } else {
                notifyNextSong(song);
            }
        } else {
            messageSender.sendErrorOccurredMessage(event, true);
        }
    }

    public void playNextSong() {
        playNextSong(null);
    }

    public void playNextSong(@Nullable SlashCommandInteractionEvent eventEndingSong) {
        Song nextSong = songQueue.poll();
        if (nextSong != null) {
            try {
                notifyNextSong(nextSong, eventEndingSong);
                audioPlayer.startTrack(nextSong.audioTrack(), false);
            } catch (Exception exception) {
                exception.printStackTrace();
                messageSender.sendErrorOccurredMessage(nextSong.event(), true);
            }
        }
    }

    public void notifyNextSong(@NotNull Song nextSong) {
        notifyNextSong(nextSong, nextSong.event());
    }

    public void notifyNextSong(@NotNull Song nextSong, @Nullable SlashCommandInteractionEvent eventEndingSong) {
        if (eventEndingSong == null) {
            eventEndingSong = nextSong.event();
        }

        AudioTrack nextTrack = nextSong.audioTrack();
        Guild guild = eventEndingSong.getGuild();

        if (guild != null) {
            if (!songQueue.isEmpty()) {
                messageSender.sendAddedToQueueMessage(eventEndingSong, nextTrack.getInfo().title, nextTrack.getInfo().uri,
                        messageSender.getThumbnailURL(nextTrack.getIdentifier()),
                        musicManager.getGuildMusicManager(guild).musicScheduler()
                                .getSongQueue()
                                .size());
            } else {
                messageSender.sendNowPlayingMessage(eventEndingSong, nextSong.audioTrack().getInfo());
            }
        }
    }

    public void skipCurrentSong(@NotNull SlashCommandInteractionEvent event) {
        if (audioPlayer.getPlayingTrack() == null) {
            messageSender.sendNoSongsInTheQueueMessage(event);
            return;
        }

        if (songQueue.isEmpty()) {
            audioPlayer.stopTrack();
            messageSender.sendQueueHasEndedMessage(event);
            return;
        }

        playNextSong(event);
    }

    @Override
    public void onTrackEnd(@NotNull AudioPlayer audioPlayer, @NotNull AudioTrack audioTrack,
                           @NotNull AudioTrackEndReason audioTrackEndReason) {
        if (audioTrackEndReason.mayStartNext) {
            if (isMusicLoop) {
                audioPlayer.startTrack(audioTrack.makeClone(), false);
                return;
            }

            playNextSong();
        }
    }
}
