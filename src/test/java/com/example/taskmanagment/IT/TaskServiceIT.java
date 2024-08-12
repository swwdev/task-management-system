package com.example.taskmanagment.IT;

import com.example.taskmanagment.dto.filtering.TaskQueryParams;
import com.example.taskmanagment.dto.task.AssignTaskDto;
import com.example.taskmanagment.dto.task.TaskCreatedDto;
import com.example.taskmanagment.dto.task.TaskUpdateDto;
import com.example.taskmanagment.dto.task.TaskWithAssignees;
import com.example.taskmanagment.dto.task.TaskWithDetails;
import com.example.taskmanagment.exceptions.CantAssignException;
import com.example.taskmanagment.exceptions.InvalidQueryParamsException;
import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import com.example.taskmanagment.repositories.TaskRepository;
import com.example.taskmanagment.services.TaskService;
import com.example.taskmanagment.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.HEADER;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.PRIORITY;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.TASK_ID;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.createTaskupdateDto;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.ALREADY_ASSIGNEE;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PRIORITY;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class TaskServiceIT extends IntegrationTestBase {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void getById() {
        TaskWithDetails taskWithDetails = (TaskWithDetails) taskService.getById(TASK_ID, TaskQueryParams.all.name());

        assertAll(
                () -> assertThat(taskWithDetails.getAuthor().getEmail()).isEqualTo(EMAIL),
                () -> assertThat(taskWithDetails.getAssignees()).hasSize(1),
                () -> assertThat(taskWithDetails.getComments()).hasSize(2)
        );
    }

    @Test
    void create() {
        TaskCreatedDto rightCreatedDto = new TaskCreatedDto("header", "description", PRIORITY.name());
        TaskCreatedDto wrongCreatedDto = new TaskCreatedDto("header", "description", "dummy");

        assertAll(
                () -> assertThrows(
                        DataSourceLookupFailureException.class,
                        () -> taskService.create(rightCreatedDto, DUMMY),
                        getMessage(NOT_FOUND, USER)),
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> taskService.create(wrongCreatedDto, EMAIL),
                        getMessage(INVALID_PRIORITY, DUMMY)),
                () -> {
                    Long id = taskService.create(rightCreatedDto, EMAIL).getId();
                    assertThat(taskRepository.findById(id)).isPresent();
                }
        );
    }

    @Test
    void changeStatus() {
        taskService.changeStatus(TASK_ID, Status.completed.name(), EMAIL);
        assertThat(taskRepository.findById(TASK_ID).get().getStatus()).isEqualTo(Status.completed);
    }

    @Test
    void changePriority() {
        taskService.changePriority(TASK_ID, Priority.medium.name(), EMAIL);
        assertThat(taskRepository.findById(TASK_ID).get().getPriority()).isEqualTo(Priority.medium);
    }

    @Test
    void updateTask() {
        TaskUpdateDto updateDto = createTaskupdateDto();
        assertAll(
                () -> assertThat(taskService.update(TASK_ID, updateDto, EMAIL).getHeader()).isEqualTo(HEADER + "1"),
                () -> assertThat(taskRepository.findById(TASK_ID).get().getHeader()).isEqualTo(HEADER + "1")
        );
    }

    @Test
    void assignTask() {
        TaskWithAssignees taskWithNewAssigner =
                (TaskWithAssignees) taskService.assignTask(new AssignTaskDto(EMAIL), TASK_ID, EMAIL);
        assertAll(
                () -> assertThat(taskWithNewAssigner.getAssignees()).hasSize(2),
                () -> assertThrows(
                        CantAssignException.class,
                        () -> taskService.assignTask(new AssignTaskDto(EMAIL), TASK_ID, EMAIL),
                        ALREADY_ASSIGNEE
                )
        );
    }

    @Test
    void delete() {
        taskService.delete(TASK_ID, EMAIL);
        assertThat(taskRepository.findById(TASK_ID)).isEmpty();
    }
}
