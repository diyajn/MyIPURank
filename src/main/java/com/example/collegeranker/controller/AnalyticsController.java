package com.example.collegeranker.controller;

import com.example.collegeranker.dto.CollegeAnalyticsDTO;
import com.example.collegeranker.entity.College;
import com.example.collegeranker.entity.CollegeAcademicYear;
import com.example.collegeranker.entity.PlacementSummary;
import com.example.collegeranker.repository.CollegeAcademicYearRepository;
import com.example.collegeranker.repository.CollegeRepository;
import com.example.collegeranker.repository.PlacementSummaryRepository;
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
    private final CollegeAnalyticsService analyticsService;

    @GetMapping("/colleges/{yearLabel}")
    public List<CollegeAnalyticsDTO> getCollegeAnalytics(
            @PathVariable String yearLabel) {
        yearLabel = yearLabel.trim(); // ✅ CRITICAL LINE
        List<College> colleges = collegeRepo.findAll();
        System.out.println(colleges.size());
        List<CollegeAnalyticsDTO> result = new ArrayList<>();

        for (College college : colleges) {
            Optional<CollegeAcademicYear> cayOpt =
                    cayRepo.findByCollegeAndYearLabel(college, yearLabel);
            System.out.println("cayOpt"+cayOpt);
            if (cayOpt.isEmpty()) continue;

            CollegeAcademicYear cay = cayOpt.get();

            List<PlacementSummary> placements =
                    placementRepo.findByCollegeAcademicYear_Id(cay.getId());

            CollegeAnalyticsDTO dto =
                    analyticsService.buildDTO(cay, placements);

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


