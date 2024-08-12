package com.example.taskmanagment.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.taskmanagment.utils.ResponseUtils.NEED_DESCRIPTION;
import static com.example.taskmanagment.utils.ResponseUtils.NEED_HEADER;
import static com.example.taskmanagment.utils.ResponseUtils.NEED_PRIORITY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreatedDto {

    @Schema(description = "header of task")
    @NotBlank(message = NEED_HEADER)
    private String header;

    @Schema(description = "description of task")
    @NotBlank(message = NEED_DESCRIPTION)
    private String description;

    @Schema(description = "priority of task. Available values of priority: 'low', 'medium', 'high'")
    @NotNull(message = NEED_PRIORITY)
    private String priority;
}
