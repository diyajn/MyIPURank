package com.example.collegeranker.service.parse.nirf;

public class StudentStrengthParser {

    public static class StudentStrength {
        public int male, female, total, withinState, outsideState, outsideCountry;
        public int economicallyBackward, sociallyChallenged;
        public int reimbGovt, reimbInstitution, reimbPrivate, noReimb;
    }

    public static StudentStrength parse(String text) {
        StudentStrength s = new StudentStrength();
        String section = slice(text, "Total Actual Student Strength", "Placement & Higher Studies");

        String[] lines = section.split("\\r?\\n");
        String dataLine = null;

        for (String line : lines) {
            if (line.contains("UG [4 Years") && line.matches(".*\\d+.*")) {
                dataLine = line.trim();
                break;
            }
        }

        if (dataLine != null) {
            // Remove label and split values
            String[] tokens = dataLine.replace("UG [4 Years Program(s)]", "").trim().split("\\s+");
            if (tokens.length >= 14) {
                s.male = parseInt(tokens[3]);
                s.female = parseInt(tokens[4]);
                s.total = parseInt(tokens[5]);
                s.withinState = parseInt(tokens[6]);
                s.outsideState = parseInt(tokens[7]);
                s.outsideCountry = parseInt(tokens[8]);
                s.economicallyBackward = parseInt(tokens[9]);
                s.sociallyChallenged = parseInt(tokens[10]);
                s.reimbGovt = parseInt(tokens[11]);
                s.reimbInstitution = parseInt(tokens[12]);
                s.reimbPrivate = parseInt(tokens[13]);
                s.noReimb = parseInt(tokens[14]);
            }
        }

        return s;
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.replaceAll("[^\\d]", "")); } catch (Exception e) { return 0; }
    }

    private static String slice(String text, String start, String end) {
        int s = text.indexOf(start);
        if (s < 0) return "";
        int e = end != null ? text.indexOf(end, s + 1) : -1;
        return e > s ? text.substring(s, e) : text.substring(s);
    }
}
