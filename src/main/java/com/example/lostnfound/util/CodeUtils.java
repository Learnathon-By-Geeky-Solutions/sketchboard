package com.example.lostnfound.util;

import java.util.regex.Pattern;

public class CodeUtils {
    private CodeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$");

}
