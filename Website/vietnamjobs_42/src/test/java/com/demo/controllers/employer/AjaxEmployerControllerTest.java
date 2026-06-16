package com.demo.controllers.employer;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.services.ApplicationHistoryService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AjaxEmployerControllerTest {

    @InjectMocks
    private AjaxEmployerController controller;

    @Mock
    private JavaMailSender sender;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    private ApplicationHistoryDTO applicationHistoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationHistoryDTO = new ApplicationHistoryDTO();
        applicationHistoryDTO.setId(3);
        applicationHistoryDTO.setStatus(0);
        applicationHistoryDTO.setResult(0);
    }

    @Test
    void testViewCV_UpdatesStatusAndReturnsDto() {
        when(applicationHistoryService.findByID(3)).thenReturn(applicationHistoryDTO);
        when(applicationHistoryService.save(applicationHistoryDTO)).thenReturn(true);

        ApplicationHistoryDTO result = controller.viewCV(8, 3);

        assertEquals(applicationHistoryDTO, result);
        assertEquals(1, result.getStatus());
        verify(applicationHistoryService).findByID(3);
        verify(applicationHistoryService).save(applicationHistoryDTO);
    }

    @Test
    void testMailDialog_ReturnsDto() {
        when(applicationHistoryService.findByID(3)).thenReturn(applicationHistoryDTO);

        ApplicationHistoryDTO result = controller.mailDialog(3);

        assertEquals(applicationHistoryDTO, result);
        verify(applicationHistoryService).findByID(3);
    }

    @Test
    void testSendMailAcp_Success() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        when(applicationHistoryService.findByID(3)).thenReturn(applicationHistoryDTO);
        when(applicationHistoryService.save(applicationHistoryDTO)).thenReturn(true);

        boolean result = controller.sendMailAcp(
                3,
                "<p>Interview invitation</p>",
                "Interview Subject",
                "candidate@example.com",
                "employer@example.com"
        );

        assertTrue(result);
        assertEquals(1, applicationHistoryDTO.getResult());
        verify(sender).createMimeMessage();
        verify(sender).send(mimeMessage);
        verify(applicationHistoryService).findByID(3);
        verify(applicationHistoryService).save(applicationHistoryDTO);
    }

    @Test
    void testSendMailAcp_EmptySubject_ReturnsFalse() {
        boolean result = controller.sendMailAcp(
                3,
                "<p>Interview invitation</p>",
                "   ",
                "candidate@example.com",
                "employer@example.com"
        );

        assertFalse(result);
        verify(sender, never()).createMimeMessage();
        verify(applicationHistoryService, never()).findByID(3);
    }

    @Test
    void testSendMailAcp_EmptyContent_ReturnsFalse() {
        boolean result = controller.sendMailAcp(
                3,
                "   ",
                "Interview Subject",
                "candidate@example.com",
                "employer@example.com"
        );

        assertFalse(result);
        verify(sender, never()).createMimeMessage();
        verify(applicationHistoryService, never()).findByID(3);
    }

    @Test
    void testSendMailAcp_SendThrowsException_ReturnsFalse() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(sender).send(mimeMessage);

        boolean result = controller.sendMailAcp(
                3,
                "<p>Interview invitation</p>",
                "Interview Subject",
                "candidate@example.com",
                "employer@example.com"
        );

        assertFalse(result);
        verify(sender).createMimeMessage();
        verify(sender).send(mimeMessage);
        verify(applicationHistoryService, never()).findByID(3);
        verify(applicationHistoryService, never()).save(applicationHistoryDTO);
    }
}
