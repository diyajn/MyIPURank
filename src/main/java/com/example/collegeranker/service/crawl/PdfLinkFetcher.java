package com.example.collegeranker.service.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class PdfLinkFetcher {

    public Map<String, String> fetchNirfLinks(String nirfPageUrl) throws Exception {
        Map<String, String> pdfMap = new LinkedHashMap<>();
        fetchFromPage("NIRF", nirfPageUrl, pdfMap); // no college-specific rules
        return pdfMap;
    }




    /**
     * Returns:
     *   pdfUrl -> pdfUrl (used later for extraction)
     */
    public Map<String, String> fetch(String collegeName,
                                     String placementUrl,
                                     String cayUrl) throws Exception {

        Map<String, String> pdfMap = new LinkedHashMap<>();

        // 1️⃣ Placement page
        fetchFromPage(collegeName, placementUrl, pdfMap);

        // 2️⃣ MAIT CAY page (extra page)
        if (cayUrl != null && !cayUrl.isBlank()) {
            fetchFromPage(collegeName, cayUrl, pdfMap);
        }

        return pdfMap;
    }

    // ------------------------------------------------------------

    private void fetchFromPage(String collegeName,
                               String pageUrl,
                               Map<String, String> pdfMap) throws Exception {

        System.out.println("\n🌐 Scanning page: " + pageUrl);

        Document doc = Jsoup.connect(pageUrl)
                .timeout(15_000)
                .get();

        Elements links = doc.select("a[href$=.pdf], a[href*=.pdf]");

        for (Element link : links) {

            String absHref = link.attr("abs:href").trim();
            if (absHref.isEmpty()) continue;

            String normalizedUrl = normalize(absHref);

            // ❌ Deduplication: same physical PDF only once
            if (pdfMap.containsKey(normalizedUrl)) {
                System.out.println("❌ DUPLICATE SKIPPED: " + normalizedUrl);
                continue;
            }

            String urlForCheck = normalizedUrl.toLowerCase(Locale.ROOT);

            // ------------------------------------------------
            // 🔥 COLLEGE-SPECIFIC URL RULES
            // ------------------------------------------------

            boolean allowed = false;

            if (collegeName.equalsIgnoreCase("BPIT")
                    || collegeName.equalsIgnoreCase("MSIT")) {

                // BPIT & MSIT → URL must contain "batch"
                allowed = urlForCheck.contains("batch");

            } else if (collegeName.equalsIgnoreCase("MAIT")) {

                // MAIT → URL must contain "placement"
                allowed = urlForCheck.contains("placement");
            }

            if (!allowed) {
                System.out.println("❌ DISCARDED (URL rule mismatch): " + normalizedUrl);
                continue;
            }

            // ✅ Store clean PDF URL
            pdfMap.put(normalizedUrl, normalizedUrl);

            System.out.println("✅ STORED PDF:");
            System.out.println("   " + normalizedUrl);
        }
    }

    // ------------------------------------------------------------

    /**
     * Normalize URL to avoid duplicates:
     * - remove query params
     * - remove fragments
     */
    private String normalize(String url) {
        try {
            URI uri = new URI(url);
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,
                    null
            ).toString();
        } catch (Exception e) {
            return url;
        }
    }
}



