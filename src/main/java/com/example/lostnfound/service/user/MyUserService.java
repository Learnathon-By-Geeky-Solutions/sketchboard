package com.example.lostnfound.service.user;

import com.example.lostnfound.model.MyUserDetails;
import com.example.lostnfound.model.User;

import com.example.lostnfound.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Component
public class MyUserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =userRepo.findByEmail(email);
        if(user == null){
            System.out.print("Email not found");
            throw new UsernameNotFoundException("Email not found");
        }
        else {
            return new MyUserDetails(user) ;
        }
    }


    
    
}
