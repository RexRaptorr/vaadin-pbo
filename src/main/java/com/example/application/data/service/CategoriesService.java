package com.example.application.data.service;

import com.example.application.data.entity.Categories;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CategoriesService {

    private final CategoriesRepository repository;

    public CategoriesService(CategoriesRepository repository) {
        this.repository = repository;
    }

    public Optional<Categories> get(Long id) {
        return repository.findById(id);
    }

    public Categories update(Categories entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Categories> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Categories> list(Pageable pageable, Specification<Categories> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
