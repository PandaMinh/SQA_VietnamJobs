package com.demo.services;

import com.demo.entities.Experience;
import com.demo.repositories.ExperienceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceServiceTest {

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    @Mock
    private ExperienceRepository experienceRepository;

    private Experience experience;

    @BeforeEach
    void setUp() {
        experience = new Experience();
        experience.setId(1);
        experience.setName("2 nam");
        experience.setStatus(true);
    }

    @Test
    void testFind_Success() {
        when(experienceRepository.findById(1)).thenReturn(Optional.of(experience));

        Experience result = experienceService.find(1);

        assertSame(experience, result);
        verify(experienceRepository).findById(1);
    }

    @Test
    void testFindAll_ReturnsAll() {
        List<Experience> experiences = List.of(experience);
        when(experienceRepository.findAll()).thenReturn(experiences);

        Iterable<Experience> result = experienceService.findAll();

        assertEquals(experiences, result);
        verify(experienceRepository).findAll();
    }

    @Test
    void testSave_Success() {
        when(experienceRepository.save(experience)).thenReturn(experience);

        boolean result = experienceService.save(experience);

        assertTrue(result);
        verify(experienceRepository).save(experience);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(experienceRepository.save(experience)).thenThrow(new RuntimeException("Database error"));

        boolean result = experienceService.save(experience);

        assertFalse(result);
        verify(experienceRepository).save(experience);
    }

    @Test
    void testDelete_Success() {
        when(experienceRepository.findById(1)).thenReturn(Optional.of(experience));

        boolean result = experienceService.delete(1);

        assertTrue(result);
        verify(experienceRepository).delete(experience);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(experienceRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = experienceService.delete(999);

        assertFalse(result);
        verify(experienceRepository, never()).delete(any(Experience.class));
    }

    @Test
    void testExists_True() {
        when(experienceRepository.exists("2 nam", 0)).thenReturn(1);

        boolean result = experienceService.exists("2 nam", 0);

        assertTrue(result);
        verify(experienceRepository).exists("2 nam", 0);
    }

    @Test
    void testExists_False() {
        when(experienceRepository.exists("5 nam", 0)).thenReturn(0);

        boolean result = experienceService.exists("5 nam", 0);

        assertFalse(result);
        verify(experienceRepository).exists("5 nam", 0);
    }

    @Test
    void testFindAllByStatus_ReturnsFilteredList() {
        List<Experience> experiences = List.of(experience);
        when(experienceRepository.findAllByStatus(true)).thenReturn(experiences);

        List<Experience> result = experienceService.findAllByStatus(true);

        assertEquals(1, result.size());
        assertEquals("2 nam", result.get(0).getName());
        verify(experienceRepository).findAllByStatus(true);
    }
}
