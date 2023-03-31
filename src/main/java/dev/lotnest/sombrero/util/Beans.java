package dev.lotnest.sombrero.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.theokanning.openai.service.OpenAiService;
import dev.lotnest.sombrero.event.EventListener;
import dev.lotnest.sombrero.music.GuildMusicManager;
import dev.lotnest.sombrero.music.MusicHandler;
import dev.lotnest.sombrero.music.Song;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Queue;

@Component
public class Beans {

    @SneakyThrows
    @Bean
    ApplicationInfo applicationInfo(@Autowired @NotNull EventListener eventListener, @Autowired @NotNull ActivityProvider activityProvider) {
        return DefaultShardManagerBuilder.createDefault(ApiKeys.DISCORD_BOT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI)
                .addEventListeners(eventListener)
                .setActivity(activityProvider.getMainActivity())
                .build()
                .retrieveApplicationInfo()
                .complete(true);
    }

    @Bean
    OpenAiService openAiService() {
        return new OpenAiService(ApiKeys.OPEN_AI, Duration.of(60, ChronoUnit.SECONDS));
    }

    @Bean
    Map<Long, GuildMusicManager> musicManagers() {
        return Maps.newConcurrentMap();
    }

    @Bean
    AudioPlayerManager audioPlayerManager() {
        return new DefaultAudioPlayerManager();
    }

    @Bean
    AudioPlayer audioPlayer(@Autowired @NotNull AudioPlayerManager audioPlayerManager) {
        return audioPlayerManager.createPlayer();
    }

    @Bean
    MusicHandler musicHandler(@Autowired @NotNull AudioPlayer audioPlayer) {
        return new MusicHandler(audioPlayer);
    }

    @Bean
    Queue<Song> songQueue() {
        return Queues.newLinkedBlockingQueue();
    }

    @Bean
    JDA jda(@Autowired @NotNull ApplicationInfo applicationInfo) {
        return applicationInfo.getJDA();
    }

    @SneakyThrows
    @Bean
    YouTube youTube() {
        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                null)
                .setApplicationName("sombrero-discord-bot")
                .build();
    }

    @Bean
    Cache<Long, Member> membersAwaitingSpotifyWebApiAuthorization() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.of(15, ChronoUnit.MINUTES))
                .build();
    }
}
