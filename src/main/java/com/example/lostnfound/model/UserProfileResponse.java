package com.example.lostnfound.model;

import com.example.lostnfound.dto.UserDto;

import java.util.List;

public class UserProfileResponse {
    private UserDto user;
    private List<Post> posts;

    public UserProfileResponse(UserDto user, List<Post> posts) {
        this.user = user;
        this.posts = posts;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}