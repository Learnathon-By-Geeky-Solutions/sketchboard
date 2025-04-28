package com.example.lostnfound.dto;

import com.example.lostnfound.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {
    @Schema(description = "Id of the User.", example = "1")
    private Long id;

    @NotEmpty
    @Schema(description = "Name of the User.", example = "John Doe", required = true)
    private String name;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    @Schema(description = "Email of the User.", example = "johndoe@gmail.com", required = true)
    private String email;

    @NotEmpty  
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",message = "Password must be at least 8 characters with at least one digit, one uppercase, one lowercase, and one special character")
    @Schema(description = "Password of the User.", example = "password", required = true)
    private String password;

    @NotEmpty
    @Schema(description = "Address of the User.", example = "123, Main Street, New York", required = true)
    private String address;

    @NotEmpty
    @Schema(description = "Department of the User.", example = "Computer Science", required = true)
    private String department;

    @NotEmpty
    @Schema(description = "Role of the User.", example = "USER", required = true)
    private Role role;

}
