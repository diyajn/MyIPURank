package com.example.collegeranker.repository;

import com.example.collegeranker.entity.College;
import com.example.collegeranker.entity.CollegeAcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CollegeAcademicYearRepository
        extends JpaRepository<CollegeAcademicYear, Long> {

  Optional<CollegeAcademicYear> findByCollegeAndYearLabel(
          College college,
          String yearLabel
  );

}

