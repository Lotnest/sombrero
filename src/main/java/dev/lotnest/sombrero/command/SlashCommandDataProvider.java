package dev.lotnest.sombrero.command;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SlashCommandDataProvider {

    private SlashCommandDataProvider() {
    }

    @Contract("_ -> new")
    public static @NotNull SlashCommandData of(@NotNull Command command) {
        return Commands.slash(command.getName(), command.getDescription());
    }
}
