package com.example.collegeranker.service.parse.nirf;
import java.util.*;
import java.util.regex.*;

public class FacultyParser {
    public static class Faculty {
        public int srno;
        public String name;
        public int age;
        public String designation;
        public String gender;
        public String qualification;
        public int experienceMonths;
        public String working;
        public String joiningDate;
        public String leavingDate;
        public String associationType;
    }

    public static List<Faculty> parse(String text) {
        List<Faculty> list = new ArrayList<>();
        String section = slice(text, "Faculty Details", null);

        Pattern row = Pattern.compile(
                "(\\d+)\\s+([A-Za-z .]+)\\s+(\\d{2})\\s+([A-Za-z /]+)\\s+(Male|Female)\\s+([A-Za-z.]+)\\s+(\\d+)\\s+(Yes|No)\\s+([0-9\\-]+|--)\\s+([0-9\\-]+|--)\\s+(Regular|Adhoc / Contractual)",
                Pattern.CASE_INSENSITIVE);

        Matcher m = row.matcher(section);
        while (m.find()) {
            Faculty f = new Faculty();
            f.srno = Integer.parseInt(m.group(1));
            f.name = m.group(2).trim();
            f.age = Integer.parseInt(m.group(3));
            f.designation = m.group(4).trim();
            f.gender = m.group(5).trim();
            f.qualification = m.group(6).trim();
            f.experienceMonths = Integer.parseInt(m.group(7));
            f.working = m.group(8).trim();
            f.joiningDate = m.group(9).trim();
            f.leavingDate = m.group(10).trim();
            f.associationType = m.group(11).trim();
            list.add(f);
        }
        return list;
    }

    private static String slice(String text, String start, String end) {
        int s = text.indexOf(start);
        int e = end != null ? text.indexOf(end, s + 1) : -1;
        return e > s ? text.substring(s, e) : text.substring(s);
    }
}
