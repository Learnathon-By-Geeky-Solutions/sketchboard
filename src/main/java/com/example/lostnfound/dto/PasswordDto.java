package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordDto {

    @NotNull
    @Schema(description = "Current password of the User.", example = "Passwod@123")
    private String currentPassword;

    @NotNull
    @Schema(description = "New password of the User.", example = "Passwod@123")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{8,}$",message = "Password must be at least 8 characters with at least one digit, one uppercase, one lowercase, and one special character")
    private String newPassword;
}