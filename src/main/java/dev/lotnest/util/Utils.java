package dev.lotnest.util;

import dev.lotnest.command.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class Utils {

    public static final Color BOT_COLOR = new Color(236, 234, 152);

    public static final String NO_PERMISSION_TITLE = getTitle("No permission");
    public static final String MUSIC_TITLE = getTitle("Music");

    public static final String NO_PERMISSION_DESCRIPTION = "Seems like the bot is missing the following permission: ```%s```. Please update the bot's permissions and try again.";

    public static final String QUERY_INFORMATION = "Query params: video URL or title.";

    private Utils() {
        throw new UnsupportedOperationException("Utils class should not be instantiated");
    }

    @Contract(pure = true)
    public static @NotNull String getTitle(@NotNull String sectionName) {
        return "Sombrero - " + getCommandNameBoldUppercase(sectionName);
    }

    public static @NotNull String getUsageFormatted(@NotNull ICommand command, @Nullable String... extraArgs) {
        String extraArgsJoin = StringUtils.join(extraArgs, " ");
        return "**/" + command.getName() + extraArgsJoin + "** - " + command.getDescription();
    }

    public static @NotNull String getCommandNameBoldUppercase(@NotNull String commandName) {
        return "**" + StringUtils.capitalize(commandName) + "**";
    }

    public static void sendNoPermissionMessage(@NotNull Permission expectedPermission, @NotNull SlashCommandEvent event) {
        event.getJDA().retrieveApplicationInfo().queue(applicationInfo -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(BOT_COLOR)
                    .setTitle(NO_PERMISSION_TITLE)
                    .setDescription(String.format(NO_PERMISSION_DESCRIPTION, expectedPermission.getName()))
                    .setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setFooter(applicationInfo.getOwner().getName() + " | Bot Developer", applicationInfo.getOwner().getEffectiveAvatarUrl());
            event.replyEmbeds(embedBuilder.build()).queue();
            embedBuilder.clear();
        });
    }

    public static void sendUsageMessage(@NotNull ICommand command, @NotNull SlashCommandEvent event) {
        event.getJDA().retrieveApplicationInfo().queue(applicationInfo -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(BOT_COLOR)
                    .setTitle(getTitle(command.getName()))
                    .setDescription(command.getUsage())
                    .setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setFooter(applicationInfo.getOwner().getName() + " | Bot Developer", applicationInfo.getOwner().getEffectiveAvatarUrl());
            event.replyEmbeds(embedBuilder.build())
                    .setEphemeral(true)
                    .queue();
            embedBuilder.clear();
        });
    }

    public static void sendNoResultsMessage(@NotNull ICommand command, @NotNull SlashCommandEvent event) {
        event.getJDA().retrieveApplicationInfo().queue(applicationInfo -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(BOT_COLOR)
                    .setTitle(getTitle(command.getName()))
                    .setDescription("No results were found matching your query.")
                    .setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setFooter(applicationInfo.getOwner().getName() + " | Bot Developer", applicationInfo.getOwner().getEffectiveAvatarUrl());
            event.replyEmbeds(embedBuilder.build())
                    .setEphemeral(true)
                    .queue();
            embedBuilder.clear();
        });
    }

    public static void sendAddedToQueueMessage(@NotNull SlashCommandEvent event, @NotNull String videoTitle) {
        event.getJDA().retrieveApplicationInfo().queue(applicationInfo -> {
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(BOT_COLOR)
                    .setTitle(MUSIC_TITLE)
                    .addField(getCommandNameBoldUppercase("Play TEST"), String.format("```%s``` was added to queue at position **#%d**.", videoTitle, 1), true)
                    .setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setFooter(applicationInfo.getOwner().getName() + " | Bot Developer", applicationInfo.getOwner().getEffectiveAvatarUrl());
            event.replyEmbeds(embedBuilder.build()).queue();
            embedBuilder.clear();
        });
    }
}
