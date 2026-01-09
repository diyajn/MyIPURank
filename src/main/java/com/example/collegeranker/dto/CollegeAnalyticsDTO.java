package com.example.collegeranker.dto;

import lombok.Data;

@Data
public class CollegeAnalyticsDTO {
    private Long collegeId;
    private String collegeName;
    private String yearLabel;
    private double score;
    private MetricsDTO metrics;
    private Integer rank; // optional
}

