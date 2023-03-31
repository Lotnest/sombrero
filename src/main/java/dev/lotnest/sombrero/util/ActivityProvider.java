package dev.lotnest.sombrero.util;

import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ActivityProvider {

    @Contract(" -> new")
    public @NotNull Activity getMainActivity() {
        return Activity.listening("/help | Sombrero Galaxy");
    }

    @Contract(" -> new")
    public @NotNull Activity getOngoingDevelopmentActivity() {
        return Activity.playing("maintenance mode | Sombrero Galaxy");
    }
}
