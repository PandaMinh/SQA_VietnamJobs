package com.demo.services;

import com.demo.entities.Category;
import com.demo.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    // TC-SV-CAT-001 ~ TC-SV-CAT-008
    // File: CategoryServiceTest.java
    // Class: CategoryServiceImpl

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = new Category();
        category.setId(1);
        category.setName("IT - Phần mềm");
        category.setStatus(true);
        category.setParentId(0);

        childCategory = new Category();
        childCategory.setId(2);
        childCategory.setName("Lập trình Java");
        childCategory.setStatus(true);
        childCategory.setParentId(1);
    }

    @Test
    void testFindbyname_Success() {
        // TC-SV-CAT-001
        // Mục tiêu: Kiểm tra tìm kiếm danh mục theo tên thành công
        // Input: name="IT - Phần mềm", repository trả về category hợp lệ
        // Output mong đợi: Category không null với name="IT - Phần mềm", id=1
        when(categoryRepository.findbyname("IT - Phần mềm")).thenReturn(category);

        Category result = categoryService.findbyname("IT - Phần mềm");

        verify(categoryRepository).findbyname("IT - Phần mềm");
        assertNotNull(result);
        assertEquals("IT - Phần mềm", result.getName());
        assertEquals(1, result.getId());
        assertTrue(result.isStatus());
    }

    @Test
    void testFindbyname_NotFound() {
        // TC-SV-CAT-002
        // Mục tiêu: Kiểm tra trường hợp không tìm thấy danh mục theo tên
        // Input: name="Unknown Category", repository trả về null
        // Output mong đợi: null
        when(categoryRepository.findbyname("Unknown Category")).thenReturn(null);

        Category result = categoryService.findbyname("Unknown Category");

        verify(categoryRepository).findbyname("Unknown Category");
        assertNull(result);
    }

    @Test
    void testSave_Success() {
        // TC-SV-CAT-003
        // Mục tiêu: Kiểm tra lưu danh mục thành công
        // Input: category hợp lệ, repository.save không ném ngoại lệ
        // Output mong đợi: true, repository.save được gọi đúng 1 lần
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        boolean result = categoryService.save(category);

        verify(categoryRepository).save(category);
        assertTrue(result);
    }

    @Test
    void testSave_Failure_RepositoryException() {
        // TC-SV-CAT-004
        // Mục tiêu: Kiểm tra xử lý khi repository ném ngoại lệ trong quá trình save
        // Input: category hợp lệ, repository.save ném RuntimeException
        // Output mong đợi: false (exception được bắt trong try-catch)
        when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException("Database error"));

        boolean result = categoryService.save(category);

        verify(categoryRepository).save(category);
        assertFalse(result);
    }

    @Test
    void testDelete_Success() {
        // TC-SV-CAT-005
        // Mục tiêu: Kiểm tra xóa danh mục thành công
        // Input: id=1, category tồn tại trong repository
        // Output mong đợi: true, repository.delete được gọi với đúng category
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(any(Category.class));

        boolean result = categoryService.delete(1);

        verify(categoryRepository).findById(1);
        verify(categoryRepository).delete(category);
        assertTrue(result);
    }

    @Test
    void testDelete_Failure_CategoryNotFound() {
        // TC-SV-CAT-006
        // Mục tiêu: Kiểm tra xử lý khi category không tồn tại (Optional rỗng → NoSuchElementException)
        // Input: id=999, repository trả về Optional.empty()
        // Output mong đợi: false (NoSuchElementException được bắt trong try-catch)
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = categoryService.delete(999);

        verify(categoryRepository).findById(999);
        assertFalse(result);
    }

    @Test
    void testExists_True() {
        // TC-SV-CAT-007
        // Mục tiêu: Kiểm tra tên danh mục đã tồn tại (trùng với category khác)
        // Input: name="IT - Phần mềm", id=2 (kiểm tra trùng tên với category khác có id != 2)
        // Output mong đợi: true (count > 0)
        when(categoryRepository.exists("IT - Phần mềm", 2)).thenReturn(1);

        boolean result = categoryService.exists("IT - Phần mềm", 2);

        verify(categoryRepository).exists("IT - Phần mềm", 2);
        assertTrue(result);
    }

    @Test
    void testExists_False() {
        // TC-SV-CAT-008
        // Mục tiêu: Kiểm tra tên danh mục chưa tồn tại (tên mới, không trùng)
        // Input: name="New Category", id=0 (thêm mới, không trùng với bất kỳ category nào)
        // Output mong đợi: false (count = 0)
        when(categoryRepository.exists("New Category", 0)).thenReturn(0);

        boolean result = categoryService.exists("New Category", 0);

        verify(categoryRepository).exists("New Category", 0);
        assertFalse(result);
    }
}
