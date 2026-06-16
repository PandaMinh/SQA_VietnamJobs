package com.demo.services;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.entities.ApplicationHistory;
import com.demo.entities.Postings;
import com.demo.entities.Seeker;
import com.demo.repositories.ApplicationHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ApplicationHistoryServiceTest {

    // TC-SV-AH-001 ~ TC-SV-AH-008
    // File: ApplicationHistoryServiceTest.java
    // Class: ApplicationHistoryServiceImpl

    @InjectMocks
    private ApplicationHistoryServiceImpl applicationHistoryService;

    @Mock
    private ApplicationHistoryRepository applicationHistoryRepository;

    @Mock
    private ModelMapper modelMapper;

    private ApplicationHistory applicationHistory;
    private ApplicationHistoryDTO applicationHistoryDTO;
    private Postings postings;
    private Seeker seeker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        postings = new Postings();
        postings.setId(1);
        postings.setTitle("Java Developer");

        seeker = new Seeker();
        seeker.setId(1);
        seeker.setFullname("Nguyen Van A");

        applicationHistory = new ApplicationHistory();
        applicationHistory.setId(1);
        applicationHistory.setPostings(postings);
        applicationHistory.setSeeker(seeker);
        applicationHistory.setCreated(new Date());
        applicationHistory.setStatus(0);
        applicationHistory.setResult(0);

        applicationHistoryDTO = new ApplicationHistoryDTO();
        applicationHistoryDTO.setId(1);
        applicationHistoryDTO.setPostingID(1);
        applicationHistoryDTO.setSeekerID(1);
        applicationHistoryDTO.setPostingTitle("Java Developer");
        applicationHistoryDTO.setSeekerName("Nguyen Van A");
        applicationHistoryDTO.setStatus(0);
        applicationHistoryDTO.setResult(0);
    }

    @Test
    void testSave_Success() {
        // TC-SV-AH-001
        // Mục tiêu: Kiểm tra lưu lịch sử ứng tuyển thành công
        // Input: applicationHistoryDTO hợp lệ với postingID=1, seekerID=1
        // Output mong đợi: trả về true, repository.save được gọi với đối tượng ApplicationHistory
        when(modelMapper.map(applicationHistoryDTO, ApplicationHistory.class)).thenReturn(applicationHistory);
        when(applicationHistoryRepository.save(any(ApplicationHistory.class))).thenReturn(applicationHistory);

        boolean result = applicationHistoryService.save(applicationHistoryDTO);

        verify(modelMapper).map(applicationHistoryDTO, ApplicationHistory.class);
        verify(applicationHistoryRepository).save(any(ApplicationHistory.class));
        assertTrue(result);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        // TC-SV-AH-002
        // Mục tiêu: Kiểm tra xử lý khi repository ném ngoại lệ trong quá trình save
        // Input: applicationHistoryDTO hợp lệ, repository.save ném RuntimeException
        // Output mong đợi: trả về false (exception được bắt trong try-catch)
        when(modelMapper.map(applicationHistoryDTO, ApplicationHistory.class)).thenReturn(applicationHistory);
        when(applicationHistoryRepository.save(any(ApplicationHistory.class))).thenThrow(new RuntimeException("Database error"));

        boolean result = applicationHistoryService.save(applicationHistoryDTO);

        verify(applicationHistoryRepository).save(any(ApplicationHistory.class));
        assertFalse(result);
    }

    @Test
    void testExistByPostId_True() {
        // TC-SV-AH-003
        // Mục tiêu: Kiểm tra trả về true khi có ít nhất một đơn ứng tuyển theo postId
        // Input: postId=1, countByPostId trả về 3
        // Output mong đợi: true
        when(applicationHistoryRepository.countByPostId(1)).thenReturn(3);

        boolean result = applicationHistoryService.existByPostId(1);

        verify(applicationHistoryRepository).countByPostId(1);
        assertTrue(result);
    }

    @Test
    void testExistByPostId_False() {
        // TC-SV-AH-004
        // Mục tiêu: Kiểm tra trả về false khi không có đơn ứng tuyển theo postId
        // Input: postId=999, countByPostId trả về 0
        // Output mong đợi: false
        when(applicationHistoryRepository.countByPostId(999)).thenReturn(0);

        boolean result = applicationHistoryService.existByPostId(999);

        verify(applicationHistoryRepository).countByPostId(999);
        assertFalse(result);
    }

    @Test
    void testCountAll_ReturnsPositive() {
        // TC-SV-AH-005
        // Mục tiêu: Kiểm tra countAll khi có dữ liệu (count > 0)
        // Input: countAll repository trả về 10
        // Output mong đợi: 10 (repository được gọi 2 lần do pattern if-then)
        when(applicationHistoryRepository.countAll()).thenReturn(10);

        int result = applicationHistoryService.countAll();

        verify(applicationHistoryRepository, times(2)).countAll();
        assertEquals(10, result);
    }

    @Test
    void testCountAll_ReturnsZero() {
        // TC-SV-AH-006
        // Mục tiêu: Kiểm tra countAll khi không có dữ liệu (count = 0)
        // Input: countAll repository trả về 0
        // Output mong đợi: 0 (repository chỉ được gọi 1 lần)
        when(applicationHistoryRepository.countAll()).thenReturn(0);

        int result = applicationHistoryService.countAll();

        verify(applicationHistoryRepository, times(1)).countAll();
        assertEquals(0, result);
    }

    @Test
    void testCountByResult_ReturnsPositive() {
        // TC-SV-AH-007
        // Mục tiêu: Kiểm tra countByResult khi có kết quả (count > 0)
        // Input: result=1 (đã duyệt), countByResult trả về 5
        // Output mong đợi: 5 (repository được gọi 2 lần)
        when(applicationHistoryRepository.countByResult(1)).thenReturn(5);

        int result = applicationHistoryService.countByResult(1);

        verify(applicationHistoryRepository, times(2)).countByResult(1);
        assertEquals(5, result);
    }

    @Test
    void testCountByResult_ReturnsZero() {
        // TC-SV-AH-008
        // Mục tiêu: Kiểm tra countByResult khi không có kết quả (count = 0)
        // Input: result=2 (từ chối), countByResult trả về 0
        // Output mong đợi: 0 (repository chỉ được gọi 1 lần)
        when(applicationHistoryRepository.countByResult(2)).thenReturn(0);

        int result = applicationHistoryService.countByResult(2);

        verify(applicationHistoryRepository, times(1)).countByResult(2);
        assertEquals(0, result);
    }
}
