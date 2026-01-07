package com.example.collegeranker.service.parse.impl;

import com.example.collegeranker.entity.PlacementSummary;
import com.example.collegeranker.service.parse.PlacementParser;

import java.util.ArrayList;
import java.util.List;

public class MsitPlacementParser implements PlacementParser {

    @Override
    public List<PlacementSummary> parse(String text) {

        List<PlacementSummary> results = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");

        System.out.println("\n========== MSIT PARSER DEBUG ==========");

        for (String raw : lines) {

            String line = raw.trim();
            if (line.isEmpty()) continue;

            String lower = line.toLowerCase();

            // ----------- SKIP HEADER / TOTAL ROWS -----------
            if (lower.contains("total")
                    || lower.contains("grand")
                    || lower.contains("overall")
                    || lower.contains("company")
                    || lower.contains("ctc")
                    || lower.contains("salary")
                    || lower.contains("placed")) {
                continue;
            }

            String[] tokens = line.split("\\s+");
            if (tokens.length < 3) continue;

            // ----------- MUST START WITH S.NO -----------
            if (!tokens[0].matches("\\d+")) continue;
            int sno = Integer.parseInt(tokens[0]);

            try {
                // ----------- FIND CTC -----------
                int ctcIndex = -1;
                double ctc = 0.0;

                for (int i = 1; i < tokens.length; i++) {
                    if (tokens[i].matches("\\d+(\\.\\d+)?")) {
                        ctc = Double.parseDouble(tokens[i]);

                        // CR → LPA
                        if (i + 1 < tokens.length &&
                                tokens[i + 1].equalsIgnoreCase("Cr")) {
                            ctc *= 100;
                        }

                        ctcIndex = i;
                        break;
                    }
                }

                if (ctcIndex == -1) {
                    System.out.println("❌ NO CTC | SNo=" + sno + " | " + line);
                    continue;
                }

                // ----------- TOTAL (LAST COLUMN) -----------
                if (!tokens[tokens.length - 1].matches("\\d+")) continue;
                int total = Integer.parseInt(tokens[tokens.length - 1]);

                // ----------- COMPANY NAME (FIRST LINE ONLY) -----------
                StringBuilder companyBuilder = new StringBuilder();
                for (int i = 1; i < ctcIndex; i++) {
                    companyBuilder.append(tokens[i]).append(" ");
                }

                String company = companyBuilder.toString().trim();

                // allow "4 Way Technologies"
                if (!company.matches(".*[A-Za-z].*")) continue;

                PlacementSummary ps = new PlacementSummary();
                ps.setCompanyName(company);
                ps.setPackageLpa(ctc);
                ps.setStudentsPlaced(total);

                results.add(ps);

                System.out.println(
                        "✅ ACCEPTED | SNo=" + sno +
                                " | Company=" + company +
                                " | CTC=" + ctc +
                                " | Total=" + total
                );

            } catch (Exception e) {
                System.out.println("❌ PARSE ERROR | SNo=" + sno + " | " + line);
            }
        }

        System.out.println("========== MSIT PARSER END ==========\n");
        return results;
    }
}











