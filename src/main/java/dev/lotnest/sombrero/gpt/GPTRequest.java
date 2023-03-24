package dev.lotnest.sombrero.gpt;

import com.theokanning.openai.completion.CompletionRequest;
import org.jetbrains.annotations.NotNull;

public record GPTRequest(@NotNull GPTRequestParams params) {

    public @NotNull CompletionRequest create() {
        return CompletionRequest.builder()
                .prompt(params.getPrompt())
                .model(params.getModel())
                .frequencyPenalty(params.getFrequencyPenalty())
                .temperature(params.getTemperature())
                .topP(params.getTopP())
                .presencePenalty(params.getPresencePenalty())
                .maxTokens(params.getMaxTokens())
                .stop(params.getStop())
                .build();
    }
}
