package com.example.lostnfound.service.user;

import java.util.List;

import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;

public interface UserService {
    User userRegister(User user);
    String verify(String name, String password);
    User findByEmail(String email);
    List<Post> findPostsByUserId(Long userId);

}
