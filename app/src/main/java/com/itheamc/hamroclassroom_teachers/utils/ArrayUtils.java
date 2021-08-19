package com.itheamc.hamroclassroom_teachers.utils;

import java.util.Arrays;

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
}
