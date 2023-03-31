package dev.lotnest.sombrero.command;

import dev.lotnest.sombrero.message.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public abstract class Command {

    public final @NotNull MessageSender messageSender;

    public abstract void execute(@NotNull SlashCommandInteractionEvent event);

    public abstract String getName();

    public final @NotNull String getFullCommandName() {
        return "/" + getName();
    }

    public abstract String getDescription();

    public String getUsage() {
        return messageSender.getUsageFormatted(this);
    }

    public CommandData getData() {
        return SlashCommandDataProvider.of(this);
    }

    public final void executeAsyncWithExceptionHandler(@NotNull SlashCommandInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
                    log.debug("Executing command '{}' (requested by {})", event.getFullCommandName(), event.getUser().getIdLong());
                    execute(event);
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    event.getHook()
                            .setEphemeral(false)
                            .sendMessage("An error has occurred while executing this command. If the problem persists, please contact the bot owner.")
                            .queue();
                    return null;
                });
    }

    public boolean isEnabled() {
        return true;
    }
}
