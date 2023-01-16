package dev.lotnest;

import dev.lotnest.event.EventListener;
import dev.lotnest.util.Utils;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class CommandLineRunner {

    @SneakyThrows
    public CommandLineRunner() {
        DefaultShardManagerBuilder.createDefault(System.getenv("BOT_TOKEN"))
                .enableIntents(Utils.GATEWAY_INTENTS)
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                .addEventListeners(new EventListener())
                .setActivity(Activity.playing("/help | Sombrero Galaxy"))
                .build();
    }
}
