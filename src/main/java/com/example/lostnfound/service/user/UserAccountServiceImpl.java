package com.example.lostnfound.service.user;

import com.example.lostnfound.exception.InvalidTokenException;
import com.example.lostnfound.exception.UnknownIdentifierException;
import com.example.lostnfound.exception.UserNotFoundException;
import com.example.lostnfound.mailing.ForgotPasswordEmailContext;
import com.example.lostnfound.model.SecureToken;
import com.example.lostnfound.model.User;
import com.example.lostnfound.repository.UserRepo;
import com.example.lostnfound.service.EmailService;
import com.example.lostnfound.service.SecureTokenService;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service("userAccountService")
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepo userRepo;
    private final UserService userService;
    private final SecureTokenService secureTokenService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserAccountServiceImpl(UserService userService, SecureTokenService secureTokenService, EmailService emailService, UserRepo userRepo, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.secureTokenService = secureTokenService;
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void forgotPassword(String email) throws UnknownIdentifierException, UserNotFoundException {
        User user = userService.findByEmail(email);
        sendResetPasswordEmail(user);
    }

    @Override
    public void updatePassword(final String password, final String token) throws InvalidTokenException, UnknownIdentifierException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if (secureToken == null || secureToken.isExpired()) {
            throw new InvalidTokenException("Token is not valid or expired.");
        }
        Optional<User> user = userRepo.findById(secureToken.getUser().getUserId());
        if (user.isEmpty()) {
            throw new UnknownIdentifierException("User not found for this token.");
        }
        System.out.println("User found for token: " + user.get().getEmail());
        secureTokenService.removeToken(secureToken); // Remove token after use
        user.get().setPassword(bCryptPasswordEncoder.encode(password));
        userRepo.save(user.get());
    }

    protected void sendResetPasswordEmail(User user) throws UnknownIdentifierException {
        SecureToken secureToken = secureTokenService.createToken(user);
        secureToken.setUser(user);
        secureTokenService.saveSecureToken(secureToken);

        ForgotPasswordEmailContext emailContext = new ForgotPasswordEmailContext();
        emailContext.init(user);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildTemporaryPassword(secureToken.getToken()); // Include temporary password
        try {
            emailService.sendEmail(emailContext);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
