package com.demo.services;

import com.demo.dtos.PostingspaymentDTO;
import com.demo.entities.Postings;
import com.demo.entities.Postingspayment;
import com.demo.repositories.PostingsPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class PostingPaymentServiceTest {

    @InjectMocks
    private PostingPaymentServiceImpl postingPaymentService;

    @Mock
    private PostingsPaymentRepository postingsPaymentRepository;

    @Mock
    private ModelMapper mapper;

    private Postingspayment payment;
    private PostingspaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        Postings postings = new Postings();
        postings.setId(1);
        postings.setTitle("Java Developer");

        payment = new Postingspayment();
        payment.setId(1);
        payment.setPostings(postings);
        payment.setCost(100000);
        payment.setCreated(new Date());
        payment.setDuration(30);
        payment.setStatus(true);

        paymentDTO = new PostingspaymentDTO();
        paymentDTO.setId(1);
        paymentDTO.setPostingsid(1);
        paymentDTO.setCost(100000);
        paymentDTO.setCreated(new Date());
        paymentDTO.setDuration(30);
        paymentDTO.setStatus(true);
    }

    @Test
    void testFindAll_ReturnsMappedDtos() {
        List<Postingspayment> payments = List.of(payment);
        List<PostingspaymentDTO> dtos = List.of(paymentDTO);
        when(postingsPaymentRepository.findAll()).thenReturn(payments);
        doReturn(dtos).when(mapper).map(eq(payments), any(Type.class));

        List<PostingspaymentDTO> result = postingPaymentService.findAll();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getPostingsid());
        verify(postingsPaymentRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        when(postingsPaymentRepository.findById(1)).thenReturn(Optional.of(payment));
        when(mapper.map(payment, PostingspaymentDTO.class)).thenReturn(paymentDTO);

        PostingspaymentDTO result = postingPaymentService.findbyid(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(postingsPaymentRepository).findById(1);
    }

    @Test
    void testSave_Success() {
        when(mapper.map(paymentDTO, Postingspayment.class)).thenReturn(payment);
        when(postingsPaymentRepository.save(payment)).thenReturn(payment);

        boolean result = postingPaymentService.save(paymentDTO);

        assertTrue(result);
        verify(postingsPaymentRepository).save(payment);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        when(mapper.map(paymentDTO, Postingspayment.class)).thenReturn(payment);
        when(postingsPaymentRepository.save(payment)).thenThrow(new RuntimeException("Database error"));

        boolean result = postingPaymentService.save(paymentDTO);

        assertFalse(result);
        verify(postingsPaymentRepository).save(payment);
    }

    @Test
    void testDelete_Success() {
        when(postingsPaymentRepository.findById(1)).thenReturn(Optional.of(payment));

        boolean result = postingPaymentService.delete(1);

        assertTrue(result);
        verify(postingsPaymentRepository).delete(payment);
    }

    @Test
    void testDelete_Failure_NotFound() {
        when(postingsPaymentRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = postingPaymentService.delete(999);

        assertFalse(result);
        verify(postingsPaymentRepository, never()).delete(any(Postingspayment.class));
    }

    @Test
    void testLimit_ReturnsMappedDtos() {
        List<Postingspayment> payments = List.of(payment);
        List<PostingspaymentDTO> dtos = List.of(paymentDTO);
        when(postingsPaymentRepository.limit(true, 5)).thenReturn(payments);
        doReturn(dtos).when(mapper).map(eq(payments), any(Type.class));

        List<PostingspaymentDTO> result = postingPaymentService.limit(true, 5);

        assertEquals(1, result.size());
        verify(postingsPaymentRepository).limit(true, 5);
    }

    @Test
    void testLimitByCategory_ReturnsMappedDtos() {
        List<Postingspayment> payments = List.of(payment);
        List<PostingspaymentDTO> dtos = List.of(paymentDTO);
        when(postingsPaymentRepository.limitbycategory(true, 3, "IT")).thenReturn(payments);
        doReturn(dtos).when(mapper).map(eq(payments), any(Type.class));

        List<PostingspaymentDTO> result = postingPaymentService.limitbycategory(true, "IT", 3);

        assertEquals(1, result.size());
        verify(postingsPaymentRepository).limitbycategory(true, 3, "IT");
    }

    @Test
    void testFindByPostingsId_ReturnsMappedDtos() {
        List<Postingspayment> payments = List.of(payment);
        List<PostingspaymentDTO> dtos = List.of(paymentDTO);
        when(postingsPaymentRepository.findbypostingsid(1, true)).thenReturn(payments);
        doReturn(dtos).when(mapper).map(eq(payments), any(Type.class));

        List<PostingspaymentDTO> result = postingPaymentService.findbypostingsid(1, true);

        assertEquals(1, result.size());
        verify(postingsPaymentRepository).findbypostingsid(1, true);
    }

    @Test
    void testOrderByCost_ReturnsMappedDtos() {
        List<Postingspayment> payments = List.of(payment);
        List<PostingspaymentDTO> dtos = List.of(paymentDTO);
        when(postingsPaymentRepository.orderbycost(true)).thenReturn(payments);
        doReturn(dtos).when(mapper).map(eq(payments), any(Type.class));

        List<PostingspaymentDTO> result = postingPaymentService.orderbycost();

        assertEquals(1, result.size());
        verify(postingsPaymentRepository).orderbycost(true);
    }
}
