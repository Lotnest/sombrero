package dev.lotnest.sombrero.command.impl.music;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.command.SlashCommandDataProvider;
import dev.lotnest.sombrero.music.MusicManager;
import dev.lotnest.sombrero.util.ApiKeys;
import dev.lotnest.sombrero.util.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;

@Component
public class PlayCommand extends Command {

    private static final String YOUTUBE_QUERY_INFORMATION = "Query params: video URL or title.";

    private final Utils utils;
    private final YouTube youTube;
    private final MusicManager musicManager;

    public PlayCommand(@NotNull Utils utils, @NotNull YouTube youTube, @NotNull MusicManager musicManager) {
        super(utils.messageSender());
        this.utils = utils;
        this.youTube = youTube;
        this.musicManager = musicManager;
    }

    private boolean isValidUrl(@NotNull String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @SneakyThrows
    @Nullable
    private String performYouTubeSearch(@NotNull String searchTerm) {
        List<SearchResult> searchResults = youTube.search()
                .list(Collections.singletonList("id,snippet"))
                .setQ(searchTerm)
                .setMaxResults(1L)
                .setFields("items(id/kind,id/videoId,id/playlistId,snippet/title,snippet/thumbnails/default/url)")
                .setKey(ApiKeys.YOUTUBE)
                .execute()
                .getItems();

        if (!searchResults.isEmpty()) {
            SearchResult firstResult = searchResults.get(0);
            String videoId = firstResult.getId().getVideoId();
            String playlistId = firstResult.getId().getPlaylistId();

            if (firstResult.getId().getKind().equals("youtube#video")) {
                return "https://www.youtube.com/watch?v=" + videoId;
            } else if (firstResult.getId().getKind().equals("youtube#playlist")) {
                return "https://www.youtube.com/playlist?list=" + playlistId;
            }
        }

        return null;
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

                Member member = event.getMember();
                if (member == null) {
                    return;
                }

                if (!botMember.hasPermission(Permission.VOICE_CONNECT)) {
                    messageSender.sendNoPermissionMessage(Permission.VOICE_CONNECT, event);
                    return;
                }

                if (utils.isMemberConnectedToVoiceChannel(member)) {
                    if (!utils.isBotConnectedToVoiceChannel(botMember)) {
                        utils.summonBotToVoiceChannel(event, true);
                    }

                    OptionMapping searchTermOptionMapping = event.getOption("query");
                    if (searchTermOptionMapping == null) {
                        messageSender.sendUsageMessage(this, event);
                        return;
                    }

                    String youtubeSearchResult = performYouTubeSearch(searchTermOptionMapping.getAsString());
                    if (youtubeSearchResult == null) {
                        messageSender.sendNoResultsFoundMessage(event);
                        return;
                    }

                    if (isValidUrl(youtubeSearchResult)) {
                        musicManager.play(event, youtubeSearchResult);
                    } else {
                        musicManager.play(event, youtubeSearchResult.substring(youtubeSearchResult.indexOf("h")));
                    }
                } else {
                    messageSender.sendMemberNotConnectedToVoiceChannelMessage(event);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play YouTube music directly from your server's voice channel. " + YOUTUBE_QUERY_INFORMATION;
    }

    @Override
    public String getUsage() {
        return messageSender.getUsageFormatted(this, "search-term");
    }

    @Override
    public CommandData getData() {
        return SlashCommandDataProvider.of(this)
                .addOption(OptionType.STRING, "query", YOUTUBE_QUERY_INFORMATION, true);
    }
}
