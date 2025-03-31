package com.example.lostnfound.mailing;

import com.example.lostnfound.model.User;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

	@Override
    public <T> void init(T context) {
        User user = (User) context;
        put("Username", user.getName());
        setTemplateLocation("mail/email-verification.html"); // Corrected path
        setSubject("LostNFound: Complete Your Email Verification");
        setFrom("no-reply@lostnfound.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
	    put("token", token);
    }

    public void buildVerificationUrl(String baseUrl, String token) {
        // Ensure baseUrl is not null
        if (baseUrl == null) {
            throw new IllegalArgumentException("Base URL must not be null");
        }

        final String url= UriComponentsBuilder.fromUriString(baseUrl)
                .path("/verifyEmail").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }

}
