package com.example.abdul.bank.common.utils;

import java.util.List;

public class StringHelper {
    public static String join(String delimiter, List<String> items) {
        int listSize = items.size();
        if (listSize == 0) {
            return "";
        }
        if (listSize == 1) {
            return items.get(0);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(items.get(0));

        for (int index = 1; index < listSize; index++) {
            builder.append(delimiter);
            builder.append(items.get(index));
        }
        return builder.toString();
    }
}
