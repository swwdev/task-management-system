package com.example.taskmanagment.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.taskmanagment.utils.ResponseUtils.NEED_EMAIL;
import static com.example.taskmanagment.utils.ResponseUtils.NEED_PASSWORD;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {

    @Schema(description = "email of user")
    @Email
    @NotBlank(message = NEED_EMAIL)
    private String email;

    @Schema(description = "password of user")
    @NotBlank(message = NEED_PASSWORD)
    private String password;
}
