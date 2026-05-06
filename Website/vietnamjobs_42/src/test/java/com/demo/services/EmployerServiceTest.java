package com.demo.services;

import com.demo.entities.Account;
import com.demo.entities.Employer;
import com.demo.repositories.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployerServiceTest {

    @InjectMocks
    private EmployerServiceImpl employerService;

    @Mock
    private EmployerRepository employerRepository;

    private Employer employer;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1); // Giả lập ID của tài khoản
        employer = new Employer();
        employer.setId(1);
        employer.setAccount(account); // Gán Account vào Employer
    }

    @Test
    void testGetByAccountId_Success() {
        when(employerRepository.findByAccountID(1)).thenReturn(employer);

        Employer result = employerService.getByAccountId(1);

        verify(employerRepository).findByAccountID(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getAccount().getId());
    }

    @Test
    void testGetByAccountId_NotFound() {
        when(employerRepository.findByAccountID(999)).thenReturn(null);

        Employer result = employerService.getByAccountId(999);

        verify(employerRepository).findByAccountID(999);
        assertNull(result);
    }

    @Test
    void testGetByAccountId_DatabaseError() {
        when(employerRepository.findByAccountID(1)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> employerService.getByAccountId(1));

        verify(employerRepository).findByAccountID(1);
    }
}