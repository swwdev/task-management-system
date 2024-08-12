package com.example.taskmanagment.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.taskmanagment.utils.ResponseUtils.NEED_TASK_ID;
import static com.example.taskmanagment.utils.ResponseUtils.NEED_TEXT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateDto {

    @NotNull(message = NEED_TASK_ID)
    @Schema(description = "id of the task under which the comment is left")
    private Long taskId;

    @Schema(description = "text of comment")
    @NotBlank(message = NEED_TEXT)
    private String text;
}
