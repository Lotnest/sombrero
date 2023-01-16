package dev.lotnest.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import dev.lotnest.util.Utils;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class MusicScheduler extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Queue<Song> songQueue;
    private boolean isMusicLoop;

    public MusicScheduler(@NotNull AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        songQueue = new LinkedBlockingQueue<>();
    }

    public void queueSong(@NotNull Song song) {
        Guild guild = song.getEvent().getGuild();
        if (guild != null) {
            if (!audioPlayer.startTrack(song.getAudioTrack(), true)) {
                if (songQueue.offer(song)) {
                    AudioTrack nextTrack = song.getAudioTrack();
                    Utils.sendAddedToQueueMessage(song.getEvent(), nextTrack.getInfo().title, nextTrack.getInfo().uri, Utils.getThumbnailURL(nextTrack.getIdentifier()),
                            MusicManager.getInstance().getGuildMusicManager(guild).getMusicScheduler().getSongQueue().size());
                } else {
                    Utils.sendErrorOccurredMessage(song.getEvent(), true);
                }
            } else {
                notifyNextSong(song);
            }
        } else {
            Utils.sendErrorOccurredMessage(song.getEvent(), true);
        }
    }

    public void playNextSong() {
        playNextSong(null);
    }

    public void playNextSong(@Nullable SlashCommandEvent eventEndingSong) {
        Song nextSong = songQueue.poll();
        if (nextSong != null) {
            notifyNextSong(nextSong, eventEndingSong);
            audioPlayer.startTrack(nextSong.getAudioTrack(), false);
        }
    }

    public void notifyNextSong(@NotNull Song nextSong) {
        notifyNextSong(nextSong, nextSong.getEvent());
    }

    public void notifyNextSong(@NotNull Song nextSong, @Nullable SlashCommandEvent eventEndingSong) {
        if (eventEndingSong == null) {
            eventEndingSong = nextSong.getEvent();
        }

        AudioTrack nextTrack = nextSong.getAudioTrack();
        Guild guild = eventEndingSong.getGuild();

        if (guild != null) {
            if (!songQueue.isEmpty()) {
                Utils.sendAddedToQueueMessage(eventEndingSong, nextTrack.getInfo().title, nextTrack.getInfo().uri,
                        Utils.getThumbnailURL(nextTrack.getIdentifier()),
                        MusicManager.getInstance().getGuildMusicManager(guild).getMusicScheduler()
                                .getSongQueue()
                                .size());
            } else {
                Utils.sendNowPlayingMessage(eventEndingSong, nextSong.getAudioTrack().getInfo());
            }
        }
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

    public boolean isMusicLoop() {
        return isMusicLoop;
    }

    public void setMusicLoop(boolean musicLoop) {
        this.isMusicLoop = musicLoop;
    }
}
