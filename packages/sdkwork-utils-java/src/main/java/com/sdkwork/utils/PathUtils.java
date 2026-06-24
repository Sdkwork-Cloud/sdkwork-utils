package com.sdkwork.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class PathUtils {
    private PathUtils() {
    }

    public static String joinPath(String... segments) {
        return Arrays.stream(segments)
                .map(segment -> segment.replaceAll("^/+|/+$", ""))
                .filter(segment -> !segment.isEmpty())
                .collect(Collectors.joining("/"));
    }

    public static String normalizePath(String value) {
        String joined = Arrays.stream(value.split("/"))
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("/"));
        return value.startsWith("/") ? "/" + joined : joined;
    }
}
