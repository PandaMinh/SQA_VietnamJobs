package com.demo.services;

import com.demo.entities.Local;
import com.demo.repositories.LocalRepository;
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
public class LocalServiceTest {

    @InjectMocks
    private LocalServiceImpl localService;

    @Mock
    private LocalRepository localRepository;

    private Local local;

    @BeforeEach
    void setUp() {
        local = new Local();
        local.setId(1);
        local.setName("Ha Noi");
        local.setStatus(true);
    }

    @Test
    void testFind_Success() {
        when(localRepository.findById(1)).thenReturn(Optional.of(local));

        Local result = localService.find(1);

        assertSame(local, result);
        verify(localRepository).findById(1);
    }

    @Test
    void testFindAll_ReturnsAll() {
        List<Local> locals = List.of(local);
        when(localRepository.findAll()).thenReturn(locals);

        Iterable<Local> result = localService.findAll();

        assertEquals(locals, result);
        verify(localRepository).findAll();
    }

    @Test
    void testSave_Success() {
        when(localRepository.save(local)).thenReturn(local);

        boolean result = localService.save(local);

        assertTrue(result);
        verify(localRepository).save(local);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(localRepository.save(local)).thenThrow(new RuntimeException("Database error"));

        boolean result = localService.save(local);

        assertFalse(result);
        verify(localRepository).save(local);
    }

    @Test
    void testDelete_Success() {
        when(localRepository.findById(1)).thenReturn(Optional.of(local));

        boolean result = localService.delete(1);

        assertTrue(result);
        verify(localRepository).findById(1);
        verify(localRepository).delete(local);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(localRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = localService.delete(999);

        assertFalse(result);
        verify(localRepository).findById(999);
        verify(localRepository, never()).delete(any(Local.class));
    }

    @Test
    void testExists_True() {
        when(localRepository.exists("Ha Noi", 0)).thenReturn(1);

        boolean result = localService.exists("Ha Noi", 0);

        assertTrue(result);
        verify(localRepository).exists("Ha Noi", 0);
    }

    @Test
    void testExists_False() {
        when(localRepository.exists("Da Nang", 0)).thenReturn(0);

        boolean result = localService.exists("Da Nang", 0);

        assertFalse(result);
        verify(localRepository).exists("Da Nang", 0);
    }

    @Test
    void testFindAllByStatus_ReturnsFilteredList() {
        List<Local> locals = List.of(local);
        when(localRepository.findAllByStatus(true)).thenReturn(locals);

        List<Local> result = localService.findAllByStatus(true);

        assertEquals(1, result.size());
        assertEquals("Ha Noi", result.get(0).getName());
        verify(localRepository).findAllByStatus(true);
    }
}
