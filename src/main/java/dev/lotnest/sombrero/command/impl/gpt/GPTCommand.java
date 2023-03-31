package dev.lotnest.sombrero.command.impl.gpt;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.service.OpenAiService;
import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.command.SlashCommandDataProvider;
import dev.lotnest.sombrero.gpt.GPTCompletionCreator;
import dev.lotnest.sombrero.gpt.GPTRequest;
import dev.lotnest.sombrero.gpt.GPTRequestParams;
import dev.lotnest.sombrero.message.MessageSender;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GPTCommand extends Command {

    private static final String PROMPT_OPTION_NAME = "prompt";

    private final OpenAiService openAiService;

    public GPTCommand(@NotNull MessageSender messageSender, @NotNull OpenAiService openAiService) {
        super(messageSender);
        this.openAiService = openAiService;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        OptionMapping promptOption = event.getOption(PROMPT_OPTION_NAME);
        if (promptOption == null) {
            messageSender.sendGPTMessage(event, "Please provide a prompt.");
            return;
        }

        GPTRequest gptRequest = new GPTRequest(GPTRequestParams.defaultParamsWithPrompt(promptOption.getAsString()));
        GPTCompletionCreator gptCompletionCreator = new GPTCompletionCreator(openAiService, gptRequest);
        List<CompletionChoice> choices = gptCompletionCreator.create().getChoices();

        if (choices.isEmpty()) {
            messageSender.sendGPTMessage(event, "No results were found matching your prompt.");
            return;
        }

        String response = choices.get(0).getText().replaceAll("^\\W+", "");
        messageSender.sendGPTMessage(event, response);
    }

    @Override
    public String getName() {
        return "gpt";
    }

    @Override
    public String getDescription() {
        return "Generates text using GPT-3.";
    }

    @Override
    public String getUsage() {
        return messageSender.getUsageFormatted(this, PROMPT_OPTION_NAME);
    }

    @Override
    public CommandData getData() {
        return SlashCommandDataProvider.of(this)
                .addOption(OptionType.STRING, PROMPT_OPTION_NAME, "The prompt for the GPT-3 model.", true);
    }
}
