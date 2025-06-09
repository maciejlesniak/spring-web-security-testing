package pl.sparkidea.demo.websec.api;

import com.fasterxml.jackson.annotation.JsonProperty;

record MessageResponseDto(
        @JsonProperty("msg")
        String message
) {
}
