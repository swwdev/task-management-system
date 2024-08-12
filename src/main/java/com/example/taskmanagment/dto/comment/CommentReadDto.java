package com.example.taskmanagment.dto.comment;

import com.example.taskmanagment.dto.user.UserReadDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReadDto {

    @Schema(description = "comment id")
    private Long id;

    @Schema(description = "text of comment")
    private String text;

    @Schema(description = "author of comment")
    private UserReadDto author;
}
