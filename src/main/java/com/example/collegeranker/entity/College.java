package com.example.collegeranker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String placementPageUrl;

    // ONLY for MAIT
    private String cayPageUrl;
}










