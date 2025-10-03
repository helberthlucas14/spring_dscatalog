package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dtos.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private ProductDto productDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        Long dependentId = 4L;
        Product product = Factory.createProduct();
        Category category = Factory.createCategory();
        productDTO = Factory.createProductDTO(product);
        PageImpl<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Mockito.when(repository.find(any(), any(), any())).thenReturn(page);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        doNothing().when(repository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {

        ProductDto result = service.findById(existingId);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldThrowResourcedNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

        ProductDto result = service.update(existingId, productDTO);
        Assertions.assertNotNull(result);
    }

    @Test
    public void updateShouldThrowResourcedNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, productDTO));
    }

    @Test
    public void findAllPagedShouldReturnPage() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDto> result = service.findAll(0L, "", pageable);

        Assertions.assertNotNull(result);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
        verify(repository, times(0)).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        verify(repository, times(1)).deleteById(existingId);
    }
}
