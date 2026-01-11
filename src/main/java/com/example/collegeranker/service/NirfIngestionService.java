package com.example.collegeranker.service;

import com.example.collegeranker.entity.College;
import com.example.collegeranker.entity.CollegeAcademicYear;
import com.example.collegeranker.entity.CollegeStudentStats;
import com.example.collegeranker.entity.FacultyMember;
import com.example.collegeranker.repository.CollegeAcademicYearRepository;
import com.example.collegeranker.repository.CollegeRepository;
import com.example.collegeranker.repository.CollegeStudentStatsRepository;
import com.example.collegeranker.repository.FacultyMemberRepository;
import com.example.collegeranker.service.parse.nirf.FacultyParser;
import com.example.collegeranker.service.parse.nirf.StudentStrengthParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class NirfIngestionService {

    private final CollegeRepository collegeRepository;
    private final CollegeAcademicYearRepository academicYearRepository;
    private final CollegeStudentStatsRepository statsRepo;
    private final FacultyMemberRepository facultyRepo;
    public NirfIngestionService(CollegeRepository collegeRepo,
                                CollegeAcademicYearRepository yearRepo,
                                CollegeStudentStatsRepository statsRepo,
                                FacultyMemberRepository facultyRepo) {
        this.collegeRepository = collegeRepo;
        this.academicYearRepository = yearRepo;
        this.statsRepo = statsRepo;
        this.facultyRepo = facultyRepo;
    }

    public void ingestNirfLinks(Long collegeId) throws Exception {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new RuntimeException("College not found"));

        Map<String, String> hardcodedLinks = getHardcodedLinks(college.getName());

        System.out.println("\n================== START NIRF INGESTION ==================");
        System.out.println("College: " + college.getName());
        System.out.println("Total NIRF PDFs found: " + hardcodedLinks.size());

        int storedCount = 0;

        for (Map.Entry<String, String> entry : hardcodedLinks.entrySet()) {
            String yearLabel = entry.getKey();
            String pdfUrl = entry.getValue();

            System.out.println("\n🔹 FETCHED NIRF PDF URL : " + pdfUrl);
            System.out.println("🎯 YEAR LABEL           : " + yearLabel);

            try {
                // Extract text
                String pdfText = extractTextFromPdf(pdfUrl);

                // Find or create academic year
                CollegeAcademicYear cay = academicYearRepository
                        .findByCollegeAndYearLabel(college, yearLabel)
                        .orElseGet(() -> {
                            CollegeAcademicYear y = new CollegeAcademicYear();
                            y.setCollege(college);
                            y.setYearLabel(yearLabel);
                            return academicYearRepository.save(y);
                        });

                cay.setNirfReportUrl(pdfUrl);

                // Student Strength
                StudentStrengthParser.StudentStrength s = StudentStrengthParser.parse(pdfText);
                CollegeStudentStats stats = new CollegeStudentStats();
                stats.setCollegeYear(cay);
                stats.setMaleStudents(s.male);
                stats.setFemaleStudents(s.female);
                stats.setTotalStudents(s.total);
                stats.setWithinState(s.withinState);
                stats.setOutsideState(s.outsideState);
                stats.setOutsideCountry(s.outsideCountry);
                stats.setEconomicallyBackward(s.economicallyBackward);
                stats.setSociallyChallenged(s.sociallyChallenged);
                stats.setReimbGovt(s.reimbGovt);
                stats.setReimbInstitution(s.reimbInstitution);
                stats.setReimbPrivate(s.reimbPrivate);
                stats.setNoReimbursed(s.noReimb);
                statsRepo.save(stats);

                // 3️⃣ Faculty
                List<FacultyParser.Faculty> facultyList = FacultyParser.parse(pdfText);
                for (FacultyParser.Faculty f : facultyList) {
                    FacultyMember member = new FacultyMember();
                    member.setCollegeYear(cay);
                    member.setName(f.name);
                    member.setAge(f.age);
                    member.setDesignation(f.designation);
                    member.setGender(f.gender);
                    member.setQualification(f.qualification);
                    member.setExperienceMonths(f.experienceMonths);
                    member.setWorking(f.working);
                    member.setAssociationType(f.associationType);
                    facultyRepo.save(member);
                }

                // ✅ Fetch back from DB and print only stored values
                CollegeAcademicYear storedYear = academicYearRepository.findByCollegeAndYearLabel(college, yearLabel).get();
                CollegeStudentStats storedStats = statsRepo.findByCollegeYear(storedYear).orElse(null);
                List<FacultyMember> storedFaculty = facultyRepo.findByCollegeYear(storedYear);


                if (storedStats != null) {
                    System.out.println("\n👨‍🎓 Student Strength (DB)");
                    System.out.println("Male Students: " + storedStats.getMaleStudents());
                    System.out.println("Female Students: " + storedStats.getFemaleStudents());
                    System.out.println("Total Students: " + storedStats.getTotalStudents());
                    System.out.println("Within State: " + storedStats.getWithinState());
                    System.out.println("Outside State: " + storedStats.getOutsideState());
                    System.out.println("Outside Country: " + storedStats.getOutsideCountry());
                    System.out.println("Economically Backward: " + storedStats.getEconomicallyBackward());
                    System.out.println("Socially Challenged: " + storedStats.getSociallyChallenged());
                    System.out.println("Reimb Govt: " + storedStats.getReimbGovt());
                    System.out.println("Reimb Institution: " + storedStats.getReimbInstitution());
                    System.out.println("Reimb Private: " + storedStats.getReimbPrivate());
                    System.out.println("No Reimbursed: " + storedStats.getNoReimbursed());
                }

                System.out.println("\n👩‍🏫 Faculty Details (DB)");
                for (FacultyMember fm : storedFaculty) {
                    System.out.printf("%-25s | %-25s | %-10s%n",
                            fm.getName(),
                            fm.getDesignation(),
                            fm.getQualification());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            storedCount++;
            System.out.println("✅ NIRF DATA STORED IN DB → Year: " + yearLabel);
        }

        System.out.println("\n================== END NIRF INGESTION ==================");
        System.out.println("Total NIRF PDFs stored in DB for college "
                + college.getName() + ": " + storedCount);
    }

    private Map<String, String> getHardcodedLinks(String collegeName) {
        Map<String, String> map = new LinkedHashMap<>();

        if (collegeName.equalsIgnoreCase("BPIT")) {
            map.put("2023", "https://bpitind.bpitindia.ac.in/wp-content/uploads/2023/02/B.Tech-NIRF-Data-for-2023-01.02.2023.pdf");
            map.put("2024", "https://bpitindia.ac.in/wp-content/uploads/2024/09/Bhagwan-Parshuram-Institute-of-Technology20240129_compressed.pdf");
            map.put("2025", "https://bpitindia.ac.in/wp-content/uploads/2025/02/Bhagwan-Parshuram-Institute-of-Technology20250117-_compressed.pdf");
        } else if (collegeName.equalsIgnoreCase("MSIT")) {
            map.put("2022", "https://www.msit.in/media/2022/08/12/nirf2022-data.pdf");
            map.put("2023", "https://www.msit.in/media/2023/01/24/maharaja-surajmal-institute-of-technology20230119-.pdf");
            map.put("2024", "https://www.msit.in/media/2024/02/29/maharaja-surajmal-institute-of-technology-engineering.pdf");
            map.put("2025", "https://www.msit.in/media/uploads/2025/06/18/nirf-2025-engineering.pdf");
        } else if (collegeName.equalsIgnoreCase("MAIT")) {
            map.put("2022", "https://mait.ac.in/images/nirf/nirf22/MAHARAJA_AGRASEN_INSTITUTE_OF_TECHNOLOGY20220207-ENGG.pdf");
            map.put("2023", "https://mait.ac.in/images/nirf/nirf23/MAHARAJA_AGRASEN_INSTITUTE_OF_TECHNOLOGY20230105-ENGINEERING.pdf");
            map.put("2024", "https://mait.ac.in/images/nirf/nirf24/MAHARAJA_AGRASEN_INSTITUTE_OF_TECHNOLOGY-ENGINEERING.pdf");
            map.put("2025", "https://mait.ac.in/images/nirf/nirf25/ENGG-MAHARAJA_AGRASEN_INSTITUTE_OF_TECHNOLOGY20250108-.pdf");
        }

        return map;
    }


    // ---------------- PDF extraction ----------------
    private static String extractTextFromPdf(String url) throws Exception {
        try (InputStream input = new URL(url).openStream();
             PDDocument document = PDDocument.load(input)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }

}