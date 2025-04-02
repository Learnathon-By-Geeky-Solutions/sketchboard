package com.example.lostnfound.service;

import com.example.lostnfound.mailing.AbstractEmailContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(final AbstractEmailContext email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getContext());
        String emailContent = templateEngine.process(email.getTemplateLocation(), context);

        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubject());
        helper.setText(emailContent, true);

        emailSender.send(message);
    }
}
