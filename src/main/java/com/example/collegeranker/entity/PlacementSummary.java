package com.example.collegeranker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_college_year", columnList = "college_year_id"),
                @Index(name = "idx_company", columnList = "companyName")
        }
)
@Data
public class PlacementSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "college_year_id")
    private CollegeAcademicYear collegeAcademicYear;

    private String companyName;

    private Double packageLpa;

    private int studentsPlaced;
}






