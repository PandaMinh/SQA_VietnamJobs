package com.demo.services;

import com.demo.entities.Type;
import com.demo.repositories.TypeRepository;
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
public class TypeServiceTest {

    @InjectMocks
    private TypeServiceImpl typeService;

    @Mock
    private TypeRepository typeRepository;

    private Type type;

    @BeforeEach
    void setUp() {
        type = new Type();
        type.setId(1);
        type.setName("Full-time");
        type.setStatus(true);
    }

    @Test
    void testFind_Success() {
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));

        Type result = typeService.find(1);

        assertSame(type, result);
        verify(typeRepository).findById(1);
    }

    @Test
    void testFindAll_ReturnsAll() {
        List<Type> types = List.of(type);
        when(typeRepository.findAll()).thenReturn(types);

        Iterable<Type> result = typeService.findAll();

        assertEquals(types, result);
        verify(typeRepository).findAll();
    }

    @Test
    void testSave_Success() {
        when(typeRepository.save(type)).thenReturn(type);

        boolean result = typeService.save(type);

        assertTrue(result);
        verify(typeRepository).save(type);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(typeRepository.save(type)).thenThrow(new RuntimeException("Database error"));

        boolean result = typeService.save(type);

        assertFalse(result);
        verify(typeRepository).save(type);
    }

    @Test
    void testDelete_Success() {
        when(typeRepository.findById(1)).thenReturn(Optional.of(type));

        boolean result = typeService.delete(1);

        assertTrue(result);
        verify(typeRepository).delete(type);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(typeRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = typeService.delete(999);

        assertFalse(result);
        verify(typeRepository, never()).delete(any(Type.class));
    }

    @Test
    void testExists_True() {
        when(typeRepository.exists("Full-time", 0)).thenReturn(1);

        boolean result = typeService.exists("Full-time", 0);

        assertTrue(result);
        verify(typeRepository).exists("Full-time", 0);
    }

    @Test
    void testExists_False() {
        when(typeRepository.exists("Remote", 0)).thenReturn(0);

        boolean result = typeService.exists("Remote", 0);

        assertFalse(result);
        verify(typeRepository).exists("Remote", 0);
    }

    @Test
    void testFindAllByStatus_ReturnsFilteredList() {
        List<Type> types = List.of(type);
        when(typeRepository.findAllByStatus(true)).thenReturn(types);

        List<Type> result = typeService.findAllByStatus(true);

        assertEquals(1, result.size());
        assertEquals("Full-time", result.get(0).getName());
        verify(typeRepository).findAllByStatus(true);
    }
}
