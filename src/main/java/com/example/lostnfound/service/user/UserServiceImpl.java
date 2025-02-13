package com.example.lostnfound.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.UserRepo;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public User userRegister(User user) {
        return userRepo.save(user);
    }
    
}
