package com.example.taskmanagment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class JwtResponse {

    @Schema(description = "access token")
    private final String accessToken;

    @Schema(description = "refresh token")
    private final String refreshToken;
}
