package com.onegravity.colorpreference;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.ArrayRes;

import com.onegravity.colorpicker.ColorPickerPreferenceWidget;
import com.onegravity.colorpicker.R;
import com.onegravity.utils.AlphaPatternDrawable;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
public class ColorUtils {

    public static int[] extractColorArray(@ArrayRes int arrayId, Context context) {
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

    public static void showDialog(Context context, ColorDialog.OnColorSelectedListener listener, String tag,
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

    public static void attach(Context context, ColorDialog.OnColorSelectedListener listener, String tag) {
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
