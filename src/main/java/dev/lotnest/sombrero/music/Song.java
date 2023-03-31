package dev.lotnest.sombrero.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public record Song(@NotNull AudioTrack audioTrack, @NotNull SlashCommandInteractionEvent event) {
}
