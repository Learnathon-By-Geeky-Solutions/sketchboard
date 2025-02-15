package com.example.lostnfound.service.user;

import com.example.lostnfound.model.User;

public interface UserService {
    User userRegister(User user);
    String verify(String name, String password);

}
