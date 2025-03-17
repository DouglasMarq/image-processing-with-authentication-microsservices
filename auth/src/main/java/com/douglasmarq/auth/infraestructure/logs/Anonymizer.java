package com.douglasmarq.auth.infraestructure.logs;

import java.util.*;

public final class Anonymizer {

    private Anonymizer() {
        // Private constructor to prevent instantiation
    }

    private static final List<String> KEYS_TO_SUPPRESS = List.of("email");

    private static final List<String> KEYS_TO_CENSOR = List.of("email");

    public static Map<String, Object> desensitizeData(
            Map<String, Object> originalData, boolean shouldClone) {
        Map<String, Object> data = null;
        if (originalData != null) {
            data = shouldClone ? new HashMap<>(originalData) : new HashMap<>(originalData);
        }

        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof List<?>) {
                    desensitizeListData((List<?>) value);
                } else if (value instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> valueAsMap = (Map<String, Object>) value;
                    desensitizeData(valueAsMap, false);
                } else if (isKeyToBeSuppressed(key)) {
                    data.put(key, "SUPPRESSED");
                } else if (isKeyToBeCensored(key)) {
                    data.put(key, censorString(value));
                } else if (isKeyEmail(key)) {
                    data.put(key, censorEmail(value));
                }
            }
        }
        return data != null ? data : Collections.emptyMap();
    }

    private static void desensitizeListData(List<?> valueList) {
        for (Object value : valueList) {
            if (value instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapValue = (Map<String, Object>) value;
                desensitizeData(mapValue, false);
            }
        }
    }

    private static String censorString(Object value) {
        if (value == null) {
            return null;
        }
        String result = value.toString();
        if (result.length() <= 6) {
            return result;
        }
        String start = result.substring(0, 3);
        String censored = "*".repeat(result.length() - 6);
        String end = result.substring(result.length() - 3);
        return start + censored + end;
    }

    private static String censorEmail(Object emailString) {
        if (emailString == null) {
            return null;
        }
        String[] emailParts = emailString.toString().split("@");
        if (emailParts.length != 2) {
            return censorString(emailString);
        }
        String start = emailParts[0].length() >= 3 ? emailParts[0].substring(0, 3) : emailParts[0];
        String censored = emailParts[0].length() >= 3 ? "*".repeat(emailParts[0].length() - 3) : "";
        String end = "@" + emailParts[1];
        return start + censored + end;
    }

    private static boolean isKeyToBeSuppressed(String key) {
        return KEYS_TO_SUPPRESS.stream()
                .anyMatch(
                        k ->
                                key.toLowerCase(Locale.getDefault())
                                        .contains(k.toLowerCase(Locale.getDefault())));
    }

    private static boolean isKeyToBeCensored(String key) {
        return KEYS_TO_CENSOR.stream()
                .anyMatch(
                        k ->
                                key.toLowerCase(Locale.getDefault())
                                        .contains(k.toLowerCase(Locale.getDefault())));
    }

    private static boolean isKeyEmail(String key) {
        return key.toLowerCase(Locale.getDefault()).contains("email");
    }
}
