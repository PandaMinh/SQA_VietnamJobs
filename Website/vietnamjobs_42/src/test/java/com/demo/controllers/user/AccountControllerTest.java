package com.demo.controllers.user;

import com.demo.dtos.AccountDTO;
import com.demo.services.AccountService;
import com.demo.services.MailService;
import com.demo.servicesModelMap.ServiceModelMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AccountControllerTest {

    // TC-CT-USER-CP-001 ~ TC-CT-USER-CP-005
    // File: AccountControllerTest.java
    // Class: AccountController
    // Method: changepassword

    @InjectMocks
    private AccountController controller;

    @Mock
    private AccountService accountService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private MailService mailService;

    @Mock
    private ServiceModelMap serviceModelMap;

    @Mock
    private Environment environment;

    @Mock
    private RedirectAttributes redirectAttributes;

    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountDTO = new AccountDTO();
        accountDTO.setId(1);
        accountDTO.setUsername("testUser");
        accountDTO.setPassword("encodedOldPassword");
    }

    @Test
    void testChangePassword_PasswordsMatch_SaveSuccess() {
        // TC-CT-USER-CP-001
        // Mục tiêu: Kiểm tra đổi mật khẩu thành công khi confirmpassword khớp với password và save thành công
        // Input: username="testUser", password="newPassword123", confirmpassword="newPassword123"
        // Output mong đợi: redirect "/account/login", flash "successMk"="pass"
        when(accountService.findByUsername("testUser")).thenReturn(accountDTO);
        when(encoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(accountService.save(any(AccountDTO.class))).thenReturn(true);

        String result = controller.changepassword("testUser", "newPassword123", "newPassword123", redirectAttributes);

        verify(accountService).findByUsername("testUser");
        verify(encoder).encode("newPassword123");
        verify(accountService).save(accountDTO);
        assertEquals("encodedNewPassword", accountDTO.getPassword());
        assertEquals("redirect:/account/login", result);
        verify(redirectAttributes).addFlashAttribute("successMk", "pass");
    }

    @Test
    void testChangePassword_PasswordsMismatch() {
        // TC-CT-USER-CP-002
        // Mục tiêu: Kiểm tra xử lý khi confirmpassword không khớp với password
        // Input: username="testUser", password="newPassword123", confirmpassword="differentPassword"
        // Output mong đợi: redirect "/account/changepassword", flash "errorMk"="failed", không gọi service
        String result = controller.changepassword("testUser", "newPassword123", "differentPassword", redirectAttributes);

        verify(accountService, never()).findByUsername(anyString());
        verify(accountService, never()).save(any());
        assertEquals("redirect:/account/changepassword", result);
        verify(redirectAttributes).addFlashAttribute("errorMk", "failed");
    }

    @Test
    void testChangePassword_PasswordsMatch_SaveFails() {
        // TC-CT-USER-CP-003
        // Mục tiêu: Kiểm tra xử lý khi mật khẩu khớp nhưng save thất bại
        // Input: username="testUser", password="newPassword123", confirmpassword="newPassword123", save=false
        // Output mong đợi: redirect "/account/changepassword", flash "errorMk"="failed"
        when(accountService.findByUsername("testUser")).thenReturn(accountDTO);
        when(encoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(accountService.save(any(AccountDTO.class))).thenReturn(false);

        String result = controller.changepassword("testUser", "newPassword123", "newPassword123", redirectAttributes);

        verify(accountService).findByUsername("testUser");
        verify(accountService).save(accountDTO);
        assertEquals("redirect:/account/changepassword", result);
        verify(redirectAttributes).addFlashAttribute("errorMk", "failed");
    }

    @Test
    void testChangePassword_PasswordsMatchWithLeadingTrailingWhitespace() {
        // TC-CT-USER-CP-004
        // Mục tiêu: Kiểm tra đổi mật khẩu thành công khi mật khẩu có khoảng trắng đầu/cuối (trim được áp dụng)
        // Input: password="  newPassword123  ", confirmpassword="  newPassword123  "
        // Output mong đợi: Encoder nhận "newPassword123" (đã trim), redirect "/account/login"
        when(accountService.findByUsername("testUser")).thenReturn(accountDTO);
        when(encoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(accountService.save(any(AccountDTO.class))).thenReturn(true);

        String result = controller.changepassword("testUser", "  newPassword123  ", "  newPassword123  ", redirectAttributes);

        verify(accountService).findByUsername("testUser");
        verify(encoder).encode("newPassword123");
        assertEquals("redirect:/account/login", result);
        verify(redirectAttributes).addFlashAttribute("successMk", "pass");
    }

    @Test
    void testChangePassword_FindByUsernameThrowsException() {
        // TC-CT-USER-CP-005
        // Mục tiêu: Kiểm tra hành vi khi service.findByUsername ném ngoại lệ (method không có try-catch)
        // Input: username="testUser", password="newPassword123", confirmpassword="newPassword123"
        // Output mong đợi: RuntimeException được ném ra khỏi controller (không có try-catch trong method)
        when(accountService.findByUsername("testUser")).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () ->
            controller.changepassword("testUser", "newPassword123", "newPassword123", redirectAttributes)
        );

        verify(accountService).findByUsername("testUser");
        verify(accountService, never()).save(any());
    }
}
