package com.example.taskmanagment.controllers;

import com.example.taskmanagment.dto.response.ExceptionResponse;
import com.example.taskmanagment.dto.response.JwtResponse;
import com.example.taskmanagment.dto.user.LoginDto;
import com.example.taskmanagment.services.AuthenticationService;
import com.example.taskmanagment.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Auth", description = "User`s authentication management API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;


    @Operation(
            summary = "Create user access token and persist new refresh token",
            description = "Get a jwt-tokens by credentials. The response is access and refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JwtResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping("/login")
    ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginDto loginDto, HttpServletRequest request) {
        JwtResponse response = authenticationService.login(loginDto, request);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "refresh access token by valid refresh token",
            description = "refresh access token and persist new refresh token. The response is access and refresh tokens"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JwtResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping("/refresh")
    ResponseEntity<JwtResponse> refresh(@RequestBody String refresh, HttpServletRequest request) {
        JwtResponse response = authenticationService.refresh(refresh, request);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "clean auth data from server",
            description = "delete refresh token from database. The response is string about success logout"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = JwtResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)}),
            @ApiResponse(responseCode = "403", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = APPLICATION_JSON_VALUE)})})
    @PostMapping("/logout")
    ResponseEntity<String> logout(HttpServletRequest request, Principal principal) {
        authenticationService.logout(request, principal.getName());
        return ResponseEntity.ok(ResponseUtils.LOGOUT);
    }


}