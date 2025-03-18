package com.example.lostnfound.mailing;

import com.example.lostnfound.model.User;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    private String token;
    @Override
    public <T> void init(T context) {
        User user = (User) context;
        put("Username", user.getName());
        setTemplateLocation("mail/email-verification");
        setSubject("Complete Email Verification");
        setFrom("sk03ru@gmail.com");
        setTo(user.getEmail());
    }

    public void setTo(String token) {
        this.token = token;
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token) {
        final String url = UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/account/verify")
                .queryParam("token", token)
                .toUriString();
        put("verificationURL", url);
    }

}
