package com.example.taskmanagment.services;

import com.example.taskmanagment.dto.filtering.TaskFilter;
import com.example.taskmanagment.dto.filtering.UserQueryParams;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.user.UserCreateDto;
import com.example.taskmanagment.dto.user.UserReadDto;
import com.example.taskmanagment.mapper.ExtendedTaskMapper;
import com.example.taskmanagment.mapper.ExtendedUserMapper;
import com.example.taskmanagment.mapper.UserMapper;
import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.TaskRepository;
import com.example.taskmanagment.repositories.UserRepository;
import com.example.taskmanagment.validation.validators.WellFormattedInputValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final WellFormattedInputValidator inputValidator;
    private final UserMapper userMapper;
    private final ExtendedTaskMapper taskMapper;
    private final ExtendedUserMapper extendedUserMapper;

    public UserReadDto getById(Long id, String include) {
        UserQueryParams queryParams = UserQueryParams.safeValueOf(include);

        Function<User, UserReadDto> mappingFunc = switch (queryParams) {
            case null -> userMapper::toDto;
            case created -> extendedUserMapper::toDtoWithCreatedTasks;
            case assigned -> extendedUserMapper::toDtoWithAssignedTasks;
            case all -> extendedUserMapper::toDtoWitAllTasks;
        };
        return userRepository.findById(id)
                .map(mappingFunc)
                .orElseThrow(() -> new EntityNotFoundException(getMessage(NOT_FOUND, USER)));
    }

    public Page<TaskWithComments> getPaginatedCreatedTasks(Long authorId, TaskFilter filter, Pageable pageable) {
        return getPaginatedTasks(
                authorId,
                filter,
                pageable,
                (id, f, s, pr) -> taskRepository.findAllCreatedTasksBy(id, f.getHeader(), s, pr, pageable)
        );
    }

    public Page<TaskWithComments> getPaginatedAssignedTasks(Long assigneeId, TaskFilter filter, Pageable pageable) {
        return getPaginatedTasks(
                assigneeId,
                filter,
                pageable,
                (id, f, s, pr) -> taskRepository.findAllAssignedTasksBy(id, f.getHeader(), s, pr, pageable)
        );
    }

    public UserReadDto create(UserCreateDto createDto) {
        User savedUser = userRepository.save(userMapper.toEntity(createDto));
        return userMapper.toDto(savedUser);
    }

    public void delete(String email) {
        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);
    }

    private Page<TaskWithComments> getPaginatedTasks(
            Long userId,
            TaskFilter filter,
            Pageable pageable,
            TaskFetcher taskFetcher
    ) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(getMessage(NOT_FOUND, USER)));

        inputValidator.validateTaskPageable(pageable);

        Priority priority = Priority.safeValueOf(filter.getPriority());
        Status status = Status.safeValueOf(filter.getStatus());

        return taskFetcher.fetchTasks(userId, filter, status, priority)
                .map(taskMapper::toDtoWithComments);
    }

    @FunctionalInterface
    private interface TaskFetcher {
        Page<Task> fetchTasks(Long userId, TaskFilter filter, Status status, Priority priority);
    }

}
