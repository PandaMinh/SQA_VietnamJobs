package com.demo.services;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @InjectMocks
    private MailServiceImpl mailService;

    @Mock
    private JavaMailSender sender;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
    }

    @Test
    void testSendMailAccuracy_Success() {
        when(sender.createMimeMessage()).thenReturn(mimeMessage);

        boolean result = mailService.sendMailAccuracy(
                "from@gmail.com",
                "to@gmail.com",
                "<b>Test content</b>"
        );

        assertTrue(result);
        verify(sender).createMimeMessage();
        verify(sender).send(mimeMessage);
    }

    @Test
    void testSendMailAccuracy_SendFails_ReturnsFalse() {
        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail error")).when(sender).send(mimeMessage);

        boolean result = mailService.sendMailAccuracy(
                "from@gmail.com",
                "to@gmail.com",
                "<b>Test content</b>"
        );

        assertFalse(result);
        verify(sender).send(mimeMessage);
    }
}
