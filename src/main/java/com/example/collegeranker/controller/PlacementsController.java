package com.example.collegeranker.controller;

import com.example.collegeranker.entity.PlacementSummary;
import com.example.collegeranker.repository.PlacementSummaryRepository;
import com.example.collegeranker.service.PlacementIngestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/placements")
public class PlacementsController {

    private final PlacementIngestionService ingestionService;
    private final PlacementSummaryRepository summaryRepo;

    public PlacementsController(
            PlacementIngestionService ingestionService,
            PlacementSummaryRepository summaryRepo) {

        this.ingestionService = ingestionService;
        this.summaryRepo = summaryRepo;
    }

    //  All data of a college
    @GetMapping("/college/{collegeName}")
    public List<PlacementSummary> byCollege(
            @PathVariable String collegeName) {

        return summaryRepo
                .findByCollegeAcademicYear_College_Name(collegeName);
    }

    // College + Year
    @GetMapping("/college/{collegeName}/year/{year}")
    public List<PlacementSummary> byCollegeAndYear(
            @PathVariable String collegeName,
            @PathVariable String year) {

        //  Fetch from DB
        List<PlacementSummary> list =
                summaryRepo.findByCollegeAcademicYear_College_NameAndCollegeAcademicYear_YearLabel(
                        collegeName, year);

        // Print to console (HUMAN READABLE)
        System.out.println("\n========= DATA FETCHED FROM DB =========");
        System.out.printf("%-30s %-12s %-10s%n",
                "COMPANY", "PACKAGE(LPA)", "STUDENTS");

        for (PlacementSummary ps : list) {
            System.out.printf("%-30s %-12.2f %-10d%n",
                    ps.getCompanyName(),
                    ps.getPackageLpa(),
                    ps.getStudentsPlaced());
        }

        System.out.println("=======================================\n");

        // Return as JSON (unchanged)
        return list;
    }


    //  Summary (same data, frontend can aggregate)
    @GetMapping("/college/{collegeName}/summary")
    public List<PlacementSummary> summary(
            @PathVariable String collegeName) {

        return summaryRepo
                .findByCollegeAcademicYear_College_Name(collegeName);
    }
}





