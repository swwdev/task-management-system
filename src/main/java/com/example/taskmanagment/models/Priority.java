package com.example.taskmanagment.models;

import com.example.taskmanagment.exceptions.InvalidQueryParamsException;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PRIORITY;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

public enum Priority {
    low,
    medium,
    high;

    public static Priority safeValueOf(String priority) {
        if (priority == null)
            return null;
        try {
            return Priority.valueOf(priority);
        } catch (RuntimeException e) {
            throw new InvalidQueryParamsException(getMessage(INVALID_PRIORITY, priority));
        }
    }

}
