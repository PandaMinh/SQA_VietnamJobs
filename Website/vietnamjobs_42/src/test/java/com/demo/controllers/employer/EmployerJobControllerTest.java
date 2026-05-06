package com.demo.controllers.employer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.demo.entities.*;
import com.demo.services.*;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployerJobControllerTest {

    @InjectMocks
    private EmployerJobController controller;

    @Mock
    private PostingService jobProjectService;

    @Mock
    private AccountService accountService;

    @Mock
    private EmployerService companyService;

    @Mock
    private Authentication auth;

    @Mock
    private RedirectAttributes redirectAttributes;

    private Postings newJob;
    private Account account;
    private Employer employer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newJob = new Postings();
        newJob.setTitle("Software Engineer");
        account = new Account();
        account.setId(1);
        employer = new Employer();
        employer.setId(1);
    }

    @Test
    void testHandleAdd_Success() {
        when(auth.getName()).thenReturn("testUser");
        when(accountService.getByUsername("testUser")).thenReturn(account);
        when(companyService.getByAccountId(1)).thenReturn(employer);
        when(jobProjectService.saveDB(any(Postings.class))).thenReturn(true);

        String result = controller.handleAdd(newJob, redirectAttributes, auth);

        verify(jobProjectService).saveDB(newJob);
        assertEquals("redirect:/employer/job", result);
        assertEquals(true, newJob.isOpen());
        assertEquals(false, newJob.isStatus());
        assertEquals(employer, newJob.getEmployer());
        assertNotNull(newJob.getCreated());
        verify(redirectAttributes).addFlashAttribute("success", "Thành công!");
    }

    @Test
    void testHandleAdd_Failure_InvalidData() {
        when(auth.getName()).thenReturn("testUser");
        when(accountService.getByUsername("testUser")).thenReturn(account);
        when(companyService.getByAccountId(1)).thenReturn(employer);
        when(jobProjectService.saveDB(any(Postings.class))).thenReturn(false);

        String result = controller.handleAdd(newJob, redirectAttributes, auth);

        verify(jobProjectService).saveDB(newJob);
        assertEquals("redirect:/employer/job/add", result);
        assertEquals(true, newJob.isOpen());
        assertEquals(false, newJob.isStatus());
        assertEquals(employer, newJob.getEmployer());
        assertNotNull(newJob.getCreated());
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testHandleAdd_Failure_Exception() {
        when(auth.getName()).thenReturn("testUser");
        when(accountService.getByUsername("testUser")).thenReturn(account);
        when(companyService.getByAccountId(1)).thenReturn(employer);
        when(jobProjectService.saveDB(any(Postings.class))).thenThrow(new RuntimeException("DB Error"));

        String result = controller.handleAdd(newJob, redirectAttributes, auth);

        verify(jobProjectService).saveDB(newJob);
        assertEquals("redirect:/employer/job/add", result);
        assertEquals(true, newJob.isOpen());
        assertEquals(false, newJob.isStatus());
        assertEquals(employer, newJob.getEmployer());
        assertNotNull(newJob.getCreated());
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testHandleAdd_Failure_NullAuthentication() {
        Authentication nullAuth = null;

        String result = controller.handleAdd(newJob, redirectAttributes, nullAuth);

        verify(jobProjectService, never()).saveDB(any(Postings.class));
        assertNull(newJob.getEmployer());
        assertNull(newJob.getCreated());
        assertEquals("redirect:/employer/job/add", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testHandleAdd_Failure_AccountNotFound() {
        when(auth.getName()).thenReturn("testUser");
        when(accountService.getByUsername("testUser")).thenReturn(null);

        String result = controller.handleAdd(newJob, redirectAttributes, auth);

        verify(jobProjectService, never()).saveDB(any(Postings.class));
        assertNull(newJob.getEmployer());
        assertNull(newJob.getCreated());
        assertEquals("redirect:/employer/job/add", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testHandleAdd_Failure_EmployerNotFound() {
        when(auth.getName()).thenReturn("testUser");
        when(accountService.getByUsername("testUser")).thenReturn(account);
        when(companyService.getByAccountId(1)).thenReturn(null);
        when(jobProjectService.saveDB(any(Postings.class))).thenReturn(false); // Giả lập thất bại

        String result = controller.handleAdd(newJob, redirectAttributes, auth);

        verify(jobProjectService).saveDB(newJob); // Chấp nhận saveDB được gọi
        assertNull(newJob.getEmployer()); // Employer là null
        assertNotNull(newJob.getCreated()); // Created vẫn được thiết lập
        assertEquals(true, newJob.isOpen());
        assertEquals(false, newJob.isStatus());
        assertEquals("redirect:/employer/job/add", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testHandleAdd_Failure_MissingJobTitle() {
        newJob.setTitle(null);
        when(auth.getName()).thenReturn("testUser");
        when(accountService.getByUsername("testUser")).thenReturn(account);
        when(companyService.getByAccountId(1)).thenReturn(employer);
        when(jobProjectService.saveDB(any(Postings.class))).thenReturn(false);

        String result = controller.handleAdd(newJob, redirectAttributes, auth);

        verify(jobProjectService).saveDB(newJob);
        assertEquals("redirect:/employer/job/add", result);
        assertEquals(true, newJob.isOpen());
        assertEquals(false, newJob.isStatus());
        assertEquals(employer, newJob.getEmployer());
        assertNotNull(newJob.getCreated());
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }
}