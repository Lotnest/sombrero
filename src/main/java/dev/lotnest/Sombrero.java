package dev.lotnest;

import dev.lotnest.event.EventListener;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class Sombrero {

    public static final Logger LOGGER = JDALogger.getLog(Sombrero.class);

    @SneakyThrows
    public static void main(String[] args) {
        DefaultShardManagerBuilder.createDefault(System.getenv("BOT_TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .setMemberCachePolicy(MemberCachePolicy.ALL.and(MemberCachePolicy.VOICE))
                .enableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                .setBulkDeleteSplittingEnabled(false)
                .addEventListeners(new EventListener())
                .setActivity(Activity.playing("/help | Sombrero Galaxy"))
                .build();
    }
}
