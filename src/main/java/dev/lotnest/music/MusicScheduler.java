package dev.lotnest.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class MusicScheduler extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final BlockingQueue<AudioTrack> audioTrackQueue;
    public boolean musicLoop = false;

    public MusicScheduler(@NotNull AudioPlayer player) {
        audioPlayer = player;
        audioTrackQueue = new LinkedBlockingQueue<>();
    }

    public void queueAudioTrack(@NotNull AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) {
            audioTrackQueue.offer(audioTrack);
        }
    }

    public void playNextTrack() {
        audioPlayer.startTrack(audioTrackQueue.poll(), false);
    }

    @Override
    public void onTrackEnd(@NotNull AudioPlayer audioPlayer, @NotNull AudioTrack audioTrack, @NotNull AudioTrackEndReason audioTrackEndReason) {
        if (audioTrackEndReason.mayStartNext) {
            if (musicLoop) {
                audioPlayer.startTrack(audioTrack.makeClone(), false);
                return;
            }
            playNextTrack();
        }
    }
}
