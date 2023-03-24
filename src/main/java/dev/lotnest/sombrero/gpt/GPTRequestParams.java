package dev.lotnest.sombrero.gpt;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder
@Data
public class GPTRequestParams {

    private static final @NotNull GPTRequestParams DEFAULT = GPTRequestParams.builder()
            .model("text-davinci-003")
            .temperature(0.7)
            .maxTokens(2048)
            .build();

    private String prompt;
    private @NotNull String model;
    private @Nullable Double frequencyPenalty;
    private double temperature;
    private @Nullable Double topP;
    private @Nullable Double presencePenalty;
    private int maxTokens;
    private @Nullable List<String> stop;

    public static @NotNull GPTRequestParams defaultParamsWithPrompt(@NotNull String prompt) {
        return GPTRequestParams.builder()
                .prompt(prompt)
                .model(DEFAULT.getModel())
                .frequencyPenalty(DEFAULT.getFrequencyPenalty())
                .temperature(DEFAULT.getTemperature())
                .topP(DEFAULT.getTopP())
                .presencePenalty(DEFAULT.getPresencePenalty())
                .maxTokens(DEFAULT.getMaxTokens())
                .stop(DEFAULT.getStop())
                .build();
    }
}
