package com.example.taskmanagment.mapper;

import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.TaskRepository;
import com.example.taskmanagment.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;


@Component
@RequiredArgsConstructor
@Transactional
@Named("MapperUtil")
public class MapperUtil {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public static Status getInitialStatus() {
        return Status.pending;
    }

    @Named("getPriority")
    public static Priority getPriority(String priority) {
        return Priority.safeValueOf(priority);
    }

    @Named("encode")
    public String encode(String source) {
        return passwordEncoder.encode(source);
    }

    @Named("getAuthor")
    public User getAuthor(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new DataSourceLookupFailureException(getMessage(NOT_FOUND, USER))
                );
    }

    @Named("getTask")
    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(
                        () -> new DataSourceLookupFailureException(getMessage(NOT_FOUND, TASK))
                );
    }

}
