package com.example.taskmanagment.controllers;

import com.example.taskmanagment.dto.comment.CommentCreateDto;
import com.example.taskmanagment.dto.comment.CommentReadDto;
import com.example.taskmanagment.dto.response.ExceptionResponse;
import com.example.taskmanagment.dto.task.AssignTaskDto;
import com.example.taskmanagment.dto.task.TaskCreatedDto;
import com.example.taskmanagment.dto.task.TaskReadDto;
import com.example.taskmanagment.dto.task.TaskUpdateDto;
import com.example.taskmanagment.dto.task.TaskWithAssignees;
import com.example.taskmanagment.dto.task.TaskWithAuthor;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.task.TaskWithDetails;
import com.example.taskmanagment.services.CommentService;
import com.example.taskmanagment.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.example.taskmanagment.utils.Constants.TASK;
import static com.example.taskmanagment.utils.ResponseUtils.DELETED;
import static com.example.taskmanagment.utils.ResponseUtils.SUCCESSFULLY_UPDATED;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Tag(name = "Task", description = "Tasks management API")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    @Operation(
            summary = "get task by id",
            description = "get task with child entities using the 'include' query parameter. The response is task with child entities depends on 'include' query parameter.\n" +
                    "The include parameter can take the following values: 'author', 'assignees', 'comments', 'all'"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(oneOf = {TaskReadDto.class, TaskWithAssignees.class, TaskWithAuthor.class, TaskWithComments.class, TaskWithDetails.class}), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @GetMapping("/{id}")
    public ResponseEntity<TaskReadDto> get(@PathVariable Long id, @RequestParam(required = false) String include) {
        return ResponseEntity.ok().body(taskService.getById(id, include));
    }


    @Operation(
            summary = "create task",
            description = "create task. The response is created task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = TaskReadDto.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping
    public ResponseEntity<TaskReadDto> create(@RequestBody @Valid TaskCreatedDto createDto, Principal principal) {
        return new ResponseEntity<>(taskService.create(createDto, principal.getName()), HttpStatus.CREATED);
    }


    @Operation(
            summary = "create comment to task",
            description = "create comment. The response is created comment"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = CommentReadDto.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping("/comment")
    public ResponseEntity<CommentReadDto> createComment(@RequestBody @Valid CommentCreateDto createDto, Principal principal) {
        return new ResponseEntity<>(commentService.create(createDto, principal.getName()), HttpStatus.CREATED);
    }


    @Operation(
            summary = "change status of task",
            description = "change status. The response is string about success changes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = TEXT_PLAIN_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PatchMapping("/status/{id}")
    public String changeStatus(@PathVariable Long id, @RequestBody String status, Principal principal) {
        taskService.changeStatus(id, status, principal.getName());
        return SUCCESSFULLY_UPDATED;
    }


    @Operation(
            summary = "change priority of task",
            description = "change priority. The response is string about success changes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = TEXT_PLAIN_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PatchMapping("/priority/{id}")
    public String changePriority(@PathVariable Long id, @RequestBody String priority, Principal principal) {
        taskService.changePriority(id, priority, principal.getName());
        return SUCCESSFULLY_UPDATED;
    }


    @Operation(
            summary = "update task",
            description = "set new description and header of task. The response is updated task"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = TaskReadDto.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PatchMapping("/{id}")
    public ResponseEntity<TaskReadDto> updateTask(@RequestBody TaskUpdateDto updateDto, @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok().body(taskService.update(id, updateDto, principal.getName()));
    }

    @Operation(
            summary = "assign task to user",
            description = "assign task to user. The response is task with all assignees"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = TaskWithAssignees.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping("/assign/{id}")
    public ResponseEntity<TaskReadDto> assignTask(@RequestBody @Valid AssignTaskDto assignTaskDto, @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok().body(taskService.assignTask(assignTaskDto, id, principal.getName()));
    }


    @Operation(
            summary = "delete task",
            description = "delete tak. The response is string about success delete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = String.class), mediaType = TEXT_PLAIN_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Principal principal) {
        taskService.delete(id, principal.getName());
        return ResponseEntity.ok().body(getMessage(DELETED, TASK));
    }
}
