package dev.lotnest.sombrero.util;

import com.google.common.collect.Maps;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lotnest.sombrero.CommandLineRunner;
import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.event.EventListener;
import dev.lotnest.sombrero.music.MusicManager;
import dev.lotnest.sombrero.music.Song;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.rmi.UnexpectedException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Utils {

    public static final List<GatewayIntent> GATEWAY_INTENTS = List.of(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);

    public static final Color BOT_COLOR = new Color(236, 234, 152);
    public static final Color ERROR_COLOR = new Color(211, 8, 8);

    public static final String NO_PERMISSION_TITLE = getTitle("No permission");
    public static final String ERROR_TITLE = getTitle("Error");
    public static final String MUSIC_TITLE = getTitle("Music");
    public static final String SUMMON_TITLE = getTitle("Summon");
    public static final String SKIP_TITLE = getTitle("Skip");
    public static final String NOW_PLAYING_TITLE = getTitle("Now playing");
    public static final String PING_TITLE = getTitle("Ping");
    public static final String HELP_TITLE = getTitle("Help");
    public static final String GPT_TITLE = getTitle("GPT");

    public static final String MUSIC_BOLD = getBoldText("Music");
    public static final String PLAY_BOLD = getBoldText("Play");
    public static final String PLAY_COMMAND_BOLD = getBoldText("/play");
    public static final String SUMMON_BOLD = getBoldText("Summon");
    public static final String SUMMON_COMMAND_BOLD = getBoldText("/summon");
    public static final String SKIP_BOLD = getBoldText("Skip");
    public static final String SKIP_COMMAND_BOLD = getBoldText("/skip");
    public static final String NOW_PLAYING_BOLD = getBoldText("Now playing");
    public static final String NOW_PLAYING_COMMAND_BOLD = getBoldText("/now_playing");
    public static final String PING_BOLD = getBoldText("Ping");
    public static final String PING_COMMAND_BOLD = getBoldText("/ping");
    public static final String HELP_COMMAND_BOLD = getBoldText("/help");
    public static final String GPT_COMMAND_BOLD = getBoldText("/gpt");

    public static final String NO_PERMISSION_DESCRIPTION = "Hmm, looks like the bot is missing the following permission: ```%s```. Please update the bot's permissions and try again.";
    public static final String HELP_DESCRIPTION = "Shows help.";
    public static final String PING_DESCRIPTION = "Shows the bot's ping.";
    public static final String SUMMON_DESCRIPTION = "Summons the bot to your voice channel.";
    public static final String PLAY_DESCRIPTION = "Plays a song from YouTube.";
    public static final String NOW_PLAYING_DESCRIPTION = "Shows the currently playing song.";
    public static final String SKIP_DESCRIPTION = "Skips the currently playing song.";
    public static final String GPT_DESCRIPTION = "Generates text using GPT-3.";

    public static final String YOUTUBE_QUERY_INFORMATION = "Query params: video URL or title.";
    public static final String GPT_PROMPT_INFORMATION = "Any text.";
    public static final String GPT_PROMPT_MISSING = "Please provide a prompt.";
    public static final String NO_RESULTS_FOUND = "No results were found matching your query.";
    public static final String ADDED_TO_QUEUE = "Song was added to queue at position **#%d**.";
    public static final String LOADING_TRACK_FAILED = "Loading the song has failed, please try again later.";
    public static final String BOT_NOT_CONNECTED_TO_VOICE_CHANNEL = "You are not connected to the same voice channel as me or I am not connected to a voice channel. Please join my channel first or use the **/summon** command.";
    public static final String MEMBER_NOT_CONNECTED_TO_VOICE_CHANNEL = "You are not connected a voice channel.";
    public static final String INTERNAL_ERROR_HAS_OCCURRED = "An internal error has occurred. Please try again later, if this continues please report it.";
    public static final String CONNECTED_TO_VOICE_CHANNEL = "Successfully connected to **%s**.";
    public static final String QUEUE_HAS_ENDED = "The queue has ended.";
    public static final String NO_SONGS_IN_QUEUE = "There are no songs in the queue.";
    public static final String NOW_PLAYING = ":musical_note: Now playing! (Song duration: **%s**)";
    public static final String NO_SONG_PLAYING = "There is no song currently playing.";
    public static final String RESTARTING_IN_FIVE_MINUTES = "The bot will be restarting in 5 minutes. It should be back up fairly quickly.";
    public static final String RESTARTING_IN_ONE_MINUTE = "The bot will be restarting in 1 minute.";

    public static final String THUMBNAIL_URL_FORMAT = "https://img.youtube.com/vi/%s/default.jpg";
    public static final String NOW_PLAYING_FORMAT = "Song progress: **%s**\nSong duration: **%s**";
    public static final String PING_FORMAT = "Bot latency: **%d ms**\nDiscord API latency: **%d ms**";
    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final Map<String, TextChannel> LAST_USED_TEXT_CHANNELS = Maps.newConcurrentMap();

    private Utils() {
        throw new UnsupportedOperationException("Utils class should not be instantiated");
    }

    @Contract(pure = true)
    public static @NotNull String getTitle(@NotNull String sectionName) {
        return "Sombrero - " + getBoldUppercaseText(sectionName);
    }

    public static @NotNull String getUsageFormatted(@NotNull Command command, @Nullable String... args) {
        String argsJoined = StringUtils.join(args, " ");
        return "**/" + command.getName() + argsJoined + "** - " + command.getDescription();
    }

    public static @NotNull String getBoldText(@NotNull String text) {
        return "**" + text + "**";
    }

    public static @NotNull String getBoldUppercaseText(@NotNull String text) {
        return "**" + StringUtils.capitalize(text) + "**";
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

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull String title, @Nullable String description, boolean ephemeral, @Nullable MessageEmbed.Field... fields) {
        sendMessage(event, BOT_COLOR, title, description, ephemeral, event.getJDA().getSelfUser().getEffectiveAvatarUrl(), fields);
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull Color color, @NotNull String title, @Nullable String description, boolean ephemeral, @Nullable String thumbnailURL, @Nullable MessageEmbed.Field... fields) {
        sendMessage(event, color, title, null, description, ephemeral, thumbnailURL, fields);
    }

    public static void sendMessage(@NotNull SlashCommandEvent event, @NotNull Color color, @NotNull String title, @Nullable String uri, @Nullable String description, boolean ephemeral, @Nullable String thumbnailURL, @Nullable MessageEmbed.Field... fields) {
        if (event.getGuild() == null) {
            return;
        }

        try {
            if (!event.isAcknowledged()) {
                event.deferReply().queue();
            }

            log.debug("Sending message to user {} in guild {}", event.getUser().getAsTag(), Objects.requireNonNull(event.getGuild()).getId());
            LAST_USED_TEXT_CHANNELS.put(event.getGuild().getId(), event.getTextChannel());

            ApplicationInfo applicationInfo = CommandLineRunner.getInstance().getApplicationInfo();
            EmbedBuilder embedBuilder = getEmbedBuilder(color, title, uri, description, thumbnailURL, applicationInfo, fields);

            event.getHook()
                    .setEphemeral(ephemeral)
                    .editOriginalEmbeds(embedBuilder.build())
                    .queue();
        } catch (Exception exception) {
            log.error("Failed to send a message", exception);
        }
    }

    private static @NotNull EmbedBuilder getEmbedBuilder(@NotNull Color color, @NotNull String title, @Nullable String uri, @Nullable String description, @Nullable String thumbnailURL, @NotNull ApplicationInfo applicationInfo, @Nullable MessageEmbed.Field[] fields) {
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

    public static void sendNoPermissionMessage(@NotNull Permission expectedPermission, @NotNull SlashCommandEvent event) {
        sendMessage(event, NO_PERMISSION_TITLE, String.format(NO_PERMISSION_DESCRIPTION, expectedPermission.getName()));
    }

    public static void sendUsageMessage(@NotNull Command command, @NotNull SlashCommandEvent event) {
        sendMessage(event, getTitle(command.getName()), command.getUsage());
    }

    public static void sendNoResultsMessage(@NotNull Command command, @NotNull SlashCommandEvent event) {
        sendNoResultsMessage(getTitle(command.getName()), event);
    }

    public static void sendNoResultsMessage(@NotNull String sectionName, @NotNull SlashCommandEvent event) {
        sendMessage(event, sectionName, NO_RESULTS_FOUND);
    }

    public static void sendAddedToQueueMessage(@NotNull SlashCommandEvent event, @NotNull String videoTitle, @NotNull String uri, @NotNull String thumbnailURL, int queuePosition) {
        sendMessage(event, BOT_COLOR, videoTitle, uri, null, false, thumbnailURL,
                new MessageEmbed.Field(PLAY_BOLD, String.format(ADDED_TO_QUEUE, queuePosition), true));
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

    public static void sendBotNotConnectedToVoiceChannelMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(MUSIC_BOLD, BOT_NOT_CONNECTED_TO_VOICE_CHANNEL, true));
    }

    public static void sendMemberNotConnectedToVoiceChannelMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, MUSIC_TITLE, null, true, new MessageEmbed.Field(PLAY_BOLD, MEMBER_NOT_CONNECTED_TO_VOICE_CHANNEL, true));
    }

    public static void sendErrorOccurredMessage(@NotNull SlashCommandEvent event) {
        sendErrorOccurredMessage(event, true);
    }

    @SneakyThrows
    public static void sendErrorOccurredMessage(@NotNull SlashCommandEvent event, boolean throwException) {
        sendMessage(event, ERROR_COLOR, ERROR_TITLE, INTERNAL_ERROR_HAS_OCCURRED);
        if (throwException) {
            throw new UnexpectedException("Unexpected error occurred, please investigate");
        }
    }

    @SneakyThrows
    public static void sendErrorOccurredMessage(@NotNull SlashCommandEvent event, @NotNull Throwable throwable) {
        sendMessage(event, ERROR_COLOR, ERROR_TITLE, INTERNAL_ERROR_HAS_OCCURRED);
        throw throwable;
    }

    public static void sendVoiceChannelJoinSuccessMessage(@NotNull SlashCommandEvent event, String voiceChannelName) {
        sendMessage(event, SUMMON_TITLE, null, false, new MessageEmbed.Field(SUMMON_BOLD, String.format(CONNECTED_TO_VOICE_CHANNEL, voiceChannelName), true));
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

                if (!silent) {
                    Utils.sendVoiceChannelJoinSuccessMessage(event, Objects.requireNonNull(memberVoiceChannel).getName());
                }
            } catch (Exception e) {
                Utils.sendErrorOccurredMessage(event, e);
            }
        }, () -> Utils.sendErrorOccurredMessage(event));
    }

    public static boolean isBotConnectedToVoiceChannel(@NotNull Member botMember) {
        return botMember.getVoiceState() != null && botMember.getVoiceState().getChannel() != null;
    }

    public static Queue<Song> getSongQueue(@NotNull Guild guild) {
        return MusicManager.getInstance()
                .getGuildMusicManager(guild)
                .getMusicScheduler()
                .getSongQueue();
    }

    public static void sendQueueHasEndedMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, SKIP_TITLE, null, false, new MessageEmbed.Field(SKIP_BOLD, QUEUE_HAS_ENDED, true));
    }

    public static void sendNoSongsInTheQueueMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, SKIP_TITLE, null, false, new MessageEmbed.Field(SKIP_BOLD, NO_SONGS_IN_QUEUE, true));
    }

    public static void sendNowPlayingMessage(@NotNull SlashCommandEvent event, @NotNull AudioTrackInfo audioTrackInfo) {
        sendMessage(event, BOT_COLOR, audioTrackInfo.title, audioTrackInfo.uri, null, false,
                getThumbnailURL(audioTrackInfo.identifier),
                new MessageEmbed.Field(MUSIC_BOLD, String.format(NOW_PLAYING,
                        formatMillisecondsToHHMMSS(audioTrackInfo.length)), true));
    }

    public static void sendNoSongPlayingMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, NOW_PLAYING_TITLE, null, false,
                new MessageEmbed.Field(NOW_PLAYING_BOLD, NO_SONG_PLAYING, true));
    }

    public static void sendNowPlayingDetailedMessage(@NotNull SlashCommandEvent event) {
        MusicManager musicManager = MusicManager.getInstance();
        Guild guild = event.getGuild();

        if (guild != null) {
            AudioPlayer audioPlayer = musicManager.getGuildMusicManager(guild).getAudioPlayer();
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

        Utils.sendErrorOccurredMessage(event, true);
    }

    public static void sendPingMessage(@NotNull SlashCommandEvent event) {
        event.getJDA()
                .getRestPing()
                .queue(pingReply -> sendMessage(event, PING_TITLE, null, false,
                                new MessageEmbed.Field(PING_BOLD, String.format(PING_FORMAT, pingReply, event.getJDA().getGatewayPing()), true)),
                        throwable -> Utils.sendErrorOccurredMessage(event, throwable));
    }

    @Contract(pure = true)
    public static @NotNull String formatMillisecondsToHHMMSS(long milliseconds) {
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

    public static void sendHelpMessage(@NotNull SlashCommandEvent event) {
        sendMessage(event, HELP_TITLE, null, false,
                new MessageEmbed.Field(HELP_COMMAND_BOLD, HELP_DESCRIPTION, true),
                new MessageEmbed.Field(PING_COMMAND_BOLD, PING_DESCRIPTION, true),
                new MessageEmbed.Field(SUMMON_COMMAND_BOLD, SUMMON_DESCRIPTION, true),
                new MessageEmbed.Field(PLAY_COMMAND_BOLD, PLAY_DESCRIPTION, true),
                new MessageEmbed.Field(NOW_PLAYING_COMMAND_BOLD, NOW_PLAYING_DESCRIPTION, true),
                new MessageEmbed.Field(SKIP_COMMAND_BOLD, SKIP_DESCRIPTION, true),
                new MessageEmbed.Field(GPT_COMMAND_BOLD, GPT_DESCRIPTION, true));
    }

    public static @NotNull Optional<TextChannel> getLastUsedTextChannel(@NotNull Guild guild) {
        return Optional.ofNullable(LAST_USED_TEXT_CHANNELS.get(guild.getId()));
    }

    public static void sendMessageToAllGuilds(@NotNull JDA jda, @NotNull String message) {
        jda.getGuilds().forEach(guild -> getLastUsedTextChannel(guild)
                .ifPresent(textChannel -> textChannel.sendMessage(message).queue()));
    }

    public static void sendRestartingInFiveMinutesMessage(@NotNull JDA jda) {
        sendMessageToAllGuilds(jda, RESTARTING_IN_FIVE_MINUTES);
    }

    public static void sendRestartingInOneMinuteMessage(@NotNull JDA jda) {
        sendMessageToAllGuilds(jda, RESTARTING_IN_ONE_MINUTE);
    }

    public static @NotNull Optional<JDA> getJDA() {
        return EventListener.getJda();
    }

    public static void restart() {
        log.info("Received restart action, restarting in 5 minutes");


        getJDA().ifPresentOrElse(jda -> {
            sendRestartingInFiveMinutesMessage(jda);

            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> sendRestartingInOneMinuteMessage(jda), 4, TimeUnit.MINUTES);
            SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
                jda.shutdown();
                System.exit(0);
            }, 5, TimeUnit.MINUTES);
        }, () -> System.exit(0));
    }

    public static void sendGPTMessage(@NotNull SlashCommandEvent event, @NotNull String responseFromGPT) {
        sendMessage(event, GPT_TITLE, null, false, new MessageEmbed.Field(GPT_COMMAND_BOLD, responseFromGPT, true));
    }
}
