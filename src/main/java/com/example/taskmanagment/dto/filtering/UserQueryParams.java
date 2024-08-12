package com.example.taskmanagment.dto.filtering;

import com.example.taskmanagment.exceptions.InvalidQueryParamsException;

import static com.example.taskmanagment.utils.ResponseUtils.INVALID_USER_QUERY_PARAMS;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

public enum UserQueryParams {
    created,
    assigned,
    all;

    public static UserQueryParams safeValueOf(String userQueryParams) {
        if (userQueryParams == null)
            return null;
        try {
            return UserQueryParams.valueOf(userQueryParams);
        } catch (RuntimeException e) {
            throw new InvalidQueryParamsException(getMessage(INVALID_USER_QUERY_PARAMS, userQueryParams));
        }
    }
}
