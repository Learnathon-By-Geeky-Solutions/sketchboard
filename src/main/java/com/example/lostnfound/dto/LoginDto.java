package com.example.lostnfound.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;



@Data
public class LoginDto {
    @NotEmpty
    @Schema(description = "Email of the User.", example = "johndoe@gmail.com", required = true)
    private String email;

    @NotEmpty
    @Schema(description = "Password of the User.", example = "password", required = true)
    private String password;
    
}
