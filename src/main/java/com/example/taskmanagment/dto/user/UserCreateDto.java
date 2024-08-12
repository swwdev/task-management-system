package com.example.taskmanagment.dto.user;

import com.example.taskmanagment.validation.annotations.Unique;
import com.example.taskmanagment.validation.groups.FirstGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.taskmanagment.utils.Constants.PASS_REGEXP;
import static com.example.taskmanagment.utils.ResponseUtils.NEED_EMAIL;
import static com.example.taskmanagment.utils.ResponseUtils.NEED_PASSWORD;
import static com.example.taskmanagment.utils.ResponseUtils.WEAK_PASSWORD;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@GroupSequence({FirstGroup.class, UserCreateDto.class})
public class UserCreateDto {

    @Schema(description = "email of user")
    @Unique
    @Email
    @NotBlank(groups = FirstGroup.class, message = NEED_EMAIL)
    private String email;

    @Schema(description = "password of user")
    @NotBlank(message = NEED_PASSWORD)
    @Pattern(regexp = PASS_REGEXP, message = WEAK_PASSWORD)
    private String password;
}
