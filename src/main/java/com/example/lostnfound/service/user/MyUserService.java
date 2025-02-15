package com.example.lostnfound.service.user;

import com.example.lostnfound.model.MyUserDetails;
import com.example.lostnfound.model.User;

import com.example.lostnfound.repository.UserRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Component
public class MyUserService implements UserDetailsService {
    private final UserRepo userRepo;
    private static final Logger logger = LoggerFactory.getLogger(MyUserService.class);
    
    MyUserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =userRepo.findByEmail(email);
        if(user == null){
            logger.error("Email not found: {}", email);
            throw new UsernameNotFoundException("Email not found");
        }
        else {
            return new MyUserDetails(user) ;
        }
    }


    
    
}
