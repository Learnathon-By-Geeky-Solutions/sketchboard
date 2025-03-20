package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;



@Data
public class LoginDto {
    @NotEmpty
    @Schema(description = "Email of the User.", example = "johndoe@gmail.com", required = true)
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",message = "Password must be at least 8 characters with at least one digit, one uppercase, one lowercase, and one special character")
    @Schema(description = "Password of the User.", example = "password", required = true)
    private String password;
    
}
