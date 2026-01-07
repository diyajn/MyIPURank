package com.example.collegeranker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CollegeAcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "college_id")
    private College college;

    private String yearLabel; // e.g. "2019-2023"
}







