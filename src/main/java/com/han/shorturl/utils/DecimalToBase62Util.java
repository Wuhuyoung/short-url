package com.han.shorturl.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author han
 * @date 2023/12/6 11:34
 */
public class DecimalToBase62Util {

    public static String decimalToBase62(long decimalNumber) {
        List<Character> base62Chars = generateBase62Chars();
        StringBuilder base62Number = new StringBuilder();

        while (decimalNumber > 0) {
            int remainder = (int) (decimalNumber % 62);
            base62Number.insert(0, base62Chars.get(remainder));
            decimalNumber /= 62;
        }

        return base62Number.toString();
    }

    private static List<Character> generateBase62Chars() {
        List<Character> base62Chars = new ArrayList<>();
        for (char c = '0'; c <= '9'; c++) {
            base62Chars.add(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            base62Chars.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            base62Chars.add(c);
        }
        return base62Chars;
    }
}

