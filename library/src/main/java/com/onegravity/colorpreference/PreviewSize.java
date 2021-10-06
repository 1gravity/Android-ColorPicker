package com.onegravity.colorpreference;

/**
 * Created by Kizito Nwose on 10/2/2016.
 */
public enum PreviewSize {
    NORMAL, LARGE;

    public static PreviewSize getSize(int num) {
        if (num == 2) {
            return LARGE;
        }
        return NORMAL;
    }
}
