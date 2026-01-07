package com.example.collegeranker.service.factory;

import com.example.collegeranker.service.parse.PlacementParser;
import com.example.collegeranker.service.parse.impl.*;

public class ParserFactory {

    public static PlacementParser getParser(String collegeName) {

        switch (collegeName.toUpperCase()) {
            case "MSIT":
                return new MsitPlacementParser();
            case "MAIT":
                return new MaitPlacementParser();
            case "BPIT":
                return new BpitPlacementParser();
            default:
                throw new IllegalArgumentException(
                        "No parser defined for college: " + collegeName);
        }
    }
}







