package com.demo.services;

import com.demo.entities.Wage;
import com.demo.repositories.WageRepository;
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
public class WageServiceTest {

    @InjectMocks
    private WageServiceImpl wageService;

    @Mock
    private WageRepository wageRepository;

    private Wage wage;

    @BeforeEach
    void setUp() {
        wage = new Wage();
        wage.setId(1);
        wage.setName("10 - 20 trieu");
        wage.setMin(10);
        wage.setMax(20);
        wage.setStatus(true);
    }

    @Test
    void testFind_Success() {
        when(wageRepository.findById(1)).thenReturn(Optional.of(wage));

        Wage result = wageService.find(1);

        assertSame(wage, result);
        verify(wageRepository).findById(1);
    }

    @Test
    void testFindAll_ReturnsAll() {
        List<Wage> wages = List.of(wage);
        when(wageRepository.findAll()).thenReturn(wages);

        Iterable<Wage> result = wageService.findAll();

        assertEquals(wages, result);
        verify(wageRepository).findAll();
    }

    @Test
    void testSave_Success() {
        when(wageRepository.save(wage)).thenReturn(wage);

        boolean result = wageService.save(wage);

        assertTrue(result);
        verify(wageRepository).save(wage);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(wageRepository.save(wage)).thenThrow(new RuntimeException("Database error"));

        boolean result = wageService.save(wage);

        assertFalse(result);
        verify(wageRepository).save(wage);
    }

    @Test
    void testDelete_Success() {
        when(wageRepository.findById(1)).thenReturn(Optional.of(wage));

        boolean result = wageService.delete(1);

        assertTrue(result);
        verify(wageRepository).delete(wage);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(wageRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = wageService.delete(999);

        assertFalse(result);
        verify(wageRepository, never()).delete(any(Wage.class));
    }

    @Test
    void testExists_True() {
        when(wageRepository.exists(10, 20, 0)).thenReturn(1);

        boolean result = wageService.exists(10, 20, 0);

        assertTrue(result);
        verify(wageRepository).exists(10, 20, 0);
    }

    @Test
    void testExists_False() {
        when(wageRepository.exists(30, 40, 0)).thenReturn(0);

        boolean result = wageService.exists(30, 40, 0);

        assertFalse(result);
        verify(wageRepository).exists(30, 40, 0);
    }

    @Test
    void testFindAllByStatus_ReturnsFilteredList() {
        List<Wage> wages = List.of(wage);
        when(wageRepository.findAllByStatus(true)).thenReturn(wages);

        List<Wage> result = wageService.findAllByStatus(true);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getMin());
        verify(wageRepository).findAllByStatus(true);
    }
}
