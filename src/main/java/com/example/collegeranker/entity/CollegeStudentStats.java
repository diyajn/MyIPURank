package com.example.collegeranker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "college_student_stats")
@Data
public class CollegeStudentStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_year_id")
    private CollegeAcademicYear collegeYear;

    private Integer maleStudents;
    private Integer femaleStudents;
    private Integer totalStudents;
    private Integer withinState;
    private Integer outsideState;
    private Integer outsideCountry;
    private Integer economicallyBackward;
    private Integer sociallyChallenged;
    private Integer reimbGovt;
    private Integer reimbInstitution;
    private Integer reimbPrivate;
    private Integer noReimbursed;
}
