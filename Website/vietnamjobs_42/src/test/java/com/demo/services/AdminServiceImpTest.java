package com.demo.services;

import com.demo.dtos.AdminDTO;
import com.demo.entities.Admin;
import com.demo.repositories.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImpTest {

    @InjectMocks
    private AdminServiceImp adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ModelMapper mapper;

    private Admin admin;
    private AdminDTO adminDTO;

    @BeforeEach
    void setUp() {
        admin = new Admin();
        admin.setId(1);
        admin.setFullname("System Admin");
        admin.setPhone("0123456789");
        admin.setPhoto("admin.png");
        admin.setStatus(true);

        adminDTO = new AdminDTO();
        adminDTO.setId(1);
        adminDTO.setAccountId(1);
        adminDTO.setAccountName("admin");
        adminDTO.setFullname("System Admin");
        adminDTO.setPhone("0123456789");
        adminDTO.setPhoto("admin.png");
    }

    @Test
    void testFindByAccountId_Success() {
        when(adminRepository.findByAccountID(1)).thenReturn(admin);
        when(mapper.map(admin, AdminDTO.class)).thenReturn(adminDTO);

        AdminDTO result = adminService.findByAccountId(1);

        assertNotNull(result);
        assertEquals("System Admin", result.getFullname());
        verify(adminRepository).findByAccountID(1);
        verify(mapper).map(admin, AdminDTO.class);
    }

    @Test
    void testFindByAccountId_NotFound() {
        AdminDTO emptyDto = new AdminDTO();
        when(adminRepository.findByAccountID(999)).thenReturn(null);
        when(mapper.map(null, AdminDTO.class)).thenReturn(emptyDto);

        AdminDTO result = adminService.findByAccountId(999);

        assertNotNull(result);
        verify(adminRepository).findByAccountID(999);
        verify(mapper).map(null, AdminDTO.class);
    }

    @Test
    void testSave_Success() {
        when(mapper.map(adminDTO, Admin.class)).thenReturn(admin);
        when(adminRepository.save(admin)).thenReturn(admin);

        boolean result = adminService.save(adminDTO);

        assertTrue(result);
        verify(mapper).map(adminDTO, Admin.class);
        verify(adminRepository).save(admin);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(mapper.map(adminDTO, Admin.class)).thenReturn(admin);
        when(adminRepository.save(admin)).thenThrow(new RuntimeException("Database error"));

        boolean result = adminService.save(adminDTO);

        assertFalse(result);
        verify(adminRepository).save(admin);
    }
}
