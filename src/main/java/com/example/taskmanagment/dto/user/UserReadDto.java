package com.example.taskmanagment.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserReadDto {

    @Schema(description = "id of user")
    private Long id;

    @Schema(description = "email of user")
    private String email;
}
