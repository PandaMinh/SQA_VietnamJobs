package com.demo.services;

import com.demo.dtos.TransactionHistoryDTO;
import com.demo.entities.Account;
import com.demo.entities.TransactionHistory;
import com.demo.repositories.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionHistoryServiceTest {

    @InjectMocks
    private TransactionHistoryServiceImpl transactionHistoryService;

    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private ModelMapper mapper;

    private TransactionHistory transactionHistory;
    private TransactionHistoryDTO transactionHistoryDTO;

    @BeforeEach
    void setUp() {
        Account account = new Account();
        account.setId(1);

        transactionHistory = new TransactionHistory();
        transactionHistory.setId(1);
        transactionHistory.setAccount(account);
        transactionHistory.setTotal(500000);
        transactionHistory.setCreated(new Date());
        transactionHistory.setTradingCode(123456);
        transactionHistory.setStatus(true);

        transactionHistoryDTO = new TransactionHistoryDTO();
        transactionHistoryDTO.setId(1);
        transactionHistoryDTO.setAccountid(1);
        transactionHistoryDTO.setTotal(500000);
        transactionHistoryDTO.setCreated(new Date());
        transactionHistoryDTO.setTradingCode(123456);
        transactionHistoryDTO.setStatus(true);
    }

    @Test
    void testDelete_Success() {
        when(transactionHistoryRepository.findById(1)).thenReturn(Optional.of(transactionHistory));

        boolean result = transactionHistoryService.delete(1);

        assertTrue(result);
        verify(transactionHistoryRepository).delete(transactionHistory);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(transactionHistoryRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = transactionHistoryService.delete(999);

        assertFalse(result);
        verify(transactionHistoryRepository, never()).delete(any(TransactionHistory.class));
    }

    @Test
    void testSave_Success_SetsAccountOnEntity() {
        when(mapper.map(transactionHistoryDTO, TransactionHistory.class)).thenReturn(transactionHistory);
        when(transactionHistoryRepository.save(any(TransactionHistory.class))).thenReturn(transactionHistory);

        boolean result = transactionHistoryService.save(transactionHistoryDTO);

        ArgumentCaptor<TransactionHistory> captor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionHistoryRepository).save(captor.capture());
        assertTrue(result);
        assertNotNull(captor.getValue().getAccount());
        assertEquals(1, captor.getValue().getAccount().getId());
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(mapper.map(transactionHistoryDTO, TransactionHistory.class)).thenReturn(transactionHistory);
        when(transactionHistoryRepository.save(any(TransactionHistory.class)))
                .thenThrow(new RuntimeException("Database error"));

        boolean result = transactionHistoryService.save(transactionHistoryDTO);

        assertFalse(result);
        verify(transactionHistoryRepository).save(any(TransactionHistory.class));
    }

    @Test
    void testFindAll_ReturnsMappedDtos() {
        List<TransactionHistory> entities = List.of(transactionHistory);
        List<TransactionHistoryDTO> dtos = List.of(transactionHistoryDTO);
        when(transactionHistoryRepository.findAll()).thenReturn(entities);
        doReturn(dtos).when(mapper).map(eq(entities), any(Type.class));

        List<TransactionHistoryDTO> result = transactionHistoryService.findAll();

        assertEquals(1, result.size());
        assertEquals(500000, result.get(0).getTotal());
        verify(transactionHistoryRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        when(transactionHistoryRepository.findById(1)).thenReturn(Optional.of(transactionHistory));
        when(mapper.map(transactionHistory, TransactionHistoryDTO.class)).thenReturn(transactionHistoryDTO);

        TransactionHistoryDTO result = transactionHistoryService.findbyid(1);

        assertNotNull(result);
        assertEquals(123456, result.getTradingCode());
        verify(transactionHistoryRepository).findById(1);
    }

    @Test
    void testFindByAccountId_ReturnsMappedList() {
        List<TransactionHistoryDTO> dtos = List.of(transactionHistoryDTO);
        when(transactionHistoryRepository.findByAccountId(1)).thenReturn(transactionHistory);
        doReturn(dtos).when(mapper).map(eq(transactionHistory), any(Type.class));

        List<TransactionHistoryDTO> result = transactionHistoryService.findbyaccountid(1);

        assertEquals(1, result.size());
        verify(transactionHistoryRepository).findByAccountId(1);
    }

    @Test
    void testTotalByAccountId_ReturnsRepositoryValue() {
        when(transactionHistoryRepository.totalbyemployerid(1)).thenReturn(750000d);

        double result = transactionHistoryService.totalbyaccountid(1);

        assertEquals(750000d, result);
        verify(transactionHistoryRepository).totalbyemployerid(1);
    }
}
