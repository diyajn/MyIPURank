package com.example.collegeranker.service.parse.impl;

import com.example.collegeranker.entity.PlacementSummary;
import com.example.collegeranker.service.parse.PlacementParser;

import java.util.*;

public class BpitPlacementParser implements PlacementParser {

    private static final Set<String> STREAM_CODES = new HashSet<>(Arrays.asList(
            "CSE", "IT", "ECE", "EEE", "ME", "CE", "A", "B", "C", "-A"
    ));

    private static final double USD_TO_INR = 89.96;
    private static final double LPA_IN_INR = 100_000.0;

    @Override
    public List<PlacementSummary> parse(String text) {

        Map<String, PlacementSummary> resultMap = new LinkedHashMap<>();
        String[] lines = text.split("\\r?\\n");

        System.out.println("\n========== BPIT PARSER DEBUG ==========");

        for (String raw : lines) {

            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            if (tokens.length < 6) continue;

            // ---------- MUST START WITH SNO ----------
            if (!tokens[0].matches("\\d+")) continue;

            try {
                // ---------- EXTRACT PACKAGE (LAST TWO TOKENS) ----------
                String rawPackage = tokens[tokens.length - 2] + " " + tokens[tokens.length - 1];
                double packageLpa = parsePackageToLpa(rawPackage);
                if (packageLpa <= 0) continue;

                // ---------- EXTRACT COMPANY NAME ----------
                int packageStart = tokens.length - 2;
                int streamEnd = -1;

                for (int i = 0; i < packageStart; i++) {
                    if (STREAM_CODES.contains(tokens[i])) {
                        streamEnd = i;
                    }
                }

                if (streamEnd == -1) continue;

                StringBuilder companyBuilder = new StringBuilder();
                for (int i = streamEnd + 1; i < packageStart; i++) {
                    companyBuilder.append(tokens[i]).append(" ");
                }

                String company = companyBuilder.toString().trim();
                if (company.isEmpty()) continue;

                // ---------- AGGREGATE (company + package) ----------
                String key = company + "@" + packageLpa;
                PlacementSummary ps = resultMap.get(key);

                if (ps == null) {
                    ps = new PlacementSummary();
                    ps.setCompanyName(company);
                    ps.setPackageLpa(packageLpa);
                    ps.setStudentsPlaced(1);
                    resultMap.put(key, ps);
                } else {
                    ps.setStudentsPlaced(ps.getStudentsPlaced() + 1);
                }

                System.out.println(
                        "✅ ACCEPTED | Company=" + company +
                                " | Package=" + packageLpa +
                                " | Count=" + ps.getStudentsPlaced()
                );

            } catch (Exception e) {
                System.out.println("❌ PARSE ERROR | " + line);
            }
        }

        System.out.println("========== BPIT PARSER END ==========\n");
        return new ArrayList<>(resultMap.values());
    }

    // ================= PACKAGE PARSER =================

    private double parsePackageToLpa(String raw) {

        String text = raw.toLowerCase().trim();

        // 4 LPA + 2 LPA
        if (text.contains("+")) {
            double sum = 0;
            for (String p : text.split("\\+")) {
                sum += parsePackageToLpa(p);
            }
            return round(sum);
        }

        // 7.2-9 LPA
        if (text.contains("-")) {
            String[] r = text.split("-");
            return round((extract(r[0]) + extract(r[1])) / 2);
        }

        // USD monthly
        if (text.contains("usd")) {
            double usd = extract(text);
            return round((usd * USD_TO_INR * 12) / LPA_IN_INR);
        }

        // 15k stipend
        if (text.contains("k")) {
            double monthly = extract(text) * 1000;
            return round((monthly * 12) / LPA_IN_INR);
        }

        // LPA
        if (text.contains("lpa")) {
            return round(extract(text));
        }

        // plain number
        return round(extract(text));
    }

    private double extract(String t) {
        String n = t.replaceAll("[^0-9.]", "");
        return n.isEmpty() ? 0 : Double.parseDouble(n);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}








