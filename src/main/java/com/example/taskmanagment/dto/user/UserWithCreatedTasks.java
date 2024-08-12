package com.example.taskmanagment.dto.user;

import com.example.taskmanagment.dto.task.TaskReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserWithCreatedTasks extends UserReadDto {

    @Schema(description = "created tasks of user")
    private List<TaskReadDto> createdTasks;
}
