package com.example.taskmanagment.dto.filtering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TaskFilter {

    @Schema(description = "header of task")
    private final String header;

    @Schema(description = "available values fot status: 'pending', 'ongoing', 'completed'")
    private final String status;

    @Schema(description = "available values fot priority: 'low', 'medium', 'high'")
    private final String priority;
}
