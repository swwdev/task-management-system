package com.example.taskmanagment.dto.task;

import com.example.taskmanagment.dto.user.UserReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithAuthor extends TaskReadDto {

    @Schema(description = "author of task")
    private UserReadDto author;
}
