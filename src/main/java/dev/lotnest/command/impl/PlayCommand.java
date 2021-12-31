package dev.lotnest.command.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import dev.lotnest.Sombrero;
import dev.lotnest.command.ICommand;
import dev.lotnest.util.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class PlayCommand implements ICommand {

    private final YouTube youTube;

    @SneakyThrows
    public PlayCommand() {
        youTube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null)
                .setApplicationName("Sombrero Discord Bot")
                .build();
    }

    @SneakyThrows
    @Nullable
    public String executeYouTubeSearch(@NotNull String searchTerm) {
        List<SearchResult> searchResults = youTube.search()
                .list("id,snippet")
                .setQ(searchTerm)
                .setMaxResults(1L)
                .setFields("items(id/kind,id/videoId,id/playlistId,snippet/title,snippet/thumbnails/default/url)")
                .setKey(System.getenv("YOUTUBE_KEY"))
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
    public void execute(@NotNull SlashCommandEvent event) {
        if (event.getChannelType().equals(ChannelType.TEXT)) {
            Guild guild = event.getGuild();
            if (guild != null) {
                Member botMember = guild.getMember(event.getJDA().getSelfUser());
                if (botMember == null) {
                    return;
                }

                if (!botMember.hasPermission(Permission.VOICE_CONNECT)) {
                    Utils.sendNoPermissionMessage(Permission.VOICE_CONNECT, event);
                    return;
                }

                OptionMapping searchTermOptionMapping = event.getOption("query");
                if (searchTermOptionMapping == null) {
                    Utils.sendUsageMessage(this, event);
                    return;
                }

                String youtubeSearchResult = executeYouTubeSearch(searchTermOptionMapping.getAsString());
                if (youtubeSearchResult == null) {
                    Utils.sendNoResultsMessage(this, event);
                    return;
                }

                Utils.sendAddedToQueueMessage(event, "Video Title Test");
            }
        }
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play YouTube music directly from your server's voice channel. " + Utils.QUERY_INFORMATION;
    }

    @Override
    public String getUsage() {
        return Utils.getUsageFormatted(this, "search-term");
    }
}
