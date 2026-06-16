package com.demo.controllers.employer;

import com.demo.dtos.ApplicationHistoryDTO;
import com.demo.services.ApplicationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployerApplyControllerTest {

    @InjectMocks
    private EmployerApplyController controller;

    @Mock
    private ApplicationHistoryService applicationHistoryService;

    @Mock
    private RedirectAttributes redirectAttributes;

    private ApplicationHistoryDTO applicationHistoryDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        applicationHistoryDTO = new ApplicationHistoryDTO();
        applicationHistoryDTO.setId(5);
        applicationHistoryDTO.setPostingID(10);
        applicationHistoryDTO.setResult(0);
    }

    @Test
    void testJob_ReturnsViewWithPostingId() {
        ModelMap modelMap = new ModelMap();

        String result = controller.job(10, modelMap);

        assertEquals("employer/apply/job", result);
        assertEquals(10, modelMap.get("id"));
    }

    @Test
    void testReject_SaveSuccess_RedirectsWithSuccessMessage() {
        ModelMap modelMap = new ModelMap();
        when(applicationHistoryService.findByID(5)).thenReturn(applicationHistoryDTO);
        when(applicationHistoryService.save(applicationHistoryDTO)).thenReturn(true);

        String result = controller.reject(5, modelMap, redirectAttributes);

        verify(applicationHistoryService).findByID(5);
        verify(applicationHistoryService).save(applicationHistoryDTO);
        assertEquals(2, applicationHistoryDTO.getResult());
        assertEquals(5, modelMap.get("id"));
        assertEquals("redirect:/employer/apply/index/10", result);
        verify(redirectAttributes).addFlashAttribute("success", "Thành công!");
    }

    @Test
    void testReject_SaveFailure_RedirectsWithErrorMessage() {
        ModelMap modelMap = new ModelMap();
        when(applicationHistoryService.findByID(5)).thenReturn(applicationHistoryDTO);
        when(applicationHistoryService.save(applicationHistoryDTO)).thenReturn(false);

        String result = controller.reject(5, modelMap, redirectAttributes);

        verify(applicationHistoryService).findByID(5);
        verify(applicationHistoryService).save(applicationHistoryDTO);
        assertEquals(2, applicationHistoryDTO.getResult());
        assertEquals(5, modelMap.get("id"));
        assertEquals("redirect:/employer/apply/index/10", result);
        verify(redirectAttributes).addFlashAttribute("error", "Thất bại...");
    }
}
