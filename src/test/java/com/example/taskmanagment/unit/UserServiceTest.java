package com.example.taskmanagment.unit;

import com.example.taskmanagment.dto.filtering.TaskFilter;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.user.UserCreateDto;
import com.example.taskmanagment.dto.user.UserReadDto;
import com.example.taskmanagment.dto.user.UserWithAllTasks;
import com.example.taskmanagment.dto.user.UserWithAssignedTasks;
import com.example.taskmanagment.dto.user.UserWithCreatedTasks;
import com.example.taskmanagment.exceptions.InvalidQueryParamsException;
import com.example.taskmanagment.mapper.ExtendedTaskMapper;
import com.example.taskmanagment.mapper.ExtendedUserMapper;
import com.example.taskmanagment.mapper.UserMapper;
import com.example.taskmanagment.models.Task;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.UserRepository;
import com.example.taskmanagment.services.UserService;
import com.example.taskmanagment.validation.validators.WellFormattedInputValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.taskmanagment.util.DataUtil.DUMMY;
import static com.example.taskmanagment.util.test_data.UserDataUtil.EMAIL;
import static com.example.taskmanagment.util.test_data.UserDataUtil.OTHER_ID;
import static com.example.taskmanagment.util.test_data.UserDataUtil.USER_ID;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createUser;
import static com.example.taskmanagment.util.test_data.UserDataUtil.createUserCreateDto;
import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PAGEABLE;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_PRIORITY;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_STATUS;
import static com.example.taskmanagment.utils.ResponseUtils.INVALID_USER_QUERY_PARAMS;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String INCLUDE_CREATED = "created";
    private static final String INCLUDE_ASSIGNED = "assigned";
    private static final String INCLUDE_ALL = "all";
    private final User user = createUser();
    private final UserCreateDto createDto = createUserCreateDto();
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Spy
    private WellFormattedInputValidator inputValidator;
    @Mock
    private ExtendedTaskMapper extendedTaskMapper;
    @Mock
    private ExtendedUserMapper extendedUserMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void failedGetById() {
        doReturn(Optional.empty()).when(userRepository).findById(OTHER_ID);
        assertAll(
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> userService.getById(USER_ID, DUMMY),
                        getMessage(INVALID_USER_QUERY_PARAMS, DUMMY)),
                () -> assertThrows(
                        EntityNotFoundException.class,
                        () -> userService.getById(OTHER_ID, null),
                        getMessage(NOT_FOUND, USER))
        );
    }

    @Test
    void successGetById() {
        doReturn(Optional.of(user)).when(userRepository).findById(USER_ID);

        doReturn(new UserReadDto()).when(userMapper).toDto(user);
        doReturn(new UserWithCreatedTasks()).when(extendedUserMapper).toDtoWithCreatedTasks(user);
        doReturn(new UserWithAssignedTasks()).when(extendedUserMapper).toDtoWithAssignedTasks(user);
        doReturn(new UserWithAllTasks()).when(extendedUserMapper).toDtoWitAllTasks(user);

        assertAll(
                () -> assertThat(userService.getById(USER_ID, null)).isInstanceOf(UserReadDto.class),
                () -> assertThat(userService.getById(USER_ID, INCLUDE_CREATED)).isInstanceOf(UserWithCreatedTasks.class),
                () -> assertThat(userService.getById(USER_ID, INCLUDE_ASSIGNED)).isInstanceOf(UserWithAssignedTasks.class),
                () -> assertThat(userService.getById(USER_ID, INCLUDE_ALL)).isInstanceOf(UserWithAllTasks.class)
        );
    }

    @Test
    void create() {
        User transientUser = createUser();
        transientUser.setId(null);
        doReturn(transientUser).when(userMapper).toEntity(createDto);
        doReturn(user).when(userRepository).save(transientUser);
        doReturn(new UserReadDto()).when(userMapper).toDto(user);

        assertThat(userService.create(createDto)).isEqualTo(new UserReadDto());
        verify(userRepository).save(transientUser);
    }

    @Test
    void delete() {
        doReturn(Optional.of(user)).when(userRepository).findByEmail(EMAIL);

        assertAll(
                () -> assertThatNoException().isThrownBy(() -> userService.delete(EMAIL)),
                () -> assertThatNoException().isThrownBy(() -> userService.delete(anyString()))
        );
        verify(userRepository, times(1)).delete(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getPaginatedTasks() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        doReturn(Optional.of(user)).when(userRepository).findById(USER_ID);
        doReturn(new TaskWithComments()).when(extendedTaskMapper).toDtoWithComments(any());

        TaskFilter rightFilter = new TaskFilter("header", "pending", "low");
        Pageable pageable = Pageable.unpaged();
        Class<?> taskFetcherClass = Arrays.stream(UserService.class.getDeclaredClasses())
                .findFirst()
                .orElseThrow(RuntimeException::new);

        Object taskFetcher = Proxy.newProxyInstance(
                taskFetcherClass.getClassLoader(),
                new Class<?>[]{taskFetcherClass},
                (object, method, args) -> {
                    if (method.getName().equals("fetchTasks")) {
                        return new PageImpl<>(List.of(new Task()));
                    } else
                        return method.invoke(object, args);
                }
        );

        Method getPaginatedTasks = UserService.class
                .getDeclaredMethod(
                        "getPaginatedTasks",
                        Long.class,
                        TaskFilter.class,
                        Pageable.class,
                        taskFetcherClass);
        getPaginatedTasks.setAccessible(true);

        Page<TaskWithComments> successResult = (Page<TaskWithComments>) getPaginatedTasks
                .invoke(userService, USER_ID, rightFilter, pageable, taskFetcher);

        //success invocation
        assertAll(
                () -> assertThat(successResult.getContent()).isNotNull(),
                () -> assertThat(successResult.getTotalElements()).isEqualTo(1),
                () -> assertThat(successResult.getContent().getFirst()).isInstanceOf(TaskWithComments.class)
        );

        TaskFilter wrongStatusFilter = new TaskFilter("header", DUMMY, "high");
        TaskFilter wrongPriorityFilter = new TaskFilter("header", "ongoing", DUMMY);
        //failed invocation
        assertAll(
                // EntityNotFoundException with wrong userId
                () -> assertThrows(
                        EntityNotFoundException.class,
                        () -> {
                            try {
                                getPaginatedTasks.invoke(userService, OTHER_ID, rightFilter, pageable, taskFetcher);
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        },
                        getMessage(NOT_FOUND, USER)
                ),

                // InvalidQueryParamsException with wrong status
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> {
                            try {
                                getPaginatedTasks.invoke(userService, USER_ID, wrongStatusFilter, pageable, taskFetcher);
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        },
                        getMessage(INVALID_STATUS, DUMMY)
                ),

                // InvalidQueryParamsException with wrong priority
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> {
                            try {
                                getPaginatedTasks.invoke(userService, USER_ID, wrongPriorityFilter, pageable, taskFetcher);
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        },
                        getMessage(INVALID_PRIORITY, DUMMY)
                ),

                // InvalidQueryParamsException with wrong pageable
                () -> assertThrows(
                        InvalidQueryParamsException.class,
                        () -> {
                            try {
                                getPaginatedTasks.invoke(userService, USER_ID, rightFilter,
                                        PageRequest.of(1, 1, Sort.by(DUMMY)), taskFetcher);
                            } catch (InvocationTargetException e) {
                                throw e.getCause();
                            }
                        },
                        INVALID_PAGEABLE
                )
        );
    }
}
