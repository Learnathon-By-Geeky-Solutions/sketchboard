package com.example.lostnfound.service.user;
import java.util.List;
import java.util.Objects;

import com.example.lostnfound.mailing.AccountVerificationEmailContext;
import com.example.lostnfound.mailing.ForgotPasswordEmailContext;
import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.service.EmailServiceImpl;
import com.example.lostnfound.service.SecureTokenService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.PostRepo;
import com.example.lostnfound.repository.UserRepo;
import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.dto.UserDto;
import com.example.lostnfound.exception.PostNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final AuthenticationManager authmManager;
    private final JWTService jwtService;
    private final PostRepo postRepo;
    private final ModelMapper modelMapper;
    private final SecureTokenService secureTokenService;
    private final EmailServiceImpl emailService;
    @Value("${app.baseUrl}")
    private String baseUrl;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    UserService(UserRepo userRepo, AuthenticationManager authmManager, JWTService jwtService, PostRepo postRepo, ModelMapper modelMapper, SecureTokenService secureTokenService, EmailServiceImpl emailService) {
        this.userRepo = userRepo;
        this.authmManager = authmManager;
        this.jwtService = jwtService;
        this.postRepo = postRepo;
        this.modelMapper = modelMapper;
        this.secureTokenService = secureTokenService;
        this.emailService = emailService;
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public void register(User user) {
        if(userRepo.findByEmail(user.getEmail()) != null) {
            throw new UserNotFoundException("User already exists with email: " + user.getEmail() + "\n");
        }
        User newUser = userRepo.save(user);
        sendRegistrationEmail(user);
    }

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

    public User findByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);
        User user=userRepo.findByEmail(email);
        if(user==null){
            logger.error("Unable to locate user with email: {}", email);
            throw new UserNotFoundException("User not found with email: " + email + "\n");
        }
        else return user;
    }

    public List<Post> findPostsByUserId(Long userId) {
        List<Post> posts = postRepo.findByUserId(userId);
        if(posts == null) {
            throw new PostNotFoundException("Posts not found");
        }
        else return posts;
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id + "\n"));
    }

    public User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            User user = findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException( "User not found");
            }
            return user;
        }
        else return null;
    }

    public boolean checkIfUserExist(String email) {
        return userRepo.findByEmail(email) != null;
    }

    public void sendRegistrationEmail(User user) {
        SecureToken secureToken = secureTokenService.createToken();
        secureToken.setUser(user);
        secureTokenService.saveSecureToken(secureToken);

        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(user);
        context.setToken(secureToken.getToken());
        context.buildVerificationUrl(baseUrl, secureToken.getToken());

        try {
            emailService.sendEmail(context);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyUser(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);

        if (Objects.isNull(token) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()) {
            throw new InvalidTokenException("Token is invalid or expired");
        }
        User user = userRepo.findById(secureToken.getUser().getUserId()).orElseThrow(() -> new UserNotFoundException("User not found with id: " + secureToken.getUser().getUserId() + "\n"));
        user.setAccountVerified(true);
        userRepo.save(user);
        return true;
    }
    public void update(UserDto updatedUser) {
        User user = getCurrentUser();
        user.setEmail(updatedUser.getEmail());
        user.setName(updatedUser.getName());
        user.setDepartment(updatedUser.getDepartment());
        user.setAddress(updatedUser.getAddress());
        userRepo.save(user);
    }

    public void updatePassword(User user){
        logger.debug("Updating password for user: {}", user.getEmail());
        userRepo.save(user);
    }
}
