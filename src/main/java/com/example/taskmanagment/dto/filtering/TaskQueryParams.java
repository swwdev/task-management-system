package com.example.taskmanagment.dto.filtering;

import com.example.taskmanagment.exceptions.InvalidQueryParamsException;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_TASK_QUERY_PARAMS;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

public enum TaskQueryParams {
    author,
    assignees,
    comments,
    all;


    public static TaskQueryParams safeValueOf(String taskQueryParams) {
        if (taskQueryParams == null)
            return null;
        try {
            return TaskQueryParams.valueOf(taskQueryParams);
        } catch (RuntimeException e) {
            throw new InvalidQueryParamsException(getMessage(INVALID_TASK_QUERY_PARAMS, taskQueryParams));
        }
    }
}
