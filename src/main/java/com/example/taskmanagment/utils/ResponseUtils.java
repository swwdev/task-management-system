package com.example.taskmanagment.utils;

public class ResponseUtils {
    public static final String NOT_FOUND = "%s not found";
    public static final String DELETED = "%s deleted";
    public static final String LOGOUT = "logout successfully";
    public static final String TOKEN_IS_COMPROMISED = "The token has been compromised, please log into your account again using your credentials.";
    public static final String INVALID_FINGER_PRINT = "invalid finger print";
    public static final String INVALID_REFRESH_TOKEN = "need well formatted refresh token to do refresh";
    public static final String NEED_HEADER = "task header must be fill in";
    public static final String NEED_DESCRIPTION = "description of task must be fill in";
    public static final String NEED_PRIORITY = "priority of task must be fill in";
    public static final String EMAIL_DUPLICATE = "email already exist";
    public static final String NEED_EMAIL = "email must be fill in";
    public static final String NEED_PASSWORD = "password must be fill in";
    public static final String NEED_TASK_ID = "need task id";
    public static final String NEED_TEXT = "need not blank text of comment";
    public static final String SUCCESSFULLY_UPDATED = "successfully updated";
    public static final String NO_PERMITS_CHANGE_TASK = "you have no permits to change this task";
    public static final String ALREADY_ASSIGNEE = "this user is already assignee";
    public static final String INVALID_PAGEABLE = "Sorting is available only by 'header', 'status' and 'priority'";
    public static final String INVALID_STATUS = "can not parse '%s'. available constants for status: pending, ongoing, completed";
    public static final String INVALID_PRIORITY = "can not parse '%s'. available constants for priority: high, medium, low";
    public static final String INVALID_TASK_QUERY_PARAMS = "can not parse '%s'. available constants for task query params: author, assignees, comments, all";
    public static final String INVALID_USER_QUERY_PARAMS = "can not parse '%s'. available constants for user query params: created, assigned, all";
    public static final String CANNOT_DELETE_OTHER_TASKS = "you cannot delete other peoples tasks";
    public static final String WEAK_PASSWORD = "your password is weak";

    private ResponseUtils() {

    }

    public static String getMessage(String message, String entity) {
        return String.format(message, entity);
    }
}
