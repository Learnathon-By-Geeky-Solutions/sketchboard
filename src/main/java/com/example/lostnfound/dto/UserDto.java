package com.example.lostnfound.dto;

import com.example.lostnfound.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserDto {
    @NotEmpty
    @Schema(description = "Name of the User.", example = "John Doe", required = true)
    private String name;

    @NotEmpty
    @Schema(description = "Email of the User.", example = "johndoe@gmail.com", required = true)
    private String email;

    @NotEmpty  
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
