package com.eventify.app.model.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationForm {

    @NotNull(message = "Message is mandatory")
    @NotBlank(message = "Message is mandatory")
    private String message;

    @NotNull(message = "date is mandatory")
    @NotBlank(message = "date is mandatory")
    private String dateTime;

    private boolean isRead;
}
