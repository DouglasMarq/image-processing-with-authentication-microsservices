package com.douglasmarq.imageservice.infraestructure.utils;

public class Base64Utils {
    public static String removePrefix(String base64Image) {
        return base64Image.replaceFirst("^data:image/[^;]+;base64,", "");
    }
}
