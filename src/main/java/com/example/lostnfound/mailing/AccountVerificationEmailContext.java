package com.example.lostnfound.mailing;

import com.example.lostnfound.model.User;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    private String token;
    @Override
    public <T> void init(T context) {
        User user = (User) context;
        put("Username", user.getName());
        setTemplateLocation("mail/email-verification.html"); // Corrected path
        setSubject("Complete Email Verification");
        setFrom("s2010676102@ru.ac.bd");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(String baseUrl, String token) {
        // Ensure baseUrl is not null
        if (baseUrl == null) {
            throw new IllegalArgumentException("Base URL must not be null");
        }

        UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("registration/verify")
                .queryParam("token", token)
                .toUriString();
    }

}
