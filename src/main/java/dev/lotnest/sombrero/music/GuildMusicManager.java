package dev.lotnest.sombrero.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class GuildMusicManager {

    private final AudioPlayer audioPlayer;
    private final MusicScheduler musicScheduler;
    private final MusicHandler musicHandler;

    public GuildMusicManager(@NotNull AudioPlayerManager audioPlayerManager) {
        audioPlayer = audioPlayerManager.createPlayer();
        musicScheduler = new MusicScheduler(audioPlayer);
        audioPlayer.addListener(musicScheduler);
        musicHandler = new MusicHandler(audioPlayer);
    }
}
