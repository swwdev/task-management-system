package com.example.taskmanagment.dto.task;

import com.example.taskmanagment.dto.comment.CommentReadDto;
import com.example.taskmanagment.dto.user.UserReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithDetails extends TaskReadDto {

    @Schema(description = "author of task")
    private UserReadDto author;

    @Schema(description = "collection of task comments")
    private List<CommentReadDto> comments;

    @Schema(description = "collection of task assignees")
    private Set<UserReadDto> assignees;
}
