package dev.lotnest.util;

import dev.lotnest.command.ICommand;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.rmi.UnexpectedException;
import java.util.Objects;
import java.util.Optional;

public class Utils {

    public static final Color BOT_COLOR = new Color(236, 234, 152);
    public static final Color ERROR_COLOR = new Color(211, 8, 8);

    public static final String MUSIC_TITLE = getTitle("Music");

    public static final String PLAY_COMMAND_NAME_BOLD_UPPERCASE = getCommandNameBoldUppercase("Play");

    public static final String NO_PERMISSION_TITLE = getTitle("No permission");
    public static final String NO_PERMISSION_DESCRIPTION = "Seems like the bot is missing the following permission: ```%s```. Please update the bot's permissions and try again.";

    public static final String QUERY_INFORMATION = "Query params: video URL or title.";
    public static final String NO_RESULTS_FOUND = "No results were found matching your query.";
    public static final String ADDED_TO_QUEUE = "Song was added to queue at position **#%d**.";
    public static final String THUMBNAIL_URL_FORMAT = "https://img.youtube.com/vi/%s/default.jpg";
    public static final String LOADING_TRACK_FAILED = "Loading the track has failed, please try again later.";
    public static final String BOT_NOT_CONNECTED_TO_VOICE_CHANNEL = "You are not connected to the same voice channel as me or I am not connected to a voice channel. Please join my channel first or use the **/summon** command.";
    public static final String MEMBER_NOT_CONNECTED_TO_VOICE_CHANNEL = "You are not connected a voice channel.";
    public static final String INTERNAL_ERROR_HAS_OCCURRED = "An internal error has occurred. Please try again later, if this continues please report it.";
    public static final String CONNECTED_TO_VOICE_CHANNEL = "Successfully connected to ```%s```.";

    private Utils() {
        throw new UnsupportedOperationException("Utils class should not be instantiated");
    }

    @Contract(pure = true)
    public static @NotNull String getTitle(@NotNull String sectionName) {
        return "Sombrero - " + getCommandNameBoldUppercase(sectionName);
    }

    public static @NotNull String getUsageFormatted(@NotNull ICommand command, @Nullable String... extraArgs) {
        String extraArgsJoin = StringUtils.join(extraArgs, " ");
        return "**/" + command.getName() + extraArgsJoin + "** - " + command.getDescription();
    }

    public static @NotNull String getCommandNameBoldUppercase(@NotNull String commandName) {
        return "**" + StringUtils.capitalize(commandName) + "**";
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull String title, @Nullable String description) {
        sendMessage(event, BOT_COLOR, title, description, true, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull Color color, @NotNull String title, @Nullable String description) {
        sendMessage(event, color, title, description, true, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull String title, @Nullable String description, boolean ephemeral) {
        sendMessage(event, BOT_COLOR, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull Color color, @NotNull String title, @Nullable String description, boolean ephemeral) {
        sendMessage(event, color, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull String title, @Nullable String description, boolean ephemeral, MessageEmbed.Field... fields) {
        sendMessage(event, BOT_COLOR, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl(), fields);
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull Color color, @NotNull String title, @Nullable String description, boolean ephemeral, @Nullable String thumbnailURL, MessageEmbed.Field... fields) {
        sendMessage(event, color, title, null, description, ephemeral, thumbnailURL, fields);
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull Color color, @NotNull String title, @Nullable String uri, @Nullable String description, boolean ephemeral, @Nullable String thumbnailURL, MessageEmbed.Field... fields) {
        event.getJDA().retrieveApplicationInfo().queue(applicationInfo -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(color)
                    .setTitle(title, uri)
                    .setDescription(description)
                    .setThumbnail(thumbnailURL)
                    .setFooter(applicationInfo.getOwner().getName() + " | Bot Developer", applicationInfo.getOwner().getEffectiveAvatarUrl());

            if (fields != null && fields.length > 0) {
                for (MessageEmbed.Field field : fields) {
                    embedBuilder.addField(field);
                }
            }

            event.replyEmbeds(embedBuilder.build())
                    .setEphemeral(ephemeral)
                    .queue();
            embedBuilder.clear();
        });
    }

    public static void sendNoPermissionMessage(@NotNull Permission expectedPermission, @NotNull SlashCommandEvent event) {
        sendMessage(event, NO_PERMISSION_TITLE, String.format(NO_PERMISSION_DESCRIPTION, expectedPermission.getName()));
    }

    public static void sendUsageMessage(@NotNull ICommand command, @NotNull SlashCommandEvent event) {
        sendMessage(event, getTitle(command.getName()), command.getUsage());
    }

    public static void sendNoResultsMessage(@NotNull ICommand command, @NotNull SlashCommandEvent event) {
        sendNoResultsMessage(getTitle(command.getName()), event);
    }

    public static void sendNoResultsMessage(@NotNull String sectionName, @NotNull SlashCommandEvent event) {
        sendMessage(event, getTitle(sectionName), NO_RESULTS_FOUND);
    }

    public static void sendAddedToQueueMessage(@NotNull SlashCommandEvent event, @NotNull String videoTitle, @NotNull String uri, @NotNull String thumbnailURL, int queuePosition) {
        sendMessage(event, BOT_COLOR, videoTitle, uri, null, false, thumbnailURL,
                new MessageEmbed.Field(PLAY_COMMAND_NAME_BOLD_UPPERCASE, String.format(ADDED_TO_QUEUE, queuePosition), true));
    }

    public static String getThumbnailURL(@NotNull String audioTrackIdentifier) {
        return String.format(THUMBNAIL_URL_FORMAT, audioTrackIdentifier);
    }

    public static void sendLoadFailedMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, ERROR_COLOR, MUSIC_TITLE, LOADING_TRACK_FAILED, false);
    }

    public static @NotNull Optional<AudioManager> getAudioManager(@NotNull SlashCommandEvent event) {
        Guild guild = event.getGuild();
        if (guild != null) {
            return Optional.of(guild.getAudioManager());
        }
        return Optional.empty();
    }

    public static boolean isMemberConnectedToVoiceChannel(@NotNull SlashCommandEvent event) {
        Member member = event.getMember();
        if (member == null) {
            return false;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null) {
            return false;
        }

        Optional<AudioManager> optionalGuildAudioManager = getAudioManager(event);
        if (optionalGuildAudioManager.isEmpty()) {
            return false;
        }

        return memberVoiceState.getChannel() != null;
    }

    public static void sendBotNotConnectedToSameVoiceChannelMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(PLAY_COMMAND_NAME_BOLD_UPPERCASE, BOT_NOT_CONNECTED_TO_VOICE_CHANNEL, true));
    }

    public static void sendMemberNotConnectedToVoiceChannelMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(PLAY_COMMAND_NAME_BOLD_UPPERCASE, MEMBER_NOT_CONNECTED_TO_VOICE_CHANNEL, true));
    }

    public static void sendErrorOccurredMessage(@NotNull SlashCommandEvent event) {
        sendErrorOccurredMessage(event, true);
    }

    @SneakyThrows
    public static void sendErrorOccurredMessage(@NotNull SlashCommandEvent event, boolean throwException) {
        sendMessage(event, ERROR_COLOR, getTitle("Error"), INTERNAL_ERROR_HAS_OCCURRED);
        if (throwException) {
            throw new UnexpectedException("Internal error occurred, please investigate");
        }
    }

    @SneakyThrows
    public static void sendErrorOccurredMessage(@NotNull SlashCommandEvent event, @NotNull Exception exception) {
        sendMessage(event, ERROR_COLOR, getTitle("Error"), INTERNAL_ERROR_HAS_OCCURRED);
        throw exception;
    }

    public static void sendVoiceChannelJoinSuccessMessage(@NotNull SlashCommandEvent event, String voiceChannelName) {
        sendMessage(event, MUSIC_TITLE, PLAY_COMMAND_NAME_BOLD_UPPERCASE, false, new MessageEmbed.Field(PLAY_COMMAND_NAME_BOLD_UPPERCASE, String.format(CONNECTED_TO_VOICE_CHANNEL, voiceChannelName), true));
    }

    public static void summonBotToVoiceChannel(@NotNull SlashCommandEvent event) {
        summonBotToVoiceChannel(event, false);
    }

    public static void summonBotToVoiceChannel(@NotNull SlashCommandEvent event, boolean silent) {
        Utils.getAudioManager(event).ifPresentOrElse(audioManager -> {
            try {
                VoiceChannel memberVoiceChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();

                audioManager.setSelfDeafened(true);
                audioManager.openAudioConnection(memberVoiceChannel);

                String voiceChannelName = Objects.requireNonNull(memberVoiceChannel).toString().replace("VC:", "");

                if (!silent) {
                    Utils.sendVoiceChannelJoinSuccessMessage(event, voiceChannelName);
                }
            } catch (Exception e) {
                Utils.sendErrorOccurredMessage(event, e);
            }
        }, () -> Utils.sendErrorOccurredMessage(event));
    }
}
