package com.example.taskmanagment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExceptionResponse {

    @Schema(description = "type of exception")
    private final String exceptionClass;

    @Schema(description = "message of exception")
    private final String message;
}
