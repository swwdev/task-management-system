package com.example.taskmanagment.models;

import com.example.taskmanagment.exceptions.InvalidQueryParamsException;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_STATUS;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

public enum Status {
    pending,
    ongoing,
    completed;

    public static Status safeValueOf(String status) {
        if (status == null)
            return null;
        try {
            return Status.valueOf(status);
        } catch (RuntimeException e) {
            throw new InvalidQueryParamsException(getMessage(INVALID_STATUS, status));
        }
    }
}
