package com.example.collegeranker.service.extract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcademicYearExtractor {

    // batch-2019-2023
    private static final Pattern BATCH_FULL_RANGE =
            Pattern.compile("batch[-_ ]?(20\\d{2})[-–](20\\d{2})",
                    Pattern.CASE_INSENSITIVE);

    // 2020-24 / 2019-23
    private static final Pattern SHORT_RANGE =
            Pattern.compile("(20\\d{2})[-–](\\d{2})");

    // 2020-2024
    private static final Pattern FULL_RANGE =
            Pattern.compile("(20\\d{2})[-–](20\\d{2})");

    // batch-2022-placement
    private static final Pattern BATCH_SINGLE =
            Pattern.compile("batch[-_ ]?(20\\d{2})",
                    Pattern.CASE_INSENSITIVE);

    // single year
    private static final Pattern SINGLE_YEAR =
            Pattern.compile("\\b(20\\d{2})\\b");

    public static String extract(String linkText,
                                 String pdfUrl,
                                 String pdfText) {

        String year;

        // 1️⃣ batch-2019-2023 → 2023 (BPIT)
        year = extractBatchFullRange(linkText);
        if (year == null) year = extractBatchFullRange(pdfUrl);
        if (year != null) return year;

        // 2️⃣ 2020-24 → 2024 (MAIT)
        year = extractShortRange(linkText);
        if (year == null) year = extractShortRange(pdfUrl);
        if (year == null) year = extractShortRange(pdfText);
        if (year != null) return year;

        // 3️⃣ batch-2022-placement → 2022 (MSIT)
        year = extractBatchSingle(linkText);
        if (year == null) year = extractBatchSingle(pdfUrl);
        if (year != null) return year;

        // 4️⃣ 2020-2024 → 2024
        year = extractFullRange(linkText);
        if (year == null) year = extractFullRange(pdfUrl);
        if (year == null) year = extractFullRange(pdfText);
        if (year != null) return year;

        // 5️⃣ fallback single year
        year = extractSingle(linkText);
        if (year == null) year = extractSingle(pdfUrl);
        if (year == null) year = extractSingle(pdfText);
        return year;
    }

    // --------------------------------------------------

    private static String extractBatchFullRange(String text) {
        if (text == null) return null;
        Matcher m = BATCH_FULL_RANGE.matcher(text);
        return m.find() ? m.group(2) : null;
    }

    private static String extractShortRange(String text) {
        if (text == null) return null;
        Matcher m = SHORT_RANGE.matcher(text);
        if (m.find()) {
            return m.group(1).substring(0, 2) + m.group(2); // 2020-24 → 2024
        }
        return null;
    }

    private static String extractBatchSingle(String text) {
        if (text == null) return null;
        Matcher m = BATCH_SINGLE.matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private static String extractFullRange(String text) {
        if (text == null) return null;
        Matcher m = FULL_RANGE.matcher(text);
        return m.find() ? m.group(2) : null;
    }

    private static String extractSingle(String text) {
        if (text == null) return null;
        Matcher m = SINGLE_YEAR.matcher(text);
        return m.find() ? m.group(1) : null;
    }
}












