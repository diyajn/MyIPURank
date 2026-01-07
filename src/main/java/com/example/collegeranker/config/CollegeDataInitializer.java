package com.example.collegeranker.config;

import com.example.collegeranker.entity.College;
import com.example.collegeranker.repository.CollegeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollegeDataInitializer {

    @Bean
    CommandLineRunner initColleges(CollegeRepository repo) {
        return args -> {

            insert(repo, "BPIT",
                    "https://bpitindia.ac.in/placement-record/",
                    null);

            insert(repo, "MSIT",
                    "https://www.msit.in/placements",
                    null);

            insert(repo, "MAIT",
                    "https://mait.ac.in/index.php/placements/placement-details.html?view=article&id=1400:previous-years-placement&catid=10",
                    "https://mait.ac.in/index.php/placements/placement-details.html");

        };
    }

    private void insert(
            CollegeRepository repo,
            String name,
            String placementUrl,
            String cayUrl
    ) {
        repo.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    College c = new College();
                    c.setName(name);
                    c.setPlacementPageUrl(placementUrl);
                    c.setCayPageUrl(cayUrl);
                    return repo.save(c);
                });
    }
}

