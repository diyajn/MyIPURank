package com.example.collegeranker.repository;

import com.example.collegeranker.entity.PlacementSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementSummaryRepository
        extends JpaRepository<PlacementSummary, Long> {

    List<PlacementSummary>
    findByCollegeAcademicYear_College_Name(String collegeName);

    List<PlacementSummary>
    findByCollegeAcademicYear_College_NameAndCollegeAcademicYear_YearLabel(
            String collegeName,
            String yearLabel);

    List<PlacementSummary> findByCollegeAcademicYear_Id(Long collegeYearId);
}





