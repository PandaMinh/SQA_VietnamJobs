package com.demo.services;

import com.demo.dtos.PostingDTO;
import com.demo.entities.Category;
import com.demo.entities.Employer;
import com.demo.entities.Experience;
import com.demo.entities.Local;
import com.demo.entities.Postings;
import com.demo.entities.Rank;
import com.demo.entities.Type;
import com.demo.entities.Wage;
import com.demo.repositories.PostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PostingServiceTest {

    @InjectMocks
    private PostingServiceImpl postingService;

    @Mock
    private PostingRepository postingRepository;

    @Mock
    private ModelMapper modelMapper;

    private Postings posting;
    private PostingDTO postingDTO;
    private Postings posting1;
    private Postings posting2;
    private Category category;
    private Employer employer;
    private Experience experience;
    private Local local;
    private Rank rank;
    private Type type;
    private Wage wage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo các đối tượng liên quan
        category = new Category();
        employer = new Employer();
        experience = new Experience();
        local = new Local();
        rank = new Rank();
        type = new Type();
        wage = new Wage();

        // Khởi tạo Postings cho kiểm thử
        posting = new Postings();
        posting.setId(1);
        posting.setTitle("Dev Java");
        posting.setDescription("Develop Java applications");
        posting.setCreated(new Date());
        posting.setDeadline(new Date());
        posting.setGender("Male");
        posting.setQuantity(5);
        posting.setOpen(true);
        posting.setStatus(false);
        posting.setCategory(category);
        posting.setEmployer(employer);
        posting.setExperience(experience);
        posting.setLocal(local);
        posting.setRank(rank);
        posting.setType(type);
        posting.setWage(wage);

        // Khởi tạo PostingDTO
        postingDTO = new PostingDTO();
        postingDTO.setId(1);
        postingDTO.setTitle("Dev Java");
        postingDTO.setDescription("Develop Java applications");
        postingDTO.setCreated(new Date());
        postingDTO.setDealine(new Date());
        postingDTO.setGender("Male");
        postingDTO.setQuantity(5);
        postingDTO.setStatus(false);
        postingDTO.setWageName("10-15M");
        postingDTO.setCategoryName("IT");
        postingDTO.setLocalName("Hanoi");
        postingDTO.setRankName("Junior");
        postingDTO.setTypeName("Full-time");
        postingDTO.setExpName("1-2 years");

        // Khởi tạo Postings cho kiểm thử getAll
        posting1 = new Postings();
        posting1.setId(1);
        posting1.setTitle("Java Developer");
        posting1.setCreated(new Date());
        posting1.setDeadline(new Date());
        posting1.setStatus(true);
        posting1.setCategory(category);
        posting1.setEmployer(employer);
        posting1.setExperience(experience);
        posting1.setLocal(local);
        posting1.setRank(rank);
        posting1.setType(type);
        posting1.setWage(wage);

        posting2 = new Postings();
        posting2.setId(2);
        posting2.setTitle("Frontend Developer");
        posting2.setCreated(new Date());
        posting2.setDeadline(new Date());
        posting2.setStatus(false);
        posting2.setCategory(category);
        posting2.setEmployer(employer);
        posting2.setExperience(experience);
        posting2.setLocal(local);
        posting2.setRank(rank);
        posting2.setType(type);
        posting2.setWage(wage);
    }

    @Test
    void testSaveDB_Success() {
        when(postingRepository.save(any(Postings.class))).thenReturn(posting);

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertTrue(result);
        assertEquals("Dev Java", posting.getTitle());
        assertEquals("Develop Java applications", posting.getDescription());
        assertTrue(posting.isOpen());
        assertFalse(posting.isStatus());
        assertNotNull(posting.getCreated());
        assertNotNull(posting.getDeadline());
        assertEquals("Male", posting.getGender());
        assertEquals(5, posting.getQuantity());
        assertNotNull(posting.getCategory());
        assertNotNull(posting.getEmployer());
        assertNotNull(posting.getExperience());
        assertNotNull(posting.getLocal());
        assertNotNull(posting.getRank());
        assertNotNull(posting.getType());
        assertNotNull(posting.getWage());
    }

    @Test
    void testSaveDB_Failure_NullTitle() {
        posting.setTitle(null);
        when(postingRepository.save(any(Postings.class))).thenThrow(new IllegalArgumentException("Title cannot be null"));

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testSaveDB_Failure_NullDescription() {
        posting.setDescription(null);
        when(postingRepository.save(any(Postings.class))).thenThrow(new IllegalArgumentException("Description cannot be null"));

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testSaveDB_Failure_NullCategory() {
        posting.setCategory(null);
        when(postingRepository.save(any(Postings.class))).thenThrow(new IllegalArgumentException("Category cannot be null"));

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testSaveDB_Failure_NegativeQuantity() {
        posting.setQuantity(-1);
        when(postingRepository.save(any(Postings.class))).thenThrow(new IllegalArgumentException("Quantity cannot be negative"));

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testSaveDB_Failure_TitleTooLong() {
        String longTitle = "A".repeat(251); // Vượt quá giới hạn 250 ký tự
        posting.setTitle(longTitle);
        when(postingRepository.save(any(Postings.class))).thenThrow(new IllegalArgumentException("Title exceeds maximum length"));

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testSaveDB_Failure_DatabaseError() {
        when(postingRepository.save(any(Postings.class))).thenThrow(new RuntimeException("Database error"));

        boolean result = postingService.saveDB(posting);

        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testSavePosting_Success() {
        when(modelMapper.map(postingDTO, Postings.class)).thenReturn(posting);
        when(postingRepository.save(any(Postings.class))).thenReturn(posting);

        boolean result = postingService.save(postingDTO);

        verify(modelMapper, times(1)).map(postingDTO, Postings.class);
        verify(postingRepository, times(1)).save(posting);
        assertTrue(result);
        assertEquals(postingDTO.getTitle(), posting.getTitle());
        assertEquals(postingDTO.getDescription(), posting.getDescription());
        assertEquals(postingDTO.getCreated(), posting.getCreated());
        assertEquals(postingDTO.getDealine(), posting.getDeadline());
        assertEquals(postingDTO.getGender(), posting.getGender());
        assertEquals(postingDTO.getQuantity(), posting.getQuantity());
    }

    @Test
    void testSavePosting_Failure_MappingError() {
        when(modelMapper.map(postingDTO, Postings.class)).thenThrow(new RuntimeException("Mapping error"));

        boolean result = postingService.save(postingDTO);

        verify(modelMapper, times(1)).map(postingDTO, Postings.class);
        verify(postingRepository, never()).save(any(Postings.class));
        assertFalse(result);
    }

    @Test
    void testSavePosting_Failure_DatabaseError() {
        when(modelMapper.map(postingDTO, Postings.class)).thenReturn(posting);
        when(postingRepository.save(any(Postings.class))).thenThrow(new RuntimeException("Database error"));

        boolean result = postingService.save(postingDTO);

        verify(modelMapper, times(1)).map(postingDTO, Postings.class);
        verify(postingRepository, times(1)).save(posting);
        assertFalse(result);
    }

    @Test
    void testGetAll_ReturnsAllPostings() {
        List<Postings> postingsList = Arrays.asList(posting1, posting2);
        when(postingRepository.findAll()).thenReturn(postingsList);

        Iterable<Postings> result = postingService.getAll();

        verify(postingRepository, times(1)).findAll();
        assertNotNull(result);
        List<Postings> resultList = (List<Postings>) result;
        assertEquals(2, resultList.size());
        assertEquals("Java Developer", resultList.get(0).getTitle());
        assertEquals(1, resultList.get(0).getId());
        assertTrue(resultList.get(0).isStatus());
        assertEquals("Frontend Developer", resultList.get(1).getTitle());
        assertEquals(2, resultList.get(1).getId());
        assertFalse(resultList.get(1).isStatus());
    }

    @Test
    void testGetAll_ReturnsEmptyList() {
        when(postingRepository.findAll()).thenReturn(List.of());

        Iterable<Postings> result = postingService.getAll();

        verify(postingRepository, times(1)).findAll();
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void testGetAll_DatabaseError() {
        when(postingRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> postingService.getAll());
        verify(postingRepository, times(1)).findAll();
    }

    @Test
    void testSearch_WithFilters_ReturnsMappedResults() {
        List<Postings> postingsList = Arrays.asList(posting1, posting2);
        List<PostingDTO> postingDTOs = Arrays.asList(postingDTO, new PostingDTO());
        when(postingRepository.search(1, 2, 3, 4, 5, "Java")).thenReturn(postingsList);
        when(modelMapper.map(any(), any(java.lang.reflect.Type.class))).thenReturn(postingDTOs);

        List<PostingDTO> result = postingService.search(1, 2, 3, 4, 5, "Java");

        verify(postingRepository).search(1, 2, 3, 4, 5, "Java");
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindById_Success() {
        when(postingRepository.findById(1)).thenReturn(Optional.of(posting));
        when(modelMapper.map(posting, PostingDTO.class)).thenReturn(postingDTO);

        PostingDTO result = postingService.findById(1);

        verify(postingRepository).findById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Dev Java", result.getTitle());
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        when(postingRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> postingService.findById(999));
        verify(postingRepository).findById(999);
    }

    @Test
    void testCountByEmployerId_ReturnsPositive() {
        when(postingRepository.countByEmployerId(1)).thenReturn(4);

        int result = postingService.countByEmployerId(1);

        verify(postingRepository, times(2)).countByEmployerId(1);
        assertEquals(4, result);
    }

    @Test
    void testCountByEmployerId_ReturnsZero() {
        when(postingRepository.countByEmployerId(99)).thenReturn(0);

        int result = postingService.countByEmployerId(99);

        verify(postingRepository, times(1)).countByEmployerId(99);
        assertEquals(0, result);
    }

    @Test
    void testUpdateStatusById_Success() {
        when(postingRepository.updateStatusById(1, true)).thenReturn(1);

        boolean result = postingService.updateStatusById(1, true);

        verify(postingRepository).updateStatusById(1, true);
        assertTrue(result);
    }

    @Test
    void testUpdateStatusById_Failure() {
        when(postingRepository.updateStatusById(1, false)).thenReturn(0);

        boolean result = postingService.updateStatusById(1, false);

        verify(postingRepository).updateStatusById(1, false);
        assertFalse(result);
    }

    @Test
    void testCountByMonthAndYear_ReturnsPositive() {
        when(postingRepository.countByMonthAndYear(6, 2026)).thenReturn(7);

        int result = postingService.countByMonthAndYear(6, 2026);

        verify(postingRepository, times(2)).countByMonthAndYear(6, 2026);
        assertEquals(7, result);
    }

    @Test
    void testCountByMonthAndYear_ReturnsZero() {
        when(postingRepository.countByMonthAndYear(1, 2030)).thenReturn(0);

        int result = postingService.countByMonthAndYear(1, 2030);

        verify(postingRepository, times(1)).countByMonthAndYear(1, 2030);
        assertEquals(0, result);
    }
}
