package com.example.collegeranker.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "faculty_member")
@Data
public class FacultyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_year_id")
    private CollegeAcademicYear collegeYear;

    private String name;
    private Integer age;
    private String designation;
    private String gender;
    private String qualification;
    private Integer experienceMonths;
    private String working;
    private String associationType;
}
