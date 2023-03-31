package dev.lotnest.sombrero.message;

import com.google.common.collect.Maps;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.command.CommandManager;
import dev.lotnest.sombrero.music.MusicManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.rmi.UnexpectedException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class MessageSender {

    private static final Color BOT_COLOR = new Color(182, 184, 179);
    private static final Color ERROR_COLOR = new Color(211, 8, 8);

    private static final String THUMBNAIL_URL_FORMAT = "https://img.youtube.com/vi/%s/default.jpg";
    private static final String NOW_PLAYING_FORMAT = "Song progress: **%s**\nSong duration: **%s**";
    private static final String PING_FORMAT = "Sombrero latency: **%d ms**\nDiscord API latency: **%d ms**";

    private static final String NO_PERMISSION_TITLE = getTitle("No permission");
    private static final String ERROR_TITLE = getTitle("Error");
    private static final String MUSIC_TITLE = getTitle("Music");
    private static final String SUMMON_TITLE = getTitle("Summon");
    private static final String SKIP_TITLE = getTitle("Skip");
    private static final String NOW_PLAYING_TITLE = getTitle("Now playing");
    private static final String PING_TITLE = getTitle("Ping");
    private static final String HELP_TITLE = getTitle("Help");
    private static final String GPT_TITLE = getTitle("GPT");
    private static final String DISCONNECT_TITLE = getTitle("Disconnect");

    private static final String ERROR_BOLD = getBoldText("Error");
    private static final String MUSIC_BOLD = getBoldText("Music");
    private static final String PLAY_BOLD = getBoldText("Play");
    private static final String PLAY_COMMAND_BOLD = getBoldText("/play");
    private static final String SUMMON_BOLD = getBoldText("Summon");
    private static final String SUMMON_COMMAND_BOLD = getBoldText("/summon");
    private static final String SKIP_BOLD = getBoldText("Skip");
    private static final String SKIP_COMMAND_BOLD = getBoldText("/skip");
    private static final String NOW_PLAYING_BOLD = getBoldText("Now playing");
    private static final String NOW_PLAYING_COMMAND_BOLD = getBoldText("/now_playing");
    private static final String PING_BOLD = getBoldText("Ping");
    private static final String PING_COMMAND_BOLD = getBoldText("/ping");
    private static final String HELP_COMMAND_BOLD = getBoldText("/help");
    private static final String GPT_COMMAND_BOLD = getBoldText("/gpt");
    private static final String DISCONNECT_BOLD = getBoldText("Disconnect");

    private static final String NO_PERMISSION_DESCRIPTION = "Hmm, looks like the bot is missing the following permission: ```%s```. Please update the bot's permissions and try again.";
    private static final String HELP_DESCRIPTION = "Shows help.";
    private static final String PING_DESCRIPTION = "Shows the bot's ping.";
    private static final String SUMMON_DESCRIPTION = "Summons the bot to your voice channel.";
    private static final String PLAY_DESCRIPTION = "Plays a song from YouTube.";
    private static final String NOW_PLAYING_DESCRIPTION = "Shows the currently playing song.";
    private static final String SKIP_DESCRIPTION = "Skips the currently playing song.";

    private static final String ADDED_TO_QUEUE = "Song was added to queue at position **#%d**.";
    private static final String LOADING_TRACK_FAILED = "Loading the song has failed, please try again later.";
    private static final String BOT_NOT_CONNECTED_TO_VOICE_CHANNEL = "I am not connected to a voice channel. Please use the /summon command first.";
    private static final String MEMBER_NOT_CONNECTED_TO_VOICE_CHANNEL = "You are not connected a voice channel.";
    private static final String INTERNAL_ERROR_HAS_OCCURRED = "An internal error has occurred. Please try again later, if this continues please report it.";
    private static final String CONNECTED_TO_VOICE_CHANNEL = "I have successfully connected to **%s**.";
    private static final String QUEUE_HAS_ENDED = "The queue has ended.";
    private static final String NO_SONGS_IN_QUEUE = "There are no songs in the queue.";
    private static final String NO_RESULTS_FOUND_FOR_QUERY = "There are no results matching your query.";
    private static final String NOW_PLAYING = ":musical_note: Now playing! (Song duration: **%s**)";
    private static final String NO_SONG_PLAYING = "There is no song currently playing.";
    private static final String RESTARTING_IN_FIVE_MINUTES = "The bot will be restarting in 5 minutes. It should be back up fairly quickly.";
    private static final String RESTARTING_IN_ONE_MINUTE = "The bot will be restarting in 1 minute.";
    private static final String MEMBER_NOT_CONNECTED_TO_SAME_VOICE_CHANNEL_AS_BOT = "You are not connected to the same voice channel as me. Please join my channel first.";
    private static final String BOT_DISCONNECTED = "I have been disconnected from the **%s** voice channel.";

    private static MessageEmbed.Field[] helpFields;

    private final Map<String, TextChannel> lastUsedTextChannels = Maps.newConcurrentMap();
    private final ApplicationInfo applicationInfo;
    private final ApplicationContext applicationContext;
    private CommandManager commandManager;

    @Contract(pure = true)
    public static @NotNull String getTitle(@NotNull String sectionName) {
        return "Sombrero - " + getBoldUppercaseText(sectionName);
    }

    public static @NotNull String getBoldText(@NotNull String text) {
        return "**" + text + "**";
    }

    public static @NotNull String getBoldUppercaseText(@NotNull String text) {
        return getBoldText(StringUtils.capitalize(text));
    }

    public @NotNull String getUsageFormatted(@NotNull Command command, @Nullable String... args) {
        String argsJoined = StringUtils.join(args, " ");
        return "**/" + command.getName() + argsJoined + "** - " + command.getDescription();
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String title, @Nullable String description) {
        sendMessage(event, BOT_COLOR, title, description, true, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull Color color, @NotNull String title, @Nullable String description) {
        sendMessage(event, color, title, description, true, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String title, @Nullable String description, boolean ephemeral) {
        sendMessage(event, BOT_COLOR, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull Color color, @NotNull String title, @Nullable String description, boolean ephemeral) {
        sendMessage(event, color, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String title, @Nullable String description, boolean ephemeral, @Nullable MessageEmbed.Field... fields) {
        sendMessage(event, BOT_COLOR, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl(), fields);
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull Color color, @NotNull String title, @Nullable String description, boolean ephemeral, @Nullable String thumbnailURL, @Nullable MessageEmbed.Field... fields) {
        sendMessage(event, color, title, null, description, ephemeral, thumbnailURL, fields);
    }

    public void sendMessage(@NotNull SlashCommandInteractionEvent event, @NotNull Color color, @NotNull String title, @Nullable String uri, @Nullable String description, boolean ephemeral, @Nullable String thumbnailURL, @Nullable MessageEmbed.Field... fields) {
        if (event.getGuild() == null) {
            return;
        }

        try {
            if (!event.isAcknowledged()) {
                event.deferReply().queue();
            }

            log.debug("Sending message to user {} in guild {}", event.getUser().getIdLong(), Objects.requireNonNull(event.getGuild()).getId());
            lastUsedTextChannels.put(event.getGuild().getId(), event.getChannel().asTextChannel());

            EmbedBuilder embedBuilder = getEmbedBuilder(color, title, uri, description, thumbnailURL, applicationInfo, fields);

            event.getHook()
                    .setEphemeral(ephemeral)
                    .editOriginalEmbeds(embedBuilder.build())
                    .queue();
        } catch (Exception exception) {
            log.error("Failed to send a message", exception);
        }
    }

    private @NotNull EmbedBuilder getEmbedBuilder(@NotNull Color color, @NotNull String title, @Nullable String uri, @Nullable String description, @Nullable String thumbnailURL, @NotNull ApplicationInfo applicationInfo, @Nullable MessageEmbed.Field[] fields) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(color)
                .setTitle(title, uri)
                .setDescription(description)
                .setThumbnail(thumbnailURL)
                .setFooter(applicationInfo.getOwner().getName() + " | Bot Developer", applicationInfo.getOwner().getEffectiveAvatarUrl());

        if (fields != null) {
            for (MessageEmbed.Field field : fields) {
                embedBuilder.addField(field);
            }
        }

        return embedBuilder;
    }

    public void sendNoPermissionMessage(@NotNull Permission expectedPermission, @NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, NO_PERMISSION_TITLE, String.format(NO_PERMISSION_DESCRIPTION, expectedPermission.getName()));
    }

    public void sendUsageMessage(@NotNull Command command, @NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, getTitle(command.getName()), command.getUsage());
    }

    public void sendAddedToQueueMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String videoTitle, @NotNull String uri, @NotNull String thumbnailURL, int queuePosition) {
        sendMessage(event, BOT_COLOR, videoTitle, uri, null, false, thumbnailURL,
                new MessageEmbed.Field(PLAY_BOLD, String.format(ADDED_TO_QUEUE, queuePosition), true));
    }

    public String getThumbnailURL(@NotNull String audioTrackIdentifier) {
        return String.format(THUMBNAIL_URL_FORMAT, audioTrackIdentifier);
    }

    public void sendLoadFailedMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, ERROR_COLOR, MUSIC_TITLE, LOADING_TRACK_FAILED, false);
    }

    public void sendPingMessage(@NotNull SlashCommandInteractionEvent event) {
        event.getJDA()
                .getRestPing()
                .queue(pingReply -> sendMessage(event, PING_TITLE, null, false,
                                new MessageEmbed.Field(PING_BOLD, String.format(PING_FORMAT, pingReply, event.getJDA().getGatewayPing()), true)),
                        throwable -> sendErrorOccurredMessage(event, throwable));
    }

    public void sendQueueHasEndedMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, SKIP_TITLE, null, false, new MessageEmbed.Field(SKIP_BOLD, QUEUE_HAS_ENDED, true));
    }

    public void sendNoSongsInTheQueueMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, SKIP_TITLE, null, false, new MessageEmbed.Field(SKIP_BOLD, NO_SONGS_IN_QUEUE, true));
    }

    @Contract(pure = true)
    public @NotNull String formatMillisecondsToHHMMSS(long milliseconds) {
        Duration durationTime = Duration.ofMillis(milliseconds);
        long durationHours = durationTime.toHours();
        long durationMinutesPart = durationTime.toMinutesPart();
        long durationSecondsPart = durationTime.toSecondsPart();

        if (durationHours >= 2562047788015L) {
            return "Live";
        }

        if (durationHours > 99) {
            durationHours = 99;
        }

        return durationHours > 0 ? String.format("%02d:%02d:%02d", durationHours, durationMinutesPart, durationSecondsPart) :
                String.format("%02d:%02d", durationMinutesPart, durationSecondsPart);
    }

    public void sendNowPlayingMessage(@NotNull SlashCommandInteractionEvent event, @NotNull AudioTrackInfo audioTrackInfo) {
        sendMessage(event, BOT_COLOR, audioTrackInfo.title, audioTrackInfo.uri, null, false,
                getThumbnailURL(audioTrackInfo.identifier),
                new MessageEmbed.Field(MUSIC_BOLD, String.format(NOW_PLAYING,
                        formatMillisecondsToHHMMSS(audioTrackInfo.length)), true));
    }

    public void sendNoSongPlayingMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, NOW_PLAYING_TITLE, null, false,
                new MessageEmbed.Field(NOW_PLAYING_BOLD, NO_SONG_PLAYING, true));
    }

    public void sendNowPlayingDetailedMessage(@NotNull MusicManager musicManager, @NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();

        if (guild != null) {
            AudioPlayer audioPlayer = musicManager.getGuildMusicManager(guild).audioPlayer();
            AudioTrack currentlyPlayingTrack = audioPlayer.getPlayingTrack();

            if (currentlyPlayingTrack != null) {
                AudioTrackInfo currentlyPlayingTrackInfo = currentlyPlayingTrack.getInfo();
                String nowPlayingInformationFormatted = String.format(NOW_PLAYING_FORMAT,
                        formatMillisecondsToHHMMSS(currentlyPlayingTrack.getPosition()),
                        formatMillisecondsToHHMMSS(currentlyPlayingTrack.getDuration()));

                sendMessage(event, BOT_COLOR, currentlyPlayingTrackInfo.title, currentlyPlayingTrackInfo.uri,
                        null, false, getThumbnailURL(currentlyPlayingTrackInfo.identifier),
                        new MessageEmbed.Field(NOW_PLAYING_BOLD, nowPlayingInformationFormatted, true));
                return;
            }
        }

        sendErrorOccurredMessage(event, true);
    }

    public synchronized void sendHelpMessage(@NotNull SlashCommandInteractionEvent event) {
        if (commandManager == null) {
            commandManager = Objects.requireNonNull(applicationContext.getBean(CommandManager.class), "CommandManager is still null");
        }

        if (helpFields == null) {
            helpFields = new MessageEmbed.Field[commandManager.getCommands().size()];
            commandManager.getCommands().forEach(command -> {
                String fullCommandName = command.getFullCommandName();
                String commandDescription = command.getDescription();
                helpFields[commandManager.getCommands().indexOf(command)] = new MessageEmbed.Field(fullCommandName, commandDescription, true);
            });
        }

        sendMessage(event, HELP_TITLE, null, false, helpFields);
    }

    public void sendGPTMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String responseFromGPT) {
        sendMessage(event, GPT_TITLE, null, false, new MessageEmbed.Field(GPT_COMMAND_BOLD, responseFromGPT, true));
    }

    public void sendBotNotConnectedToVoiceChannelMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(ERROR_BOLD, BOT_NOT_CONNECTED_TO_VOICE_CHANNEL, true));
    }

    public void sendMemberNotConnectedToVoiceChannelMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(ERROR_BOLD, MEMBER_NOT_CONNECTED_TO_VOICE_CHANNEL, true));
    }

    public void sendErrorOccurredMessage(@NotNull SlashCommandInteractionEvent event) {
        sendErrorOccurredMessage(event, true);
    }

    @SneakyThrows
    public void sendErrorOccurredMessage(@NotNull SlashCommandInteractionEvent event, boolean throwException) {
        sendMessage(event, ERROR_COLOR, ERROR_TITLE, INTERNAL_ERROR_HAS_OCCURRED);
        if (throwException) {
            throw new UnexpectedException("Unexpected error occurred, please investigate");
        }
    }

    @SneakyThrows
    public void sendErrorOccurredMessage(@NotNull SlashCommandInteractionEvent event, @NotNull Throwable throwable) {
        sendMessage(event, ERROR_COLOR, ERROR_TITLE, INTERNAL_ERROR_HAS_OCCURRED);
        throw throwable;
    }

    public void sendVoiceChannelJoinSuccessMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String voiceChannelName) {
        sendMessage(event, SUMMON_TITLE, null, false, new MessageEmbed.Field(SUMMON_BOLD, CONNECTED_TO_VOICE_CHANNEL.formatted(voiceChannelName), true));
    }

    public void sendMessageToAllGuilds(@NotNull JDA jda, @NotNull String message) {
        jda.getGuilds().forEach(guild -> getLastUsedTextChannel(guild)
                .ifPresent(textChannel -> textChannel.sendMessage(message).queue()));
    }

    public void sendRestartingInFiveMinutesMessage(@NotNull JDA jda) {
        sendMessageToAllGuilds(jda, RESTARTING_IN_FIVE_MINUTES);
    }

    public void sendRestartingInOneMinuteMessage(@NotNull JDA jda) {
        sendMessageToAllGuilds(jda, RESTARTING_IN_ONE_MINUTE);
    }

    public @NotNull Optional<TextChannel> getLastUsedTextChannel(@NotNull Guild guild) {
        return Optional.ofNullable(lastUsedTextChannels.get(guild.getId()));
    }

    public void sendNoResultsFoundMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, MUSIC_TITLE, null, false, new MessageEmbed.Field(ERROR_BOLD, NO_RESULTS_FOUND_FOR_QUERY, true));
    }

    public void sendMemberNotConnectedToSameVoiceChannelAsBotMessage(@NotNull SlashCommandInteractionEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(ERROR_BOLD, MEMBER_NOT_CONNECTED_TO_SAME_VOICE_CHANNEL_AS_BOT, true));
    }

    public void sendVoiceChannelDisconnectSuccessMessage(@NotNull SlashCommandInteractionEvent event, @NotNull String voiceChannelName) {
        sendMessage(event, DISCONNECT_TITLE, null, false, new MessageEmbed.Field(DISCONNECT_BOLD, BOT_DISCONNECTED.formatted(voiceChannelName), true));
    }
}
