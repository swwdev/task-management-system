package com.example.taskmanagment.unit;

import com.example.taskmanagment.exceptions.IllegalIdException;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.TaskRepository;
import com.example.taskmanagment.validation.validators.TaskValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.OTHER_ID;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.TASK_ID;
import static com.example.taskmanagment.util.test_data.TaskDataUtil.createTask;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createUser;
import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TaskValidatorTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskValidator taskValidator;

    @Test
    void validateIsExist() {
        doReturn(Optional.empty()).when(taskRepository).findById(OTHER_ID);
        doReturn(Optional.of(createTask())).when(taskRepository).findById(TASK_ID);

        assertAll(
                () -> assertThrows(
                        IllegalIdException.class,
                        () -> taskValidator.validateIsExist(OTHER_ID),
                        getMessage(NOT_FOUND, TASK)),
                () -> assertThatNoException().isThrownBy(() -> taskValidator.validateIsExist(TASK_ID))
        );
    }

    @Test
    void isAssignee() {
        Task task = createTask();
        Set<User> assignees = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setEmail("email" + i);
            assignees.add(user);
        }
        task.setAssignees(assignees);
        assertAll(
                () -> assertFalse(taskValidator.isAssignee(DUMMY, task)),
                () -> assertTrue(taskValidator.isAssignee("email2", task))
        );
    }

    @Test
    void isAuthor() {
        Task task = createTask();
        User author = createUser();
        task.setAuthor(author);
        assertAll(
                () -> assertFalse(taskValidator.isAuthor(DUMMY, task)),
                () -> assertTrue(taskValidator.isAuthor(author.getEmail(), task))
        );
    }
}
