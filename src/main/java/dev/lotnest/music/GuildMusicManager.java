package dev.lotnest.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import org.jetbrains.annotations.NotNull;

public class GuildMusicManager {

    public final AudioPlayer audioPlayer;
    public final MusicScheduler musicScheduler;
    public final MusicHandler audioPlayerSendHandler;

    public GuildMusicManager(@NotNull AudioPlayerManager audioPlayerManager) {
        audioPlayer = audioPlayerManager.createPlayer();
        musicScheduler = new MusicScheduler(audioPlayer);
        audioPlayer.addListener(musicScheduler);
        audioPlayerSendHandler = new MusicHandler(audioPlayer);
    }
}
