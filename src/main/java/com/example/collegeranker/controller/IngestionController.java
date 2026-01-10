package com.example.collegeranker.controller;

import com.example.collegeranker.service.NirfIngestionService;
import com.example.collegeranker.service.PlacementIngestionService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final PlacementIngestionService placementService;
    private final NirfIngestionService nirfService;

    public IngestionController(PlacementIngestionService placementService,
                               NirfIngestionService nirfService) {
        this.placementService = placementService;
        this.nirfService = nirfService;
    }

    // Trigger ingestion for both placement + NIRF
    @PostMapping("/{collegeId}")
    public String ingest(@PathVariable Long collegeId) throws Exception {
        System.out.println("\n================== INGESTION START ==================");

        // Placement ingestion
        placementService.ingestById(collegeId);

        // NIRF ingestion
        nirfService.ingestNirfLinks(collegeId);

        System.out.println("\n================== INGESTION END ==================");
        return "Placement + NIRF ingestion completed for collegeId = " + collegeId;
    }
}
