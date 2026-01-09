package com.example.collegeranker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "college")
@Data
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "placement_page_url")
    private String placementPageUrl;

    @Column(name = "cay_page_url")
    private String cayPageUrl;
}











