package dev.lotnest.sombrero.command.impl.gpt;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.service.OpenAiService;
import dev.lotnest.sombrero.command.Command;
import dev.lotnest.sombrero.gpt.GPTCompletionCreator;
import dev.lotnest.sombrero.gpt.GPTRequest;
import dev.lotnest.sombrero.gpt.GPTRequestParams;
import dev.lotnest.sombrero.util.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class GPTCommand implements Command {

    private static final String PROMPT_OPTION_NAME = "prompt";

    private final @NotNull CommandData commandData;
    private final @NotNull OpenAiService openAiService;

    @SneakyThrows
    public GPTCommand(@NotNull OpenAiService openAiService) {
        this.openAiService = openAiService;
        commandData = new CommandData(getName(), getDescription());
        commandData.addOption(OptionType.STRING, PROMPT_OPTION_NAME, Utils.GPT_PROMPT_INFORMATION, true);
    }

    @Override
    public void execute(@NotNull SlashCommandEvent event) {
        event.deferReply().queue();

        OptionMapping promptOption = event.getOption(PROMPT_OPTION_NAME);
        if (promptOption == null) {
            Utils.sendGPTMessage(event, Utils.GPT_PROMPT_MISSING);
            return;
        }

        GPTRequest gptRequest = new GPTRequest(GPTRequestParams.basedOnDefaultWithPrompt(promptOption.getAsString()));
        GPTCompletionCreator gptCompletionCreator = new GPTCompletionCreator(openAiService, gptRequest);
        List<CompletionChoice> choices = gptCompletionCreator.create().getChoices();

        if (choices.isEmpty()) {
            Utils.sendGPTMessage(event, Utils.NO_RESULTS_FOUND);
            return;
        }

        String response = choices.get(0).getText().strip();
        if (response.matches("^\\?.+$")) {
            response = response.replaceFirst("\\?", "");
        }
        Utils.sendGPTMessage(event, response);
    }

    @Override
    public String getName() {
        return "gpt";
    }

    @Override
    public String getDescription() {
        return Utils.GPT_DESCRIPTION;
    }

    @Override
    public String getUsage() {
        return Utils.getUsageFormatted(this, PROMPT_OPTION_NAME);
    }
}
