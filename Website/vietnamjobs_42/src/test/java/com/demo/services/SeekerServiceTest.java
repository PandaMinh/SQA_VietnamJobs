package com.demo.services;

import com.demo.dtos.SeekerDTO;
import com.demo.entities.Account;
import com.demo.entities.Seeker;
import com.demo.repositories.SeekerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SeekerServiceTest {

    // TC-SV-SEEKER-001 ~ TC-SV-SEEKER-008
    // File: SeekerServiceTest.java
    // Class: SeekerServiceImpl

    @InjectMocks
    private SeekerServiceImpl seekerService;

    @Mock
    private SeekerRepository seekerRepository;

    @Mock
    private ModelMapper mapper;

    private Seeker seeker;
    private SeekerDTO seekerDTO;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1);
        account.setUsername("testUser");

        seeker = new Seeker();
        seeker.setId(1);
        seeker.setAccount(account);
        seeker.setFullname("Nguyen Van A");
        seeker.setPhone("0123456789");
        seeker.setStatus(true);

        seekerDTO = new SeekerDTO();
        seekerDTO.setId(1);
        seekerDTO.setAccountID(1);
        seekerDTO.setAccountName("testUser");
        seekerDTO.setFullName("Nguyen Van A");
        seekerDTO.setPhone("0123456789");
        seekerDTO.setStatus(true);
    }

    @Test
    void testFindByAccountID_Success() {
        // TC-SV-SEEKER-001
        // Mục tiêu: Kiểm tra tìm kiếm seeker theo accountID thành công
        // Input: account_id=1, repository trả về seeker hợp lệ
        // Output mong đợi: SeekerDTO không null với fullName="Nguyen Van A"
        when(seekerRepository.findByAccountID(1)).thenReturn(seeker);
        when(mapper.map(seeker, SeekerDTO.class)).thenReturn(seekerDTO);

        SeekerDTO result = seekerService.findByAccountID(1);

        verify(seekerRepository).findByAccountID(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Nguyen Van A", result.getFullName());
        assertEquals("testUser", result.getAccountName());
    }

    @Test
    void testFindByAccountID_NotFound() {
        // TC-SV-SEEKER-002
        // Mục tiêu: Kiểm tra trường hợp không tìm thấy seeker theo accountID
        // Input: account_id=999, repository trả về null
        // Output mong đợi: SeekerDTO rỗng (mapper.map xử lý null)
        when(seekerRepository.findByAccountID(999)).thenReturn(null);
        when(mapper.map(null, SeekerDTO.class)).thenReturn(new SeekerDTO());

        SeekerDTO result = seekerService.findByAccountID(999);

        verify(seekerRepository).findByAccountID(999);
        assertNotNull(result);
    }

    @Test
    void testSave_Success() {
        // TC-SV-SEEKER-003
        // Mục tiêu: Kiểm tra lưu seeker thành công
        // Input: seekerDTO hợp lệ, mapper và repository hoạt động bình thường
        // Output mong đợi: trả về true, repository.save được gọi
        when(mapper.map(seekerDTO, Seeker.class)).thenReturn(seeker);
        when(seekerRepository.save(any(Seeker.class))).thenReturn(seeker);

        boolean result = seekerService.save(seekerDTO);

        verify(mapper).map(seekerDTO, Seeker.class);
        verify(seekerRepository).save(seeker);
        assertTrue(result);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        // TC-SV-SEEKER-004
        // Mục tiêu: Kiểm tra xử lý khi repository ném ngoại lệ trong quá trình save
        // Input: seekerDTO hợp lệ, repository.save ném RuntimeException
        // Output mong đợi: trả về false (exception được bắt trong try-catch)
        when(mapper.map(seekerDTO, Seeker.class)).thenReturn(seeker);
        when(seekerRepository.save(any(Seeker.class))).thenThrow(new RuntimeException("Database error"));

        boolean result = seekerService.save(seekerDTO);

        verify(seekerRepository).save(seeker);
        assertFalse(result);
    }

    @Test
    void testFindAll_ReturnsAll() {
        // TC-SV-SEEKER-005
        // Mục tiêu: Kiểm tra findAll trả về đầy đủ danh sách SeekerDTO
        // Input: repository có 1 seeker
        // Output mong đợi: List 1 phần tử, fullName="Nguyen Van A"
        List<Seeker> seekers = Arrays.asList(seeker);
        List<SeekerDTO> seekerDTOs = Arrays.asList(seekerDTO);
        when(seekerRepository.findAll()).thenReturn(seekers);
        when(mapper.map(any(Object.class), any(Type.class))).thenReturn(seekerDTOs);

        List<SeekerDTO> result = seekerService.findAll();

        verify(seekerRepository).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nguyen Van A", result.get(0).getFullName());
    }

    @Test
    void testFindAll_ReturnsEmpty() {
        // TC-SV-SEEKER-006
        // Mục tiêu: Kiểm tra findAll khi không có dữ liệu
        // Input: repository trả về danh sách rỗng
        // Output mong đợi: List rỗng, không null
        when(seekerRepository.findAll()).thenReturn(List.of());
        when(mapper.map(any(Object.class), any(Type.class))).thenReturn(List.of());

        List<SeekerDTO> result = seekerService.findAll();

        verify(seekerRepository).findAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindbyusername_Success() {
        // TC-SV-SEEKER-007
        // Mục tiêu: Kiểm tra tìm kiếm seeker theo username thành công
        // Input: username="testUser", repository trả về seeker hợp lệ
        // Output mong đợi: SeekerDTO không null với fullName="Nguyen Van A"
        when(seekerRepository.findByusername("testUser")).thenReturn(seeker);
        when(mapper.map(seeker, SeekerDTO.class)).thenReturn(seekerDTO);

        SeekerDTO result = seekerService.findbyusername("testUser");

        verify(seekerRepository).findByusername("testUser");
        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getFullName());
    }

    @Test
    void testFindbyusername_NotFound() {
        // TC-SV-SEEKER-008
        // Mục tiêu: Kiểm tra trường hợp không tìm thấy seeker theo username
        // Input: username="unknownUser", repository trả về null
        // Output mong đợi: SeekerDTO rỗng (mapper.map xử lý null)
        when(seekerRepository.findByusername("unknownUser")).thenReturn(null);
        when(mapper.map(null, SeekerDTO.class)).thenReturn(new SeekerDTO());

        SeekerDTO result = seekerService.findbyusername("unknownUser");

        verify(seekerRepository).findByusername("unknownUser");
        assertNotNull(result);
    }
}
