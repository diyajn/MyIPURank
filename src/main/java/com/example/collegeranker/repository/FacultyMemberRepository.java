package com.example.collegeranker.repository;

import com.example.collegeranker.entity.CollegeAcademicYear;
import com.example.collegeranker.entity.FacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyMemberRepository extends JpaRepository<FacultyMember, Long> {
    List<FacultyMember> findByCollegeYear(CollegeAcademicYear year);
}