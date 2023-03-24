package dev.lotnest.sombrero.gpt;

import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.jetbrains.annotations.NotNull;

public record GPTCompletionCreator(@NotNull OpenAiService openAiService, @NotNull GPTRequest gptRequest) {

    public @NotNull CompletionResult create() {
        return openAiService.createCompletion(gptRequest.create());
    }
}
