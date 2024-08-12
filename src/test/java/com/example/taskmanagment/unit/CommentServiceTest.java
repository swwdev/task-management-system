package com.example.taskmanagment.unit;

import com.example.taskmanagment.dto.comment.CommentCreateDto;
import com.example.taskmanagment.dto.comment.CommentReadDto;
import com.example.taskmanagment.mapper.CommentMapper;
import com.example.taskmanagment.models.Comment;
import com.example.taskmanagment.repositories.CommentRepository;
import com.example.taskmanagment.services.CommentService;
import com.example.taskmanagment.validation.validators.TaskValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.taskmanagment.util.test_data.CommentDataUtil.createComment;
import static com.example.taskmanagment.util.test_data.CommentDataUtil.createCommentCreateDto;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    private final Comment comment = createComment();
    private final CommentCreateDto createDto = createCommentCreateDto();
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private TaskValidator taskValidator;
    @InjectMocks
    private CommentService commentService;

    @Test
    void create() {
        doNothing().when(taskValidator).validateIsExist(USER_ID);
        Comment transientComment = createComment();
        transientComment.setId(null);
        doReturn(transientComment).when(commentMapper).toEntity(createDto, EMAIL);
        doReturn(comment).when(commentRepository).save(transientComment);
        doReturn(new CommentReadDto()).when(commentMapper).toDto(comment);

        assertThat(commentService.create(createDto, EMAIL)).isEqualTo(new CommentReadDto());
    }
}
