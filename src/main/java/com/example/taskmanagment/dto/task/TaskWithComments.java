package com.example.taskmanagment.dto.task;

import com.example.taskmanagment.dto.comment.CommentReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithComments extends TaskReadDto {

    @Schema(description = "collection of task comments")
    private List<CommentReadDto> comments;
}
