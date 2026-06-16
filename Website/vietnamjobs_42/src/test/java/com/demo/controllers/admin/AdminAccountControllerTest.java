package com.demo.controllers.admin;

import com.demo.entities.Account;
import com.demo.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AdminAccountControllerTest {

    // TC-CT-ADMIN-ACC-001 ~ TC-CT-ADMIN-ACC-005
    // File: AdminAccountControllerTest.java
    // Class: AdminAccountController
    // Method: updateStatus

    @InjectMocks
    private AdminAccountController controller;

    @Mock
    private AccountService accountService;

    @Mock
    private RedirectAttributes redirectAttributes;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1);
    }

    @Test
    void testUpdateStatus_ActiveToInactive_Success() {
        // TC-CT-ADMIN-ACC-001
        // Mục tiêu: Kiểm tra chuyển trạng thái từ active (true) sang inactive (false) thành công
        // Input: id=1, account.status=true
        // Output mong đợi: Flash "Thành công!", redirect "/admin/account", newStatus=false
        account.setStatus(true);
        when(accountService.find(1)).thenReturn(account);
        when(accountService.updateStatusById(1, false)).thenReturn(true);

        String result = controller.updateStatus(1, redirectAttributes);

        verify(accountService).find(1);
        verify(accountService).updateStatusById(1, false);
        assertFalse(account.isStatus());
        assertEquals("redirect:/admin/account", result);
        verify(redirectAttributes).addFlashAttribute("success", "Thành công!");
    }

    @Test
    void testUpdateStatus_InactiveToActive_Success() {
        // TC-CT-ADMIN-ACC-002
        // Mục tiêu: Kiểm tra chuyển trạng thái từ inactive (false) sang active (true) thành công
        // Input: id=1, account.status=false
        // Output mong đợi: Flash "Thành công!", redirect "/admin/account", newStatus=true
        account.setStatus(false);
        when(accountService.find(1)).thenReturn(account);
        when(accountService.updateStatusById(1, true)).thenReturn(true);

        String result = controller.updateStatus(1, redirectAttributes);

        verify(accountService).find(1);
        verify(accountService).updateStatusById(1, true);
        assertTrue(account.isStatus());
        assertEquals("redirect:/admin/account", result);
        verify(redirectAttributes).addFlashAttribute("success", "Thành công!");
    }

    @Test
    void testUpdateStatus_AccountNotFound() {
        // TC-CT-ADMIN-ACC-003
        // Mục tiêu: Kiểm tra xử lý khi không tìm thấy tài khoản
        // Input: id=999, accountService.find trả về null
        // Output mong đợi: Flash "Không tìm thấy...", redirect "/admin/account", không gọi updateStatusById
        when(accountService.find(999)).thenReturn(null);

        String result = controller.updateStatus(999, redirectAttributes);

        verify(accountService).find(999);
        verify(accountService, never()).updateStatusById(anyInt(), anyBoolean());
        assertEquals("redirect:/admin/account", result);
        verify(redirectAttributes).addFlashAttribute("notFound", "Không tìm thấy...");
    }

    @Test
    void testUpdateStatus_ServiceThrowsException() {
        // TC-CT-ADMIN-ACC-004
        // Mục tiêu: Kiểm tra xử lý khi service ném ra ngoại lệ
        // Input: id=1, accountService.find ném RuntimeException
        // Output mong đợi: Flash "Thất bại...", redirect "/admin/account", không gọi updateStatusById
        when(accountService.find(1)).thenThrow(new RuntimeException("Database error"));

        String result = controller.updateStatus(1, redirectAttributes);

        verify(accountService).find(1);
        verify(accountService, never()).updateStatusById(anyInt(), anyBoolean());
        assertEquals("redirect:/admin/account", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testUpdateStatus_UpdateStatusByIdFails() {
        // TC-CT-ADMIN-ACC-005
        // Mục tiêu: Kiểm tra xử lý khi updateStatusById trả về false (cập nhật thất bại)
        // Input: id=1, account.status=true, updateStatusById trả về false
        // Output mong đợi: Flash "Thất bại...", redirect "/admin/account"
        account.setStatus(true);
        when(accountService.find(1)).thenReturn(account);
        when(accountService.updateStatusById(1, false)).thenReturn(false);

        String result = controller.updateStatus(1, redirectAttributes);

        verify(accountService).find(1);
        verify(accountService).updateStatusById(1, false);
        assertEquals("redirect:/admin/account", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }
}
