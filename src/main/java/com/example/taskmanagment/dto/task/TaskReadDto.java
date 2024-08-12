package com.example.taskmanagment.dto.task;

import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskReadDto {

    @Schema(description = "id of task")
    private Long id;

    @Schema(description = "header of task")
    private String header;

    @Schema(description = "description of task")
    private String description;

    @Schema(description = "status of task")
    private Status status;

    @Schema(description = "priority of task")
    private Priority priority;

}
