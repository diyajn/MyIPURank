package com.example.collegeranker.repository;

import com.example.collegeranker.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {

  Optional<College> findByNameIgnoreCase(String name);
}

