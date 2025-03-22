package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordDto {

    @NotNull
    @Schema(description = "Email of the User.", example = "Passwod@123", required = true)
    private String currentPassword;
    
    @NotNull
    @Schema(description = "Email of the User.", example = "Passwod@123", required = true)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",message = "Password must be at least 8 characters with at least one digit, one uppercase, one lowercase, and one special character")
    private String newPassword;
}
