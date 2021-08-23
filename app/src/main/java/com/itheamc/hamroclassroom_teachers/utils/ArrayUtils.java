package com.itheamc.hamroclassroom_teachers.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayUtils {
    public static String toString(String[] strings) {
        return Arrays.toString(strings).replace("[", "").replace("]", "");
    }

    public static String toString(int[] ints) {
        return Arrays.toString(ints).replace("[", "").replace("]", "");
    }

    public static String toString(double[] doubles) {
        return Arrays.toString(doubles).replace("[", "").replace("]", "");
    }

    public static String[] toArray(String string, String regex) {
        return string.split(regex);
    }

    public static List<String> asList(String[] strings) {
        List<String> list = new ArrayList<>();
        for (String s: strings) {
            if (s == null || s.isEmpty() || !s.contains("https://")) continue;
            list.add(s);
        }

        return list;
    }
}
