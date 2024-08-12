package com.example.taskmanagment.validation.validators;

import com.example.taskmanagment.exceptions.IllegalIdException;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

@Component
@Transactional
@RequiredArgsConstructor
public class TaskValidator {

    private final TaskRepository taskRepository;

    public void validateIsExist(Long id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new IllegalIdException(getMessage(NOT_FOUND, TASK));
        }
    }

    public boolean isAssignee(String assigneeEmail, Task task) {
        return task.getAssignees().stream()
                .map(User::getEmail)
                .anyMatch(assigneeEmail::equals);
    }

    public boolean isAuthor(String assigneeEmail, Task task) {
        return task.getAuthor().getEmail().equals(assigneeEmail);
    }

}
