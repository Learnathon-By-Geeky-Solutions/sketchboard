package com.example.lostnfound.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long userId;
    private String name;
    private String email;
    private String address;
    private String department;

}
