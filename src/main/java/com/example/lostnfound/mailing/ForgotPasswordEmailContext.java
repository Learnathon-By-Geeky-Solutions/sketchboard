package com.example.lostnfound.mailing;

import com.example.lostnfound.model.User;

public class ForgotPasswordEmailContext extends AbstractEmailContext {

	@Override
    public <T> void init(T context) {
        User user = (User) context;
        put("Username", user.getName());
        setTemplateLocation("mail/forgot-password.html");
        setSubject("LostNFound: Reset Your Password");
        setFrom("no-reply@lostNfound.com");
        setTo(user.getEmail());
    }

    public void setToken(String token) {
	    put("token", token);
    }

    // create a temporary Password
    public void buildTemporaryPassword(final String temporaryPassword) {
        put("temporaryPassword", temporaryPassword);
    }

}
