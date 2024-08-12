package com.example.taskmanagment.IT;

import com.example.taskmanagment.dto.filtering.TaskFilter;
import com.example.taskmanagment.dto.filtering.UserQueryParams;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.user.UserCreateDto;
import com.example.taskmanagment.dto.user.UserWithAllTasks;
import com.example.taskmanagment.repositories.UserRepository;
import com.example.taskmanagment.services.UserService;
import com.example.taskmanagment.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
public class UserServiceIT extends IntegrationTestBase {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getById() {
        UserWithAllTasks userWithDetails = (UserWithAllTasks) userService.getById(USER_ID, UserQueryParams.all.name());
        assertAll(
                () -> assertThat(userWithDetails.getCreatedTasks()).hasSize(1),
                () -> assertThat(userWithDetails.getAssignedTasks()).hasSize(2)
        );
    }

    @Test
    void getPaginatedCreatedTasks() {
        Page<TaskWithComments> result = userService.getPaginatedCreatedTasks(
                USER_ID,
                new TaskFilter(null, null, null),
                PageRequest.of(0, 3));
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.getContent().getFirst().getComments()).hasSize(2)
        );
    }

    @Test
    void getPaginatedAssignedTasks() {
        Page<TaskWithComments> result = userService.getPaginatedAssignedTasks(
                USER_ID,
                new TaskFilter(null, null, null),
                PageRequest.of(0, 3));
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.getContent().getFirst().getId()).isEqualTo(2),
                () -> assertThat(result.getContent().getFirst().getComments()).hasSize(1)
        );
    }

    @Test
    void create() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail(DUMMY);
        createDto.setPassword(DUMMY);
        userService.create(createDto);
        assertThat(userRepository.findByEmail(DUMMY)).isPresent();
    }

    @Test
    void delete() {
        userService.delete(EMAIL);
        assertThat(userRepository.count()).isEqualTo(2);
    }
}

