package com.demo.services;

import com.demo.dtos.FollowDTO;
import com.demo.entities.Follow;
import com.demo.repositories.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FollowServiceTest {

    // TC-SV-FOLLOW-001 ~ TC-SV-FOLLOW-006
    // File: FollowServiceTest.java
    // Class: FollowServiceImpl

    @InjectMocks
    private FollowServiceImpl followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private BCryptPasswordEncoder encoder;

    private Follow follow;
    private FollowDTO followDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        follow = new Follow();
        follow.setId(1);

        followDTO = new FollowDTO();
        followDTO.setId(1);
        followDTO.setEmployerName("Tech Company");
        followDTO.setSeekerName("Nguyen Van A");
        followDTO.setStatus(true);
    }

    @Test
    void testFindBySeekerId_ReturnsFollowList() {
        // TC-SV-FOLLOW-001
        // Mục tiêu: Kiểm tra lấy danh sách follow theo seekerId thành công
        // Input: seekerId=1, repository trả về 1 follow
        // Output mong đợi: List 1 phần tử, employerName="Tech Company"
        List<Follow> follows = Arrays.asList(follow);
        List<FollowDTO> followDTOs = Arrays.asList(followDTO);
        when(followRepository.findBySeekerId(1)).thenReturn(follows);
        when(mapper.map(any(Object.class), any(Type.class))).thenReturn(followDTOs);

        List<FollowDTO> result = followService.findBySeekerId(1);

        verify(followRepository).findBySeekerId(1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tech Company", result.get(0).getEmployerName());
        assertEquals("Nguyen Van A", result.get(0).getSeekerName());
    }

    @Test
    void testFindBySeekerId_ReturnsEmpty() {
        // TC-SV-FOLLOW-002
        // Mục tiêu: Kiểm tra khi seeker không follow bất kỳ employer nào
        // Input: seekerId=999, repository trả về danh sách rỗng
        // Output mong đợi: List rỗng, không null
        when(followRepository.findBySeekerId(999)).thenReturn(List.of());
        when(mapper.map(any(Object.class), any(Type.class))).thenReturn(List.of());

        List<FollowDTO> result = followService.findBySeekerId(999);

        verify(followRepository).findBySeekerId(999);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCountByEmployerId_ReturnsPositive() {
        // TC-SV-FOLLOW-003
        // Mục tiêu: Kiểm tra đếm số lượng follow theo employerId khi có dữ liệu
        // Input: employerId=1, countByEmployerId trả về 5
        // Output mong đợi: 5 (repository được gọi 2 lần do pattern if-then)
        when(followRepository.countByEmployerId(1)).thenReturn(5);

        int result = followService.countByEmployerId(1);

        verify(followRepository, times(2)).countByEmployerId(1);
        assertEquals(5, result);
    }

    @Test
    void testCountByEmployerId_ReturnsZero() {
        // TC-SV-FOLLOW-004
        // Mục tiêu: Kiểm tra đếm số lượng follow khi employer chưa được follow
        // Input: employerId=999, countByEmployerId trả về 0
        // Output mong đợi: 0 (repository chỉ được gọi 1 lần)
        when(followRepository.countByEmployerId(999)).thenReturn(0);

        int result = followService.countByEmployerId(999);

        verify(followRepository, times(1)).countByEmployerId(999);
        assertEquals(0, result);
    }

    @Test
    void testFindBySeekerId1_Success() {
        // TC-SV-FOLLOW-005
        // Mục tiêu: Kiểm tra lấy một follow duy nhất theo seekerId thành công
        // Input: seekerId=1, repository trả về đối tượng Follow
        // Output mong đợi: FollowDTO không null với employerName="Tech Company"
        when(followRepository.findBySeekerId1(1)).thenReturn(follow);
        when(mapper.map(follow, FollowDTO.class)).thenReturn(followDTO);

        FollowDTO result = followService.findBySeekerId1(1);

        verify(followRepository).findBySeekerId1(1);
        assertNotNull(result);
        assertEquals("Tech Company", result.getEmployerName());
        assertEquals(1, result.getId());
    }

    @Test
    void testFindBySeekerId1_Exception_ReturnsNull() {
        // TC-SV-FOLLOW-006
        // Mục tiêu: Kiểm tra xử lý khi repository ném ngoại lệ trong findBySeekerId1
        // Input: seekerId=1, repository.findBySeekerId1 ném RuntimeException
        // Output mong đợi: null (exception được bắt trong try-catch, trả về null)
        when(followRepository.findBySeekerId1(1)).thenThrow(new RuntimeException("Database error"));

        FollowDTO result = followService.findBySeekerId1(1);

        verify(followRepository).findBySeekerId1(1);
        assertNull(result);
    }
}
