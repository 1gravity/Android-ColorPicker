package com.onegravity.colorpreference;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ArrayRes;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
class ColorUtils {

    static int[] extractColorArray(@ArrayRes int arrayId, Context context) {
        String[] choicesString = context.getResources().getStringArray(arrayId);
        int[] choicesInt = context.getResources().getIntArray(arrayId);

        // If user uses color reference(i.e. @color/color_choice) in the array,
        // the choicesString contains null values. We use the choicesInt in such case.
        boolean isStringArray = choicesString[0] != null;
        int length = isStringArray ? choicesString.length : choicesInt.length;

        int[] colorChoices = new int[length];
        for (int i = 0; i < length; i++) {
            colorChoices[i] = isStringArray ? Color.parseColor(choicesString[i]) : choicesInt[i];
        }

        return colorChoices;
    }

    static void showDialog(Context context, ColorDialog.OnColorSelectedListener listener, String tag,
                           int numColumns, ColorShape colorShape, int[] colorChoices, int selectedColorValue) {
        ColorDialog fragment = ColorDialog.newInstance(numColumns, colorShape, colorChoices, selectedColorValue);
        fragment.setOnColorSelectedListener(listener);

        Activity activity = Utils.resolveContext(context);
        if (activity != null) {
            activity.getFragmentManager()
                    .beginTransaction()
                    .add(fragment, tag)
                    .commit();
        }
    }

    static void attach(Context context, ColorDialog.OnColorSelectedListener listener, String tag) {
        Activity activity = Utils.resolveContext(context);
        if (activity != null) {
            ColorDialog fragment = (ColorDialog) activity.getFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                // re-bind preference to fragment
                fragment.setOnColorSelectedListener(listener);
            }
        }
    }


}
