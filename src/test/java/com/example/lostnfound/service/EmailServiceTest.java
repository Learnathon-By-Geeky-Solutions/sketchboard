package com.example.lostnfound.service;

import com.example.lostnfound.mailing.AbstractEmailContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

class EmailServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendEmail_success() throws MessagingException {
        // Mock email context
        AbstractEmailContext emailContext = mock(AbstractEmailContext.class);
        when(emailContext.getTo()).thenReturn("to@example.com");
        when(emailContext.getFrom()).thenReturn("from@example.com");
        when(emailContext.getSubject()).thenReturn("Test Subject");
        when(emailContext.getTemplateLocation()).thenReturn("email/template");

        Map<String, Object> contextData = new HashMap<>();
        contextData.put("name", "User");
        when(emailContext.getContext()).thenReturn(contextData);
        when(templateEngine.process(eq("email/template"), any(Context.class)))
                .thenReturn("<html><body>Hello, User</body></html>");

        emailService.sendEmail(emailContext);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
