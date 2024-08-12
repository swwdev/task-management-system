package com.example.taskmanagment.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.taskmanagment.utils.ResponseUtils.NEED_EMAIL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignTaskDto {

    @Schema(description = "email the user to whom the task needs to be assigned")
    @NotBlank(message = NEED_EMAIL)
    private String assigneeEmail;
}
