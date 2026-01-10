package com.example.collegeranker.service;

import com.example.collegeranker.entity.*;
import com.example.collegeranker.repository.*;
import com.example.collegeranker.service.crawl.PdfLinkFetcher;
import com.example.collegeranker.service.extract.AcademicYearExtractor;
import com.example.collegeranker.service.extract.PdfTextExtractor;
import com.example.collegeranker.service.factory.ParserFactory;
import com.example.collegeranker.service.parse.PlacementParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlacementIngestionService {

    private final CollegeRepository collegeRepo;
    private final CollegeAcademicYearRepository yearRepo;
    private final PlacementSummaryRepository summaryRepo;
    private final PdfLinkFetcher linkFetcher = new PdfLinkFetcher();
    private final PdfTextExtractor textExtractor = new PdfTextExtractor();

    public PlacementIngestionService(
            CollegeRepository collegeRepo,
            CollegeAcademicYearRepository yearRepo,
            PlacementSummaryRepository summaryRepo) {

        this.collegeRepo = collegeRepo;
        this.yearRepo = yearRepo;
        this.summaryRepo = summaryRepo;
    }

    public void ingestById(Long collegeId) throws Exception {
        College college = collegeRepo.findById(collegeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "College not found with id " + collegeId)
                );
        ingestCollege(college);
    }

    private void ingestCollege(College college) throws Exception {

        Map<String, String> pdfs = linkFetcher.fetch(
                college.getName(),
                college.getPlacementPageUrl(),
                college.getCayPageUrl()
        );
        System.out.println("\n================== START FETCH ==================");
        System.out.println("College: " + college.getName());
        System.out.println("Total PDFs found: " + pdfs.size());

        int storedCount = 0;

        for (Map.Entry<String, String> entry : pdfs.entrySet()) {

            String pdfUrl = entry.getKey();
            String linkText = entry.getValue();
            String pdfText = textExtractor.extract(pdfUrl);
            String yearLabel =
                    AcademicYearExtractor.extract(linkText, pdfUrl, pdfText);


            if (yearLabel == null|| Integer.parseInt(yearLabel) < 2022) {
                System.out.println("❌ SKIPPING PDF (year not found)");
                continue;
            }

            CollegeAcademicYear cay = yearRepo
                    .findByCollegeAndYearLabel(college, yearLabel)
                    .orElseGet(() -> {
                        CollegeAcademicYear y = new CollegeAcademicYear();
                        y.setCollege(college);
                        y.setYearLabel(yearLabel);
                        return yearRepo.save(y);
                    });

            PlacementParser parser =
                    ParserFactory.getParser(college.getName());
            List<PlacementSummary> summaries = parser.parse(pdfText);

            // ================== TABLE PRINT ==================
            printTable(college.getName(), yearLabel, pdfUrl, summaries);
            // =================================================

            for (PlacementSummary ps : summaries) {
                ps.setCollegeAcademicYear(cay);
                summaryRepo.save(ps);
            }

            storedCount++;
            System.out.println("✅ PDF STORED IN DB → Year: " + yearLabel);
        }

        System.out.println("\n================== END FETCH ==================");
        System.out.println("Total PDFs stored in DB for college "
                + college.getName() + ": " + storedCount);
    }

    // ----------------------------------------------------
    // CONSOLE TABLE (DEBUG / VISIBILITY ONLY)
    // ----------------------------------------------------

    private void printTable(String college,
                            String year,
                            String pdfUrl,
                            List<PlacementSummary> list) {

        System.out.println("\n------------------------------------------------------------");
        System.out.println("College : " + college + " | Year : " + year);
        System.out.println("PDF     : " + pdfUrl);
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-25s %-15s %-10s%n",
                "Company", "Package(LPA)", "Students");
        System.out.println("------------------------------------------------------------");

        for (PlacementSummary ps : list) {
            System.out.printf("%-25s %-15s %-10d%n",
                    ps.getCompanyName(),
                    ps.getPackageLpa() == null ? "NA" : ps.getPackageLpa(),
                    ps.getStudentsPlaced());
        }

        System.out.println("------------------------------------------------------------");
    }
}









