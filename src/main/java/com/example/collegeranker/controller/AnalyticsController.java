package com.example.collegeranker.controller;

import com.example.collegeranker.dto.CollegeAnalyticsDTO;
import com.example.collegeranker.entity.*;
import com.example.collegeranker.repository.*;
import com.example.collegeranker.service.analytics.CollegeAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final CollegeRepository collegeRepo;
    private final CollegeAcademicYearRepository cayRepo;
    private final PlacementSummaryRepository placementRepo;
    private final CollegeStudentStatsRepository studentStatsRepo;
    private final FacultyMemberRepository facultyRepo;
    private final CollegeAnalyticsService analyticsService;

    @GetMapping("/colleges/{yearLabel}")
    public List<CollegeAnalyticsDTO> getCollegeAnalytics(
            @PathVariable String yearLabel) {

        yearLabel = yearLabel.trim(); // 🔴 IMPORTANT

        List<College> colleges = collegeRepo.findAll();
        List<CollegeAnalyticsDTO> result = new ArrayList<>();

        for (College college : colleges) {

            Optional<CollegeAcademicYear> cayOpt =
                    cayRepo.findByCollegeAndYearLabel(college, yearLabel);

            if (cayOpt.isEmpty()) continue;

            CollegeAcademicYear cay = cayOpt.get();

            List<PlacementSummary> placements =
                    placementRepo.findByCollegeAcademicYear_Id(cay.getId());

            CollegeStudentStats stats =
                    studentStatsRepo.findByCollegeYear(cay)
                            .orElse(null);

            List<FacultyMember> faculty =
                    facultyRepo.findByCollegeYear(cay);

            CollegeAnalyticsDTO dto =
                    analyticsService.buildDTO(
                            cay,
                            placements,
                            stats,
                            faculty
                    );

            result.add(dto);
        }

        applyRanking(result);
        return result;
    }

    private void applyRanking(List<CollegeAnalyticsDTO> list) {

        list.sort(Comparator.comparingDouble(
                CollegeAnalyticsDTO::getScore).reversed());

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }
    }
}



