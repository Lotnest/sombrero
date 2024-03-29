package dev.lotnest.sombrero.command.impl.music;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.music.MusicManager;
import dev.lotnest.sombrero.util.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SkipCommand extends Command {

    private final Utils utils;
    private final MusicManager musicManager;

    public SkipCommand(@NotNull Utils utils, MusicManager musicManager) {
        super(utils.messageSender());
        this.utils = utils;
        this.musicManager = musicManager;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            Guild guild = event.getGuild();
            if (guild != null) {
                Member botMember = guild.getMember(event.getJDA().getSelfUser());
                if (botMember == null) {
                    return;
                }

                if (!utils.isBotConnectedToVoiceChannel(botMember)) {
                    messageSender.sendBotNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                musicManager.getGuildMusicManager(guild)
                        .musicScheduler()
                        .skipCurrentSong(event);
            }
        }
    }

    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips a track to the next queued one.";
    }
}
