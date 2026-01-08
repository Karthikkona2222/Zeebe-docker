package com.aaseya.AIS.utility;

import java.util.*;

public class MapStringParser {
 
    public static Map<String, Object> parseJavaMapString(String input) {
        if (input == null || input.trim().isEmpty() || !input.startsWith("{")) {
            return new HashMap<>();
        }
 
        Map<String, Object> result = new HashMap<>();
        String content = input.substring(1, input.length() - 1).trim(); // remove outer { }
 
        if (content.isEmpty()) {
            return result;
        }
 
        List<String> entries = splitTopLevel(content, ',');
 
        for (String entry : entries) {
            int eqIndex = findTopLevelEquals(entry);
            if (eqIndex == -1) continue;
 
            String key = entry.substring(0, eqIndex).trim();
            String valueStr = entry.substring(eqIndex + 1).trim();
 
            Object value = parseValue(valueStr);
            result.put(key, value);
        }
 
        return result;
    }
 
    private static Object parseValue(String valueStr) {
        valueStr = valueStr.trim();
 
        // Handle nested map
        if (valueStr.startsWith("{") && valueStr.endsWith("}")) {
            return parseJavaMapString(valueStr);
        }
 
        // Handle list
        if (valueStr.startsWith("[") && valueStr.endsWith("]")) {
            String listContent = valueStr.substring(1, valueStr.length() - 1).trim();
            if (listContent.isEmpty()) {
                return new ArrayList<>();
            }
            List<Object> list = new ArrayList<>();
            List<String> items = splitTopLevel(listContent, ',');
            for (String item : items) {
                list.add(parseValue(item.trim()));
            }
            return list;
        }
 
        // Return as string (no quotes to remove — Java toString doesn't add them)
        return valueStr;
    }
 
    private static List<String> splitTopLevel(String str, char delimiter) {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        int start = 0;
 
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
 
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == delimiter && depth == 0) {
                parts.add(str.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(str.substring(start)); // last part
        return parts;
    }
 
    private static int findTopLevelEquals(String str) {
        int depth = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '{' || c == '[') depth++;
            else if (c == '}' || c == ']') depth--;
            else if (c == '=' && depth == 0) {
                return i;
            }
        }
        return -1;
    }
}
 