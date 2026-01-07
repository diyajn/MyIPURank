package com.example.collegeranker.service.parse;

import com.example.collegeranker.entity.PlacementSummary;
import java.util.List;

public interface PlacementParser {
    List<PlacementSummary> parse(String text);
}




