package com.example.collegeranker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "placement_summary",
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_year_id")
    private CollegeAcademicYear collegeAcademicYear;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "package_lpa")
    private Double packageLpa;

    @Column(name = "students_placed")
    private int studentsPlaced;
}







