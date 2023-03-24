package dev.lotnest.sombrero;

import com.theokanning.openai.service.OpenAiService;
import dev.lotnest.sombrero.command.CommandManager;
import dev.lotnest.sombrero.event.EventListener;
import dev.lotnest.sombrero.util.ApiKeys;
import dev.lotnest.sombrero.util.Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Slf4j
public class CommandLineRunner {

    private static CommandLineRunner instance;

    private final ApplicationInfo applicationInfo;

    @SneakyThrows
    private CommandLineRunner() {
        OpenAiService openAiService = new OpenAiService(ApiKeys.OPEN_AI, Duration.of(60, ChronoUnit.SECONDS));
        CommandManager commandManager = new CommandManager(openAiService);

        applicationInfo = DefaultShardManagerBuilder.createDefault(ApiKeys.DISCORD_BOT)
                .enableIntents(Utils.GATEWAY_INTENTS)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                .addEventListeners(new EventListener(commandManager))
                .setActivity(Activity.playing("/help | Sombrero Galaxy"))
                .build()
                .retrieveApplicationInfo()
                .complete(true);
    }

    public static void run(String[] args) {
        log.debug("Running CommandLineRunner with args: {}", Arrays.toString(args));
        getInstance();
    }

    @NotNull
    public static CommandLineRunner getInstance() {
        if (instance == null) {
            instance = new CommandLineRunner();
        }

        return instance;
    }

    @NotNull
    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
