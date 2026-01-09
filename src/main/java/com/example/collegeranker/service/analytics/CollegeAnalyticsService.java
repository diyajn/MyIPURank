package com.example.collegeranker.service.analytics;

import com.example.collegeranker.dto.CollegeAnalyticsDTO;
import com.example.collegeranker.dto.MetricsDTO;
import com.example.collegeranker.entity.CollegeAcademicYear;
import com.example.collegeranker.entity.PlacementSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollegeAnalyticsService {

    // 1️⃣ PUBLIC entry method
    public CollegeAnalyticsDTO buildDTO(
            CollegeAcademicYear cay,
            List<PlacementSummary> placements) {

        MetricsDTO metrics = calculateMetrics(placements);
        double score = calculateFinalScore(metrics);

        CollegeAnalyticsDTO dto = new CollegeAnalyticsDTO();
        dto.setCollegeId(cay.getCollege().getId());
        dto.setCollegeName(cay.getCollege().getName());
        dto.setYearLabel(cay.getYearLabel());
        dto.setMetrics(metrics);
        dto.setScore(score);

        return dto;
    }

    // 2️⃣ METRICS CALCULATION
    private MetricsDTO calculateMetrics(List<PlacementSummary> placements) {
        MetricsDTO metrics = new MetricsDTO();

        double avgPackage = calculateAveragePackage(placements);

        metrics.setPlacements(normalize(avgPackage));
        metrics.setRoi(calculateROI(avgPackage));

        // currently static / later dynamic
        metrics.setAcademics(85);
        metrics.setInfrastructure(80);

        return metrics;
    }

    // 3️⃣ SCORE CALCULATION
    private double calculateFinalScore(MetricsDTO m) {
        return m.getAcademics() * 0.3
                + m.getPlacements() * 0.4
                + m.getInfrastructure() * 0.2
                + m.getRoi() * 0.1;
    }

    // 4️⃣ HELPER METHODS
    private double calculateAveragePackage(List<PlacementSummary> placements) {
        return placements.stream()
                .mapToDouble(PlacementSummary::getPackageLpa)
                .average()
                .orElse(0);
    }

    private double normalize(double avgPackage) {
        return Math.min((avgPackage / 30.0) * 100, 100);
    }

    private double calculateROI(double avgPackage) {
        double avgFees = 8.0; // example (LPA)
        return Math.min((avgPackage / avgFees) * 100, 100);
    }
}


