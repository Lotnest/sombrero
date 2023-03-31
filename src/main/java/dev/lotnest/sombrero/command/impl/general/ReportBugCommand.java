package dev.lotnest.sombrero.command.impl.general;

import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.message.MessageSender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ReportBugCommand extends Command {

    private final Modal bugReportModal;

    public ReportBugCommand(@NotNull MessageSender messageSender) {
        super(messageSender);

        TextInput bugReportTitleInput = TextInput.create("title", "Title", TextInputStyle.SHORT)
                .setPlaceholder("Title of this bug report")
                .setRequiredRange(5, 100)
                .build();
        TextInput bugReportDescriptionInput = TextInput.create("description", "Description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Describe what happened and how to reproduce this bug, if applicable")
                .setRequiredRange(30, 2000)
                .build();
        bugReportModal = Modal.create("report_bug", "Report a bug")
                .addComponents(ActionRow.of(bugReportTitleInput), ActionRow.of(bugReportDescriptionInput))
                .setTitle("Report a bug")
                .build();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.replyModal(bugReportModal).queue();
    }

    @Override
    public String getName() {
        return "report_bug";
    }

    @Override
    public String getDescription() {
        return "Report a bug to the bot owner.";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
