package com.demo.services;

import com.demo.entities.Rank;
import com.demo.repositories.RankRepository;
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
public class RankServiceTest {

    @InjectMocks
    private RankServiceImpl rankService;

    @Mock
    private RankRepository rankRepository;

    private Rank rank;

    @BeforeEach
    void setUp() {
        rank = new Rank();
        rank.setId(1);
        rank.setName("Senior");
        rank.setStatus(true);
    }

    @Test
    void testFind_Success() {
        when(rankRepository.findById(1)).thenReturn(Optional.of(rank));

        Rank result = rankService.find(1);

        assertSame(rank, result);
        verify(rankRepository).findById(1);
    }

    @Test
    void testFindAll_ReturnsAll() {
        List<Rank> ranks = List.of(rank);
        when(rankRepository.findAll()).thenReturn(ranks);

        Iterable<Rank> result = rankService.findAll();

        assertEquals(ranks, result);
        verify(rankRepository).findAll();
    }

    @Test
    void testSave_Success() {
        when(rankRepository.save(rank)).thenReturn(rank);

        boolean result = rankService.save(rank);

        assertTrue(result);
        verify(rankRepository).save(rank);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(rankRepository.save(rank)).thenThrow(new RuntimeException("Database error"));

        boolean result = rankService.save(rank);

        assertFalse(result);
        verify(rankRepository).save(rank);
    }

    @Test
    void testDelete_Success() {
        when(rankRepository.findById(1)).thenReturn(Optional.of(rank));

        boolean result = rankService.delete(1);

        assertTrue(result);
        verify(rankRepository).delete(rank);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(rankRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = rankService.delete(999);

        assertFalse(result);
        verify(rankRepository, never()).delete(any(Rank.class));
    }

    @Test
    void testExists_True() {
        when(rankRepository.exists("Senior", 0)).thenReturn(1);

        boolean result = rankService.exists("Senior", 0);

        assertTrue(result);
        verify(rankRepository).exists("Senior", 0);
    }

    @Test
    void testExists_False() {
        when(rankRepository.exists("Intern", 0)).thenReturn(0);

        boolean result = rankService.exists("Intern", 0);

        assertFalse(result);
        verify(rankRepository).exists("Intern", 0);
    }

    @Test
    void testFindAllByStatus_ReturnsFilteredList() {
        List<Rank> ranks = List.of(rank);
        when(rankRepository.findAllByStatus(true)).thenReturn(ranks);

        List<Rank> result = rankService.findAllByStatus(true);

        assertEquals(1, result.size());
        assertEquals("Senior", result.get(0).getName());
        verify(rankRepository).findAllByStatus(true);
    }
}
