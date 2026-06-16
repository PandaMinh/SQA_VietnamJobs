package com.demo.controllers.user;

import com.demo.dtos.AccountDTO;
import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.dtos.EmployerDTO;
import com.demo.dtos.PostingDTO;
import com.demo.dtos.PostingspaymentDTO;
import com.demo.dtos.SeekerDTO;
import com.demo.services.AccountService;
import com.demo.services.ApplicationHistoryService;
import com.demo.services.EmployerService;
import com.demo.services.FollowService;
import com.demo.services.PostingPaymentService;
import com.demo.services.PostingService;
import com.demo.services.SeekerService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostingControllerTest {

    @InjectMocks
    private PostingController controller;

    @Mock
    private PostingService postingService;

    @Mock
    private EmployerService employerService;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private AccountService accountService;

    @Mock
    private SeekerService seekerService;

    @Mock
    private FollowService followService;

    @Mock
    private PostingPaymentService postingPaymentService;

    @Mock
    private Authentication authentication;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpSession httpSession;

    private PostingDTO postingDTO;
    private EmployerDTO employerDTO;
    private AccountDTO accountDTO;
    private SeekerDTO seekerDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        postingDTO = new PostingDTO();
        postingDTO.setId(10);
        postingDTO.setTitle("Java Developer");
        postingDTO.setEmployerName("Tech Corp");

        employerDTO = new EmployerDTO();
        employerDTO.setId(3);
        employerDTO.setName("Tech Corp");

        accountDTO = new AccountDTO();
        accountDTO.setId(5);
        accountDTO.setUsername("seekerUser");

        seekerDTO = new SeekerDTO();
        seekerDTO.setId(8);
        seekerDTO.setAccountID(5);
        seekerDTO.setAccountName("seekerUser");
    }

    @Test
    void testDetails_WithAuthentication_ReturnsLoggedInView() {
        ModelMap modelMap = new ModelMap();
        when(postingService.findById(10)).thenReturn(postingDTO);
        when(employerService.findbyname("Tech Corp")).thenReturn(employerDTO);
        when(authentication.getName()).thenReturn("seekerUser");
        when(accountService.findByUsername("seekerUser")).thenReturn(accountDTO);
        when(seekerService.findByAccountID(5)).thenReturn(seekerDTO);

        String result = controller.details(modelMap, 10, authentication, httpSession);

        assertEquals("user/posting/detailslogin", result);
        assertEquals(postingDTO, modelMap.get("posting"));
        assertEquals(employerDTO, modelMap.get("employer"));
        assertEquals(seekerDTO, modelMap.get("seeker"));
        ApplicationHistoryDTO applicationHistoryDTO = (ApplicationHistoryDTO) modelMap.get("applicationHistoryDTO");
        assertNotNull(applicationHistoryDTO);
        assertEquals(10, applicationHistoryDTO.getPostingID());
        assertEquals(8, applicationHistoryDTO.getSeekerID());
    }

    @Test
    void testDetails_WithoutAuthentication_ReturnsGuestView() {
        ModelMap modelMap = new ModelMap();
        when(postingService.findById(10)).thenReturn(postingDTO);
        when(employerService.findbyname("Tech Corp")).thenReturn(employerDTO);

        String result = controller.details(modelMap, 10, null, httpSession);

        assertEquals("user/posting/details", result);
        assertEquals(postingDTO, modelMap.get("posting"));
        assertEquals(employerDTO, modelMap.get("employer"));
        assertNull(modelMap.get("seeker"));
        assertNull(modelMap.get("applicationHistoryDTO"));
    }

    @Test
    void testSearch_ConvertsSentinelValuesAndSeparatesVipPosts() {
        ModelMap modelMap = new ModelMap();
        PostingDTO vipPosting = new PostingDTO();
        vipPosting.setId(1);
        vipPosting.setTitle("VIP Java");
        PostingDTO regularPosting = new PostingDTO();
        regularPosting.setId(2);
        regularPosting.setTitle("Regular QA");

        PostingspaymentDTO vipPayment = new PostingspaymentDTO();
        vipPayment.setPostingsid(1);

        when(postingService.search(null, null, null, null, null, null))
                .thenReturn(new ArrayList<>(List.of(vipPosting, regularPosting)))
                .thenReturn(new ArrayList<>(List.of(vipPosting, regularPosting)));
        when(postingPaymentService.orderbycost()).thenReturn(List.of(vipPayment));

        String result = controller.search(modelMap, -1, -1, "", -1, -1, -1);

        assertEquals("user/posting/index", result);
        verify(postingService, times(2)).search(null, null, null, null, null, null);
        @SuppressWarnings("unchecked")
        List<PostingDTO> vipList = (List<PostingDTO>) modelMap.get("postingDTOVIPs");
        @SuppressWarnings("unchecked")
        List<PostingDTO> normalList = (List<PostingDTO>) modelMap.get("postings");
        assertEquals(1, vipList.size());
        assertEquals(1, normalList.size());
        assertEquals(1, vipList.get(0).getId());
        assertEquals(2, normalList.get(0).getId());
    }

    @Test
    void testApplyCV_SaveSuccess_RedirectsWithSuccessMessage() {
        ModelMap modelMap = new ModelMap();
        when(postingService.findById(10)).thenReturn(postingDTO);
        when(authentication.getName()).thenReturn("seekerUser");
        when(accountService.findByUsername("seekerUser")).thenReturn(accountDTO);
        when(seekerService.findByAccountID(5)).thenReturn(seekerDTO);
        when(applicationHistoryService.save(any(ApplicationHistoryDTO.class))).thenReturn(true);

        String result = controller.applyCV(modelMap, 10, authentication, redirectAttributes);

        ArgumentCaptor<ApplicationHistoryDTO> captor = ArgumentCaptor.forClass(ApplicationHistoryDTO.class);
        verify(applicationHistoryService).save(captor.capture());
        ApplicationHistoryDTO savedHistory = captor.getValue();
        assertEquals(10, savedHistory.getPostingID());
        assertEquals(8, savedHistory.getSeekerID());
        assertEquals(0, savedHistory.getStatus());
        assertEquals(0, savedHistory.getResult());
        assertEquals("redirect:/home/posting/10", result);
        verify(redirectAttributes).addFlashAttribute("success", "Completed");
    }

    @Test
    void testApplyCV_SaveFailure_RedirectsWithErrorMessage() {
        ModelMap modelMap = new ModelMap();
        when(postingService.findById(10)).thenReturn(postingDTO);
        when(authentication.getName()).thenReturn("seekerUser");
        when(accountService.findByUsername("seekerUser")).thenReturn(accountDTO);
        when(seekerService.findByAccountID(5)).thenReturn(seekerDTO);
        when(applicationHistoryService.save(any(ApplicationHistoryDTO.class))).thenReturn(false);

        String result = controller.applyCV(modelMap, 10, authentication, redirectAttributes);

        assertEquals("redirect:/home/posting/10", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }

    @Test
    void testSaveJob_SaveSuccess_RedirectsWithSuccessMessage() {
        ModelMap modelMap = new ModelMap();
        when(postingService.findById(10)).thenReturn(postingDTO);
        when(authentication.getName()).thenReturn("seekerUser");
        when(accountService.findByUsername("seekerUser")).thenReturn(accountDTO);
        when(seekerService.findByAccountID(5)).thenReturn(seekerDTO);
        when(applicationHistoryService.save(any(ApplicationHistoryDTO.class))).thenReturn(true);

        String result = controller.saveJob(modelMap, 10, authentication, redirectAttributes);

        ArgumentCaptor<ApplicationHistoryDTO> captor = ArgumentCaptor.forClass(ApplicationHistoryDTO.class);
        verify(applicationHistoryService).save(captor.capture());
        ApplicationHistoryDTO savedHistory = captor.getValue();
        assertEquals(10, savedHistory.getPostingID());
        assertEquals(8, savedHistory.getSeekerID());
        assertEquals(2, savedHistory.getStatus());
        assertEquals(0, savedHistory.getResult());
        assertEquals("redirect:/home/posting/10", result);
        verify(redirectAttributes).addFlashAttribute("success2", "Completed");
    }

    @Test
    void testSaveJob_SaveFailure_RedirectsWithErrorMessage() {
        ModelMap modelMap = new ModelMap();
        when(postingService.findById(10)).thenReturn(postingDTO);
        when(authentication.getName()).thenReturn("seekerUser");
        when(accountService.findByUsername("seekerUser")).thenReturn(accountDTO);
        when(seekerService.findByAccountID(5)).thenReturn(seekerDTO);
        when(applicationHistoryService.save(any(ApplicationHistoryDTO.class))).thenReturn(false);

        String result = controller.saveJob(modelMap, 10, authentication, redirectAttributes);

        assertEquals("redirect:/home/posting/10", result);
        verify(redirectAttributes).addFlashAttribute("error2", "Thất bại...");
    }
}
