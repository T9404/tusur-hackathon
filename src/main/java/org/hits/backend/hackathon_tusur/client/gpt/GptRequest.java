package org.hits.backend.hackathon_tusur.client.gpt;

public record GptRequest(
        String modelUri,
        MessageRequestDto[] messages,
        CompletionOptionsRequestDto completionOptions
) {
    public record MessageRequestDto(
            String text,
            String role
    ) {

    }

    public record CompletionOptionsRequestDto(
            boolean stream,
            String maxTokens,
            String temperature
    ) {

    }
}
