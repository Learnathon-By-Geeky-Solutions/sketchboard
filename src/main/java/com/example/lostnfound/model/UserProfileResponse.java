package com.example.lostnfound.model;

import com.example.lostnfound.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserProfileResponse {
    private UserDto user;
    private List<Post> posts;

    public UserProfileResponse(UserDto user, List<Post> posts) {
        this.user = user;
        this.posts = posts;
    }

}