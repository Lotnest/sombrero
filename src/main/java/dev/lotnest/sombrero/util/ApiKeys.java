package dev.lotnest.sombrero.util;

public class ApiKeys {

    public static final String DISCORD_BOT = System.getenv("BOT_TOKEN");
    public static final String YOUTUBE = System.getenv("YOUTUBE_KEY");
    public static final String OPEN_AI = System.getenv("OPEN_AI_KEY");

    private ApiKeys() {
    }
}
