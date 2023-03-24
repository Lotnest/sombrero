package dev.lotnest.sombrero.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class Song {

    @NotNull
    private final AudioTrack audioTrack;
    private final SlashCommandEvent event;
}
