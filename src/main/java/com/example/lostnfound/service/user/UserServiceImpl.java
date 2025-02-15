package com.example.lostnfound.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.UserRepo;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final AuthenticationManager authmManager;
    private final JWTService jwtService;

    UserServiceImpl(UserRepo userRepo, AuthenticationManager authmManager, JWTService jwtService) {
        this.userRepo = userRepo;
        this.authmManager = authmManager;
        this.jwtService = jwtService;
    }

    @Override
    public User userRegister(User user) {
        return userRepo.save(user);
    }

    @Override
    public String verify(String email, String password) {
        
        User user = userRepo.findByEmail(email);
        if(user == null) {
            return "Login Failed";
        }
        Authentication auth = authmManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        if(auth.isAuthenticated()) {
            return jwtService.generateToken(email);
        }
        else return "Login Failed";
    }
    
}
