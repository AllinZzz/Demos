package com.example.demos.restriction.funny.addworddemo.util;

/**
 * Created by yue on 2016/4/14.
 */
public class StringUtils {
    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }
}
