package dev.lotnest.sombrero.command.impl.music;

import com.google.common.cache.Cache;
import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.message.MessageSender;
import dev.lotnest.sombrero.util.ApiKeys;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

@Component
@Slf4j
public class SpotifyTestCommand extends Command {

    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri("http://localhost:8080/spotify/codeCallback");
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(ApiKeys.SPOTIFY_CLIENT_ID)
            .setClientSecret(ApiKeys.SPOTIFY_CLIENT_SECRET)
            .setRedirectUri(REDIRECT_URI)
            .build();

    private final Cache<Long, Member> membersAwaitingSpotifyWebApiAuthorization;

    public SpotifyTestCommand(@NotNull MessageSender messageSender, @NotNull Cache<Long, Member> membersAwaitingSpotifyWebApiAuthorization) {
        super(messageSender);
        this.membersAwaitingSpotifyWebApiAuthorization = membersAwaitingSpotifyWebApiAuthorization;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        if (event.getMember() == null) {
            return;
        }

        event.deferReply(true).queue();

        if (membersAwaitingSpotifyWebApiAuthorization.getIfPresent(event.getMember().getIdLong()) != null) {
            messageSender.sendMessage(event, "**Spotify**", "I have already sent you an authorization link. Please check your messages." +
                    " If you have not received the code, please use the **/resend_spotify_code** command.");
            return;
        }

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri().build();
        authorizationCodeUriRequest.executeAsync()
                .whenComplete((uri, throwable) -> {
                    if (throwable != null) {
                        log.error("An error occurred whilst trying to get SpotifyAPI URI", throwable);
                        return;
                    }

                    String uriString = uri.toString();
                    log.info("Generated Spotify API URI for {}: {}", event.getUser().getIdLong(), uriString);
                    membersAwaitingSpotifyWebApiAuthorization.put(event.getMember().getIdLong(), event.getMember());
                    messageSender.sendMessage(event, "**Spotify** ", uriString);
                });
    }

    @Override
    public String getName() {
        return "spotify_test";
    }

    @Override
    public String getDescription() {
        return "Spotify Test.";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
