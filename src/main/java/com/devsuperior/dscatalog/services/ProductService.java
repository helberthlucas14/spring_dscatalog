package com.devsuperior.dscatalog.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDto;
import com.devsuperior.dscatalog.dtos.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> findAll(Long categoryId, String name, Pageable pageable) {
        List<Category> categories = (categoryId == 0)
                ? Collections.emptyList()
                : Arrays.asList(categoryRepository.getReferenceById(categoryId));
        Page<Product> list = repository.find(categories, name, pageable);
        repository.findProductsWithCategories(list.getContent());
        return list.map(x -> new ProductDto(x, x.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {

        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));

        return new ProductDto(entity, entity.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = repository.save(entity);
        return new ProductDto(entity, entity.getCategories());
    }

    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);

            entity = repository.save(entity);
            return new ProductDto(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found");
        }
        repository.deleteById(id);
    }

    private void copyDtoToEntity(ProductDto dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();
        for (CategoryDto catDTO : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDTO.getId());
            entity.getCategories().add(category);
        }

    }
}
