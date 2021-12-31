package dev.lotnest.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class MusicHandler implements AudioSendHandler {

    @NotNull
    public final AudioPlayer audioPlayer;
    private AudioFrame lastAudioFrame;

    public MusicHandler(@NotNull AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        audioPlayer.setVolume(80);
    }

    @Override
    public boolean canProvide() {
        lastAudioFrame = audioPlayer.provide();
        return lastAudioFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastAudioFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
