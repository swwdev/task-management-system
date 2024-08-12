package com.example.taskmanagment.controllers;

import com.example.taskmanagment.dto.filtering.TaskFilter;
import com.example.taskmanagment.dto.response.ExceptionResponse;
import com.example.taskmanagment.dto.response.PageResponse;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.user.UserCreateDto;
import com.example.taskmanagment.dto.user.UserReadDto;
import com.example.taskmanagment.dto.user.UserWithAllTasks;
import com.example.taskmanagment.dto.user.UserWithAssignedTasks;
import com.example.taskmanagment.dto.user.UserWithCreatedTasks;
import com.example.taskmanagment.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.DELETED;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "User", description = "User Management API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @Operation(
            summary = "get user by id",
            description = "get user with child entities using the 'include' query parameter. The response is user with child entities depends on 'include' query parameter.\n" +
                    "The include parameter can take the following values: 'created', 'assigned', 'all'"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(oneOf = {UserReadDto.class, UserWithAllTasks.class, UserWithAssignedTasks.class, UserWithCreatedTasks.class}), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @GetMapping("/{id}")
    public ResponseEntity<UserReadDto> get(@PathVariable Long id, @RequestParam(required = false) String include) {
        return ResponseEntity.ok().body(userService.getById(id, include));
    }


    @Operation(
            summary = "get created tasks of user",
            description = "get filtering created tasks in paginated view. The response is list of created tasks with metadata"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PageResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @GetMapping("/with-created/{id}")
    public ResponseEntity<PageResponse<TaskWithComments>> getCreatedTasks(@PathVariable Long id, TaskFilter filter, Pageable pageable) {
        return ResponseEntity.ok().body(
                PageResponse.of(userService.getPaginatedCreatedTasks(id, filter, pageable))
        );
    }


    @Operation(
            summary = "get assigned tasks of user",
            description = "get filtering assigned tasks in paginated view. The response is list of assigned tasks with metadata"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PageResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @GetMapping("/with-assigned/{id}")
    public ResponseEntity<PageResponse<TaskWithComments>> getAssignedTasks(@PathVariable Long id, TaskFilter filter, Pageable pageable) {
        return ResponseEntity.ok().body(
                PageResponse.of(userService.getPaginatedAssignedTasks(id, filter, pageable))
        );
    }


    @Operation(
            summary = "create user",
            description = "create user. The response is created user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = UserReadDto.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping
    public ResponseEntity<UserReadDto> create(@RequestBody @Valid UserCreateDto createDto) {
        return new ResponseEntity<>(userService.create(createDto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "delete authenticated user",
            description = "delete authenticated user. The response is string about success delete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = PageResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @DeleteMapping()
    public ResponseEntity<String> delete(Principal principal) {
        userService.delete(principal.getName());
        return ResponseEntity.ok().body(getMessage(DELETED, USER));
    }
}
