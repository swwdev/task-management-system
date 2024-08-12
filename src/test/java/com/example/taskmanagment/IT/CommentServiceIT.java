package com.example.taskmanagment.IT;

import com.example.taskmanagment.dto.comment.CommentCreateDto;
import com.example.taskmanagment.exceptions.IllegalIdException;
import com.example.taskmanagment.services.CommentService;
import com.example.taskmanagment.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.CommentDataUtil.COMMENT_TEXT;
import static com.example.taskmanagment.util.test_data.CommentDataUtil.createCommentCreateDto;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.OTHER_ID;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class CommentServiceIT extends IntegrationTestBase {

    @Autowired
    private CommentService commentService;

    @Test
    void create() {
        CommentCreateDto createDtoWithWrongTaskId = createCommentCreateDto();
        createDtoWithWrongTaskId.setTaskId(OTHER_ID);
        CommentCreateDto rightCreateDto = createCommentCreateDto();
        assertAll(
                () -> assertThrows(
                        IllegalIdException.class,
                        () -> commentService.create(createDtoWithWrongTaskId, EMAIL),
                        getMessage(NOT_FOUND, TASK)
                ),
                () -> assertThrows(
                        DataSourceLookupFailureException.class,
                        () -> commentService.create(rightCreateDto, DUMMY),
                        getMessage(NOT_FOUND, USER)
                ),
                () -> assertThat(commentService.create(rightCreateDto, EMAIL).getText()).isEqualTo(COMMENT_TEXT)
        );
    }
}
