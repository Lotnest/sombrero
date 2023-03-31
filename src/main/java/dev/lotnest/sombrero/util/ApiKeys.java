package dev.lotnest.sombrero.util;

import org.apache.commons.lang3.Validate;

public class ApiKeys {

    public static final String DISCORD_BOT = Validate.notBlank(System.getenv("BOT_TOKEN"), "BOT_TOKEN is not set");
    public static final String YOUTUBE = Validate.notBlank(System.getenv("YOUTUBE_KEY"), "YOUTUBE_KEY is not set");
    public static final String OPEN_AI = Validate.notBlank(System.getenv("OPEN_AI_KEY"), "OPEN_AI_KEY is not set");
    public static final String SPOTIFY_CLIENT_ID = Validate.notBlank(System.getenv("SPOTIFY_CLIENT_ID"), "SPOTIFY_CLIENT_ID is not set");
    public static final String SPOTIFY_CLIENT_SECRET = Validate.notBlank(System.getenv("SPOTIFY_CLIENT_SECRET"), "SPOTIFY_CLIENT_SECRET is not set");

    private ApiKeys() {
    }
}
