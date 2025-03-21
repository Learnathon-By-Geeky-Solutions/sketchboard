package com.example.lostnfound.service;

import com.example.lostnfound.mailing.AbstractEmailContext;
import jakarta.mail.MessagingException;
public interface EmailService {
    void sendEmail(final AbstractEmailContext email) throws MessagingException;
}
