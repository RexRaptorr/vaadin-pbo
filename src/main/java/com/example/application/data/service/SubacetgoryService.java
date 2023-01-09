package com.example.application.data.service;

import com.example.application.data.entity.Subacetgory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SubacetgoryService {

    private final SubacetgoryRepository repository;

    public SubacetgoryService(SubacetgoryRepository repository) {
        this.repository = repository;
    }

    public Optional<Subacetgory> get(Long id) {
        return repository.findById(id);
    }

    public Subacetgory update(Subacetgory entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Subacetgory> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Subacetgory> list(Pageable pageable, Specification<Subacetgory> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
