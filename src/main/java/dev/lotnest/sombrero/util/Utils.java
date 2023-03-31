package dev.lotnest.sombrero.util;

import dev.lotnest.sombrero.message.MessageSender;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
public record Utils(@NotNull MessageSender messageSender) {

    public @NotNull Optional<AudioManager> getAudioManager(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild != null) {
            return Optional.of(guild.getAudioManager());
        }
        return Optional.empty();
    }

    public boolean isMemberConnectedToVoiceChannel(@Nullable Member member) {
        if (member == null) {
            return false;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null) {
            return false;
        }

        return memberVoiceState.getChannel() != null;
    }

    public void summonBotToVoiceChannel(@NotNull SlashCommandInteractionEvent event) {
        summonBotToVoiceChannel(event, false);
    }

    public void summonBotToVoiceChannel(@NotNull SlashCommandInteractionEvent event, boolean silent) {
        getAudioManager(event).ifPresentOrElse(audioManager -> {
            try {
                Member member = requireNonNull(event.getMember());
                GuildVoiceState memberVoiceState = member.getVoiceState();

                if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
                    messageSender.sendMemberNotConnectedToVoiceChannelMessage(event);
                    return;
                }

                VoiceChannel memberVoiceChannel = requireNonNull(memberVoiceState.getChannel()).asVoiceChannel();
                audioManager.setSelfDeafened(true);
                audioManager.openAudioConnection(memberVoiceChannel);

                if (!silent) {
                    messageSender.sendVoiceChannelJoinSuccessMessage(event, requireNonNull(memberVoiceChannel).getName());
                }
            } catch (Exception exception) {
                messageSender.sendErrorOccurredMessage(event, exception);
            }
        }, () -> messageSender.sendErrorOccurredMessage(event));
    }

    public boolean isBotConnectedToVoiceChannel(@NotNull Member botMember) {
        return botMember.getVoiceState() != null && botMember.getVoiceState().getChannel() != null;
    }

    public boolean isMemberConnectedToSameVoiceChannelAsBot(@NotNull Member member, @NotNull Member botMember) {
        return isBotConnectedToVoiceChannel(botMember) && isMemberConnectedToVoiceChannel(member) && member.getVoiceState().getChannel().equals(botMember.getVoiceState().getChannel());
    }

    public void disconnectBotFromVoiceChannel(@NotNull SlashCommandInteractionEvent event) {
        getAudioManager(event).ifPresentOrElse(audioManager -> {
            try {
                audioManager.closeAudioConnection();
                messageSender.sendVoiceChannelDisconnectSuccessMessage(event, requireNonNull(audioManager.getConnectedChannel()).getName());
            } catch (Exception exception) {
                messageSender.sendErrorOccurredMessage(event, exception);
            }
        }, () -> messageSender.sendErrorOccurredMessage(event));
    }

//    public void restart() {
//        log.info("Received restart action, restarting in 5 minutes");
//
//        getJDA().ifPresentOrElse(jda -> {
//            sendRestartingInFiveMinutesMessage(jda);
//
//            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> sendRestartingInOneMinuteMessage(jda), 4, TimeUnit.MINUTES);
//            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
//                jda.shutdown();
//                System.exit(0);
//            }, 5, TimeUnit.MINUTES);
//        }, () -> System.exit(0));
//    }
}
