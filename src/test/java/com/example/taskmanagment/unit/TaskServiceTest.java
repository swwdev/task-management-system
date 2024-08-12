package com.example.taskmanagment.unit;

import com.example.taskmanagment.dto.task.AssignTaskDto;
import com.example.taskmanagment.dto.task.TaskCreatedDto;
import com.example.taskmanagment.dto.task.TaskReadDto;
import com.example.taskmanagment.dto.task.TaskUpdateDto;
import com.example.taskmanagment.dto.task.TaskWithAssignees;
import com.example.taskmanagment.dto.task.TaskWithAuthor;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.task.TaskWithDetails;
import com.example.taskmanagment.exceptions.CantAssignException;
import com.example.taskmanagment.exceptions.InvalidQueryParamsException;
import com.example.taskmanagment.mapper.ExtendedTaskMapper;
import com.example.taskmanagment.mapper.TaskMapper;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.TaskRepository;
import com.example.taskmanagment.repositories.UserRepository;
import com.example.taskmanagment.services.TaskService;
import com.example.taskmanagment.validation.validators.TaskValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.OTHER_ID;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.PRIORITY;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.STATUS;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.TASK_ID;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.createTask;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.createTaskCreatedDto;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.createTaskupdateDto;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.USER_ID;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createUser;
import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.ALREADY_ASSIGNEE;
import static com.example.taskmanagment.utils.ResponseUtils.CANNOT_DELETE_OTHER_TASKS;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PRIORITY;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_STATUS;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_TASK_QUERY_PARAMS;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.NO_PERMITS_CHANGE_TASK;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mock.Strictness.LENIENT;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    public static final String INCLUDE_AUTHOR = "author";
    public static final String INCLUDE_ASSIGNEES = "assignees";
    public static final String INCLUDE_COMMENTS = "comments";
    public static final String INCLUDE_ALL = "all";
    private final Task task = createTask();
    private final TaskCreatedDto createDto = createTaskCreatedDto();
    private final TaskUpdateDto updateDto = createTaskupdateDto();
    @Mock(strictness = LENIENT)
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private TaskValidator taskValidator;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private ExtendedTaskMapper extendedTaskMapper;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        doReturn(Optional.empty()).when(taskRepository).findById(OTHER_ID);
        doReturn(Optional.of(task)).when(taskRepository).findById(TASK_ID);
    }

    @Test
    void failedGetById() {
        assertAll(
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> taskService.getById(TASK_ID, DUMMY),
                        getMessage(INVALID_TASK_QUERY_PARAMS, DUMMY)),
                () -> assertThrows(
                        EntityNotFoundException.class,
                        () -> taskService.getById(OTHER_ID, null),
                        getMessage(NOT_FOUND, TASK))
        );
    }

    @Test
    void successGetById() {
        doReturn(new TaskReadDto()).when(taskMapper).toDto(task);
        doReturn(new TaskWithAssignees()).when(extendedTaskMapper).toDtoWithAssignees(task);
        doReturn(new TaskWithAuthor()).when(extendedTaskMapper).toDtoWithAuthor(task);
        doReturn(new TaskWithComments()).when(extendedTaskMapper).toDtoWithComments(task);
        doReturn(new TaskWithDetails()).when(extendedTaskMapper).toDtoWithDetails(task);

        assertAll(
                () -> assertThat(taskService.getById(TASK_ID, null)).isInstanceOf(TaskReadDto.class),
                () -> assertThat(taskService.getById(TASK_ID, INCLUDE_AUTHOR)).isInstanceOf(TaskWithAuthor.class),
                () -> assertThat(taskService.getById(TASK_ID, INCLUDE_ASSIGNEES)).isInstanceOf(TaskWithAssignees.class),
                () -> assertThat(taskService.getById(TASK_ID, INCLUDE_COMMENTS)).isInstanceOf(TaskWithComments.class),
                () -> assertThat(taskService.getById(TASK_ID, INCLUDE_ALL)).isInstanceOf(TaskWithDetails.class)
        );
    }

    @Test
    void create() {
        Task transientTask = createTask();
        transientTask.setId(null);
        doReturn(transientTask).when(taskMapper).toEntity(createDto, EMAIL);
        doReturn(task).when(taskRepository).save(transientTask);
        doReturn(new TaskReadDto()).when(taskMapper).toDto(task);

        assertThat(taskService.create(createDto, EMAIL)).isEqualTo(new TaskReadDto());
        verify(taskRepository).save(transientTask);
    }

    @Test
    void failedChangeStatus() {
        doReturn(false).when(taskValidator).isAuthor(EMAIL, task);
        doReturn(false).when(taskValidator).isAssignee(EMAIL, task);
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.changeStatus(TASK_ID, STATUS.name(), EMAIL),
                NO_PERMITS_CHANGE_TASK);

        doReturn(true).when(taskValidator).isAssignee(EMAIL, task);
        assertThrows(
                InvalidQueryParamsException.class,
                () -> taskService.changeStatus(TASK_ID, DUMMY, EMAIL),
                getMessage(INVALID_STATUS, DUMMY));
    }

    @Test
    void successChangeStatus() {
        doReturn(true).when(taskValidator).isAssignee(EMAIL, task);
        assertThatNoException().isThrownBy(() -> taskService.changeStatus(TASK_ID, STATUS.name(), EMAIL));
    }

    @Test
    void failedChangePriority() {
        doReturn(false).when(taskValidator).isAuthor(EMAIL, task);
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.changePriority(TASK_ID, PRIORITY.name(), EMAIL),
                NO_PERMITS_CHANGE_TASK);

        doReturn(true).when(taskValidator).isAuthor(EMAIL, task);
        assertThrows(
                InvalidQueryParamsException.class,
                () -> taskService.changePriority(TASK_ID, DUMMY, EMAIL),
                getMessage(INVALID_PRIORITY, DUMMY));
    }

    @Test
    void successChangePriority() {
        doReturn(true).when(taskValidator).isAuthor(EMAIL, task);
        assertThatNoException().isThrownBy(() -> taskService.changePriority(TASK_ID, PRIORITY.name(), EMAIL));
    }

    @Test
    void failedUpdate() {
        doReturn(false).when(taskValidator).isAuthor(EMAIL, task);
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.update(TASK_ID, updateDto, EMAIL),
                NO_PERMITS_CHANGE_TASK);
    }

    @Test
    void successUpdate() {
        doReturn(true).when(taskValidator).isAuthor(EMAIL, task);
        doReturn(task).when(taskMapper).copyNotNullFields(updateDto, task);
        doReturn(new TaskReadDto()).when(taskMapper).toDto(task);

        assertThat(taskService.update(TASK_ID, updateDto, EMAIL)).isEqualTo(new TaskReadDto());
    }

    @Test
    void failedAssignTask() {
        doReturn(false).when(taskValidator).isAuthor(EMAIL, task);
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.assignTask(new AssignTaskDto(EMAIL), TASK_ID, EMAIL),
                NO_PERMITS_CHANGE_TASK);

        doReturn(true).when(taskValidator).isAuthor(EMAIL, task);
        doReturn(Optional.empty()).when(userRepository).findByEmail(EMAIL);
        assertThrows(
                CantAssignException.class,
                () -> taskService.assignTask(new AssignTaskDto(EMAIL), TASK_ID, EMAIL),
                getMessage(NOT_FOUND, USER));

        doReturn(Optional.of(createUser())).when(userRepository).findByEmail(EMAIL);
        doReturn(true).when(taskValidator).isAssignee(EMAIL, task);
        assertThrows(
                CantAssignException.class,
                () -> taskService.assignTask(new AssignTaskDto(EMAIL), TASK_ID, EMAIL),
                ALREADY_ASSIGNEE);
    }

    @Test
    void successAssignTask() {
        doReturn(true).when(taskValidator).isAuthor(EMAIL, task);
        doReturn(Optional.of(createUser())).when(userRepository).findByEmail(EMAIL);
        doReturn(false).when(taskValidator).isAssignee(EMAIL, task);
        doNothing().when(entityManager).refresh(any());
        doNothing().when(taskRepository).assignUser(TASK_ID, USER_ID);
        doReturn(new TaskWithAssignees()).when(extendedTaskMapper).toDtoWithAssignees(task);

        assertThat(taskService.assignTask(new AssignTaskDto(EMAIL), TASK_ID, EMAIL)).isEqualTo(new TaskWithAssignees());
    }

    @Test
    void failedDelete() {
        User someoneElseAuthor = createUser();
        someoneElseAuthor.setEmail(DUMMY);
        task.setAuthor(someoneElseAuthor);
        assertThrows(
                AccessDeniedException.class,
                () -> taskService.delete(TASK_ID, EMAIL),
                CANNOT_DELETE_OTHER_TASKS);
    }

    @Test
    void successDelete() {
        task.setAuthor(createUser());

        assertAll(
                () -> assertThatNoException().isThrownBy(() -> taskService.delete(TASK_ID, EMAIL)),
                () -> assertThatNoException().isThrownBy(() -> taskService.delete(OTHER_ID, EMAIL))
        );
        verify(taskRepository, times(1)).delete(any());
    }
}
