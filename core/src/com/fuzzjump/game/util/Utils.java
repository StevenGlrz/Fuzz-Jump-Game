package com.fuzzjump.game.util;

/**
 * Created by Steven on 2/2/2015.
 */
public class Utils {

    public static int[] stringToIntArray(String string) {
        String[] split = string.split(",");
        int[] arr = new int[split.length];
        for (int i = 0; i < split.length; i++)
            arr[i] = Integer.parseInt(split[i]);
        return arr;
    }
}
