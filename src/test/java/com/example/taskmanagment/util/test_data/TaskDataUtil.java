package com.example.taskmanagment.util.test_data;

import com.example.taskmanagment.dto.task.TaskCreatedDto;
import com.example.taskmanagment.dto.task.TaskUpdateDto;
import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import com.example.taskmanagment.models.Task;

public class TaskDataUtil {
    public static final String HEADER = "Task 1";
    public static final String DESCRIPTION = "Description for task 1";
    public static final Status STATUS = Status.pending;
    public static final Priority PRIORITY = Priority.low;
    public static final Long TASK_ID = 1L;
    public static final Long OTHER_ID = 1000L;

    private TaskDataUtil() {

    }

    public static Task createTask() {
        return Task.builder()
                .id(TASK_ID)
                .header(HEADER)
                .description(DESCRIPTION)
                .status(STATUS)
                .priority(PRIORITY)
                .build();
    }

    public static TaskCreatedDto createTaskCreatedDto() {
        return TaskCreatedDto.builder()
                .header(HEADER)
                .description(DESCRIPTION)
                .priority(PRIORITY.name())
                .build();

    }

    public static TaskUpdateDto createTaskupdateDto() {
        return TaskUpdateDto.builder()
                .header(HEADER + "1")
                .description(DESCRIPTION + "1")
                .build();
    }
}
