package com.example.taskmanagment.services;

import com.example.taskmanagment.dto.filtering.TaskQueryParams;
import com.example.taskmanagment.dto.task.AssignTaskDto;
import com.example.taskmanagment.dto.task.TaskCreatedDto;
import com.example.taskmanagment.dto.task.TaskReadDto;
import com.example.taskmanagment.dto.task.TaskUpdateDto;
import com.example.taskmanagment.exceptions.CantAssignException;
import com.example.taskmanagment.mapper.ExtendedTaskMapper;
import com.example.taskmanagment.mapper.TaskMapper;
import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.TaskRepository;
import com.example.taskmanagment.repositories.UserRepository;
import com.example.taskmanagment.validation.validators.TaskValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.ALREADY_ASSIGNEE;
import static com.example.taskmanagment.utils.ResponseUtils.CANNOT_DELETE_OTHER_TASKS;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.NO_PERMITS_CHANGE_TASK;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final TaskValidator taskValidator;
    private final TaskMapper taskMapper;
    private final ExtendedTaskMapper extendedTaskMapper;

    public TaskReadDto getById(Long id, String include) {
        TaskQueryParams queryParams = TaskQueryParams.safeValueOf(include);

        Function<Task, TaskReadDto> mappingFunc = switch (queryParams) {
            case null -> taskMapper::toDto;
            case author -> extendedTaskMapper::toDtoWithAuthor;
            case assignees -> extendedTaskMapper::toDtoWithAssignees;
            case comments -> extendedTaskMapper::toDtoWithComments;
            case all -> extendedTaskMapper::toDtoWithDetails;
        };
        return taskRepository.findById(id)
                .map(mappingFunc)
                .orElseThrow(() -> new EntityNotFoundException(getMessage(NOT_FOUND, TASK)));
    }


    public TaskReadDto create(TaskCreatedDto createDto, String authorEmail) {
        Task savedTask = taskRepository.save(taskMapper.toEntity(createDto, authorEmail));
        return taskMapper.toDto(savedTask);
    }

    public void changeStatus(Long taskId, String status, String email) {
        Task task = getById(taskId);
        if (!(taskValidator.isAssignee(email, task) || taskValidator.isAuthor(email, task)))
            throw new AccessDeniedException(NO_PERMITS_CHANGE_TASK);

        task.setStatus(Status.safeValueOf(status));
    }

    public void changePriority(Long taskId, String priority, String email) {
        Task task = getById(taskId);
        if (!taskValidator.isAuthor(email, task))
            throw new AccessDeniedException(NO_PERMITS_CHANGE_TASK);

        task.setPriority(Priority.safeValueOf(priority));
    }

    public TaskReadDto update(Long taskId, TaskUpdateDto updateDto, String email) {
        Task task = getById(taskId);
        if (!taskValidator.isAuthor(email, task))
            throw new AccessDeniedException(NO_PERMITS_CHANGE_TASK);

        Task upadatedTask = taskMapper.copyNotNullFields(updateDto, task);
        return taskMapper.toDto(upadatedTask);
    }

    public TaskReadDto assignTask(AssignTaskDto assignTaskDto, Long taskId, String email) {
        Task task = getById(taskId);
        if (!taskValidator.isAuthor(email, task))
            throw new AccessDeniedException(NO_PERMITS_CHANGE_TASK);
        User assignee = userRepository.findByEmail(assignTaskDto.getAssigneeEmail())
                .orElseThrow(() -> new CantAssignException(getMessage(NOT_FOUND, USER)));

        if (taskValidator.isAssignee(assignee.getEmail(), task))
            throw new CantAssignException(ALREADY_ASSIGNEE);

        taskRepository.assignUser(taskId, assignee.getId());
        entityManager.refresh(task);
        return extendedTaskMapper.toDtoWithAssignees(task);
    }

    public void delete(Long id, String email) {
        Optional<Task> deletedTask = taskRepository.findById(id);

        deletedTask.ifPresent(
                (task) -> {
                    if (!task.getAuthor().getEmail().equals(email))
                        throw new AccessDeniedException(CANNOT_DELETE_OTHER_TASKS);
                }
        );
        deletedTask
                .ifPresent(taskRepository::delete);
    }

    private Task getById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException(getMessage(NOT_FOUND, TASK))
        );
    }
}
