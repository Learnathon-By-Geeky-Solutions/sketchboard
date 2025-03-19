package com.example.lostnfound.model;

import com.example.lostnfound.dto.PostDto;
import com.example.lostnfound.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserProfileResponse {
    private UserDto user;
    private List<PostDto> posts;

    public UserProfileResponse(UserDto user, List<PostDto> posts) {
        this.user = user;
        this.posts = posts;
    }

}