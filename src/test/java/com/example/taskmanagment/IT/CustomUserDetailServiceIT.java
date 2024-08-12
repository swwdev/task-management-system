package com.example.taskmanagment.IT;


import com.example.taskmanagment.dto.user.CustomUserDetails;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.services.CustomUserDetailService;
import com.example.taskmanagment.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createUser;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
public class CustomUserDetailServiceIT extends IntegrationTestBase {
    private final User userEntity = createUser();
    @Autowired
    private CustomUserDetailService userDetailService;

    @Test
    void loadUserByUserName() {
        assertAll(
                () -> assertThat((userDetailService.loadUserByUsername(EMAIL)))
                        .isEqualTo(new CustomUserDetails(userEntity)),
                () -> assertThrows(
                        UsernameNotFoundException.class,
                        () -> userDetailService.loadUserByUsername(DUMMY),
                        getMessage(NOT_FOUND, USER)
                )
        );
    }
}
