package dev.lotnest.sombrero.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public record GuildMusicManager(@NotNull AudioPlayer audioPlayer, @NotNull MusicScheduler musicScheduler,
                                @NotNull MusicHandler musicHandler) {
}
