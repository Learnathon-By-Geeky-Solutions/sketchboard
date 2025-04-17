package com.example.lostnfound.util;

import java.util.regex.Pattern;

public class CodeUtils {
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$");

}
