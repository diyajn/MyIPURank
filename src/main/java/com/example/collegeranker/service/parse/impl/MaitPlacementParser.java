package com.example.collegeranker.service.parse.impl;

import com.example.collegeranker.entity.PlacementSummary;
import com.example.collegeranker.service.parse.PlacementParser;

import java.util.ArrayList;
import java.util.List;

public class MaitPlacementParser implements PlacementParser {

    @Override
    public List<PlacementSummary> parse(String text) {

        List<PlacementSummary> results = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");



        boolean is2025Table = false;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            // ----------- DETECT 2025 TABLE BY TITLE -----------
            if (!is2025Table && line.toUpperCase().contains("B.TECH 2025 BATCH")) {
                is2025Table = true;
                System.out.println("🟢 DETECTED 2025 TABLE BASED ON B.TECH 2025 BATCH TITLE → Using 2025 logic");
                continue; // skip the title line
            }

            String lower = line.toLowerCase();

            // ----------- SKIP GENERAL HEADER / FOOTER ROWS -----------
            if (lower.contains("company") || lower.contains("salary") ||
                    lower.contains("ctc") || lower.contains("placed") || lower.contains("total")) {
                continue;
            }

            String[] tokens = line.split("\\s+");
            if (tokens.length < 4) continue;

            // ----------- MUST START WITH S.NO -----------
            if (!tokens[0].matches("\\d+")) continue;
            int sno = Integer.parseInt(tokens[0]);

            try {
                if (is2025Table) {
                    StringBuilder companyBuilder = new StringBuilder();
                    Double salaryLpa = null;
                    Integer total = null;

                    // Start from index 1 (after SNo)
                    for (int i = 1; i < tokens.length; i++) {
                        String token = tokens[i];

                        if (token.equals("-")) continue; // skip null placeholder

                        if (token.matches("\\d+(\\.\\d+)?")) { // numeric token
                            if (salaryLpa == null) {
                                salaryLpa = Double.parseDouble(token); // first numeric = salary
                            } else if (total == null) {
                                total = Integer.parseInt(token); // next numeric = total
                            }
                        } else {
                            // Only append if salary not found yet
                            if (salaryLpa == null) {
                                companyBuilder.append(token).append(" ");
                            }
                        }
                    }

                    String company = companyBuilder.toString().trim();
                    if (company.isEmpty() || salaryLpa == null || total == null) {
                        System.out.println("❌ PARSE ERROR | SNo=" + sno + " | " + line);
                        continue;
                    }

                    PlacementSummary ps = new PlacementSummary();
                    ps.setCompanyName(company);
                    ps.setPackageLpa(salaryLpa);
                    ps.setStudentsPlaced(total);

                    results.add(ps);

                    continue;
                }


                // ---------- OLD MAIT LOGIC (OTHER YEARS) ----------
                StringBuilder companyBuilder = new StringBuilder();
                for (int i = 1; i < tokens.length; i++) {
                    if (tokens[i].equals("-")) break;
                    if (tokens[i].matches("\\d+(\\.\\d+)?")) break;
                    companyBuilder.append(tokens[i]).append(" ");
                }

                String company = companyBuilder.toString().trim();
                if (!company.matches(".*[A-Za-z].*")) continue;

                int total = Integer.parseInt(tokens[tokens.length - 2]);
                double salaryLpa = Double.parseDouble(tokens[tokens.length - 1]);

                PlacementSummary ps = new PlacementSummary();
                ps.setCompanyName(company);
                ps.setPackageLpa(salaryLpa);
                ps.setStudentsPlaced(total);

                results.add(ps);


            } catch (Exception e) {
                System.out.println("❌ PARSE ERROR | SNo=" + sno + " | " + line);
            }
        }


        return results;
    }
}














