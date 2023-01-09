package com.example.application.data.service;

import com.example.application.data.entity.Subacetgory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubacetgoryRepository extends JpaRepository<Subacetgory, Long>, JpaSpecificationExecutor<Subacetgory> {

}
