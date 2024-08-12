package com.example.taskmanagment.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateDto {

    @Schema(description = "header of task")
    private String header;

    @Schema(description = "description of task")
    private String description;
}