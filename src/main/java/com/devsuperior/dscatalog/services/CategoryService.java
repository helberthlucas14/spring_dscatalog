package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dtos.CategoryDto;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        List<Category> list = repository.findAll();
        return list.stream().map(CategoryDto::new).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Optional<Category> category = repository.findById(id);
        return new CategoryDto(category.orElseThrow(() -> new ResourceNotFoundException("Entity not found")));
    }

    @Transactional
    public CategoryDto insert(CategoryDto dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDto(entity);
    }

    @Transactional
    public CategoryDto update(CategoryDto dto) {
        Category entity = repository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDto(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found");
        }
        repository.deleteById(id);
    }

    public Page<CategoryDto> findAllPage(Pageable page) {
        Page<Category> list = repository.findAll(page);
        return list.map(CategoryDto::new);
    }
}
