package com.example.lostnfound.service.user;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.PostRepo;
import com.example.lostnfound.repository.UserRepo;
import com.example.lostnfound.exception.PostNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final AuthenticationManager authmManager;
    private final JWTService jwtService;
    private final PostRepo postRepo;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    UserServiceImpl(UserRepo userRepo, AuthenticationManager authmManager, JWTService jwtService, PostRepo postRepo) {
        this.userRepo = userRepo;
        this.authmManager = authmManager;
        this.jwtService = jwtService;
        this.postRepo = postRepo;
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

    @Override
    public User findByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);
        User user=userRepo.findByEmail(email);
        if(user==null){
            logger.error("Unable to locate user with email: {}", email);
            throw new UserNotFoundException("User not found with email: " + email + "\n");
        }
        else return user;
    }

    @Override
    public List<Post> findPostsByUserId(Long userId) {
        List<Post> posts = postRepo.findByUserUserId(userId);
        if(posts == null) {
            throw new PostNotFoundException("Posts not found");
        }
        else return posts;
    }
    
}
