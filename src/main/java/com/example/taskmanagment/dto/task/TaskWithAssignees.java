package com.example.taskmanagment.dto.task;

import com.example.taskmanagment.dto.user.UserReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithAssignees extends TaskReadDto {

    @Schema(description = "collection of task assignees")
    private Set<UserReadDto> assignees;
}
