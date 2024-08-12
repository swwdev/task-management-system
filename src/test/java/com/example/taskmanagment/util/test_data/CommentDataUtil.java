package com.example.taskmanagment.util.test_data;

import com.example.taskmanagment.dto.comment.CommentCreateDto;
import com.example.taskmanagment.models.Comment;

import static com.example.taskmanagment.util.test_data.TaskDataUtil.TASK_ID;

public class CommentDataUtil {
    public static final Long COMMENT_ID = 1L;
    public static final String COMMENT_TEXT = "Comment for task 1 by user 1";

    private CommentDataUtil() {

    }

    public static CommentCreateDto createCommentCreateDto() {
        return CommentCreateDto.builder()
                .taskId(TASK_ID)
                .text(COMMENT_TEXT)
                .build();
    }

    public static Comment createComment() {
        return Comment.builder()
                .id(COMMENT_ID)
                .text(COMMENT_TEXT)
                .build();
    }
}
