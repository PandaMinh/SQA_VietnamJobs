package com.demo.services;

import com.demo.entities.Account;
import com.demo.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1);
        account.setUsername("testUser");
    }

    @Test
    void testGetByUsername_Success() {
        when(accountRepository.getByUsername("testUser")).thenReturn(account);

        Account result = accountService.getByUsername("testUser");

        verify(accountRepository).getByUsername("testUser");
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals(1, result.getId());
    }

    @Test
    void testGetByUsername_NotFound() {
        when(accountRepository.getByUsername("unknownUser")).thenReturn(null);

        Account result = accountService.getByUsername("unknownUser");

        verify(accountRepository).getByUsername("unknownUser");
        assertNull(result);
    }

    @Test
    void testGetByUsername_DatabaseError() {
        when(accountRepository.getByUsername("testUser")).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> accountService.getByUsername("testUser"));

        verify(accountRepository).getByUsername("testUser");
    }
}