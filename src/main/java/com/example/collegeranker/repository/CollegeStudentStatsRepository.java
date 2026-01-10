package com.example.collegeranker.repository;

import com.example.collegeranker.entity.CollegeAcademicYear;
import com.example.collegeranker.entity.CollegeStudentStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollegeStudentStatsRepository extends JpaRepository<CollegeStudentStats, Long> {
    Optional<CollegeStudentStats> findByCollegeYear(CollegeAcademicYear year);
}