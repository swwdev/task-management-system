package com.example.taskmanagment.util.test_data;

import com.example.taskmanagment.dto.user.LoginDto;
import com.example.taskmanagment.dto.user.UserCreateDto;
import com.example.taskmanagment.models.User;

public class UserDataUtil {
    public static final Long USER_ID = 1L;
    public static final Long OTHER_ID = 2000L;
    public static final String EMAIL = "user1@example.com";
    public static final String PASSWORD = "password1";

    private UserDataUtil() {

    }

    public static User createUser() {
        return User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    public static UserCreateDto createUserCreateDto() {
        return UserCreateDto.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    public static LoginDto createLoginDto() {
        return LoginDto.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }
}
