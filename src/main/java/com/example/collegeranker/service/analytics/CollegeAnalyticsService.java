package com.example.collegeranker.service.analytics;

import com.example.collegeranker.dto.CollegeAnalyticsDTO;
import com.example.collegeranker.dto.MetricsDTO;
import com.example.collegeranker.entity.CollegeAcademicYear;
import com.example.collegeranker.entity.CollegeStudentStats;
import com.example.collegeranker.entity.FacultyMember;
import com.example.collegeranker.entity.PlacementSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CollegeAnalyticsService {

    // 🔹 ENTRY POINT
    public CollegeAnalyticsDTO buildDTO(
            CollegeAcademicYear cay,
            List<PlacementSummary> placements,
            CollegeStudentStats stats,
            List<FacultyMember> faculty) {

        MetricsDTO metrics =
                calculateMetrics(placements, stats, faculty);

        double score = calculateFinalScore(metrics);

        CollegeAnalyticsDTO dto = new CollegeAnalyticsDTO();
        dto.setCollegeId(cay.getCollege().getId());
        dto.setCollegeName(cay.getCollege().getName());
        dto.setYearLabel(cay.getYearLabel());
        dto.setMetrics(metrics);
        dto.setScore(score);

        return dto;
    }

    // 🔹 METRICS CALCULATION
    private MetricsDTO calculateMetrics(
            List<PlacementSummary> placements,
            CollegeStudentStats stats,
            List<FacultyMember> faculty) {

        MetricsDTO m = new MetricsDTO();

        double avgPackage = calculateAveragePackage(placements);

        m.setPlacements(calculatePlacementScore(placements, stats));
        m.setRoi(calculateROI(avgPackage));
        m.setAcademics(calculateAcademicsScore(faculty));
        m.setInfrastructure(calculateInfrastructureScore(stats));

        return m;
    }

    // 🔹 FINAL SCORE
    private double calculateFinalScore(MetricsDTO m) {
        return m.getAcademics() * 0.30
                + m.getPlacements() * 0.40
                + m.getInfrastructure() * 0.20
                + m.getRoi() * 0.10;
    }

    /* ================= PLACEMENTS ================= */

    private double calculateAveragePackage(
            List<PlacementSummary> placements) {

        int totalPlaced =
                placements.stream()
                        .mapToInt(PlacementSummary::getStudentsPlaced)
                        .sum();

        if (totalPlaced == 0) return 0;

        double totalCTC =
                placements.stream()
                        .mapToDouble(p ->
                                p.getPackageLpa() * p.getStudentsPlaced())
                        .sum();

        return totalCTC / totalPlaced;
    }

    private double calculatePlacementScore(
            List<PlacementSummary> placements,
            CollegeStudentStats stats) {

        if (stats == null || stats.getTotalStudents() == 0)
            return 0;

        int placed =
                placements.stream()
                        .mapToInt(PlacementSummary::getStudentsPlaced)
                        .sum();

        double placementRatio =
                (double) placed / stats.getTotalStudents();

        double avgPackageScore =
                normalize(calculateAveragePackage(placements), 30);

        return avgPackageScore * 0.7
                + (placementRatio * 100) * 0.3;
    }

    /* ================= ACADEMICS ================= */

    private double calculateAcademicsScore(
            List<FacultyMember> faculty) {

        if (faculty == null || faculty.isEmpty())
            return 0;

        long total = faculty.size();

        long phd =
                faculty.stream()
                        .filter(f ->
                                "Ph.D".equalsIgnoreCase(
                                        f.getQualification()))
                        .count();

        long permanent =
                faculty.stream()
                        .filter(f ->
                                "Regular".equalsIgnoreCase(
                                        f.getAssociationType()))
                        .count();

        double avgExpMonths =
                faculty.stream()
                        .mapToInt(FacultyMember::getExperienceMonths)
                        .average()
                        .orElse(0);

        double phdScore = (phd * 100.0 / total) * 0.5;
        double expScore =
                normalize(avgExpMonths / 12, 25) * 0.3;
        double permScore =
                (permanent * 100.0 / total) * 0.2;

        return phdScore + expScore + permScore;
    }

    /* ================= INFRASTRUCTURE ================= */

    private double calculateInfrastructureScore(
            CollegeStudentStats s) {

        if (s == null || s.getTotalStudents() == 0)
            return 0;

        double exposure =
                (s.getOutsideState()
                        + s.getOutsideCountry())
                        * 100.0 / s.getTotalStudents();

        double reimbursement =
                (s.getReimbGovt()
                        + s.getReimbInstitution()
                        + s.getReimbPrivate())
                        * 100.0 / s.getTotalStudents();

        double capacity =
                Math.min(
                        s.getTotalStudents() / 3000.0 * 100,
                        100
                );

        return exposure * 0.4
                + reimbursement * 0.3
                + capacity * 0.3;
    }

    /* ================= ROI ================= */

    private double calculateROI(double avgPackage) {
        double avgFees = 8.0; // configurable later
        return normalize(avgPackage / avgFees * 100, 100);
    }

    /* ================= UTIL ================= */

    private double normalize(double value, double max) {
        return Math.min((value / max) * 100, 100);
    }
}
