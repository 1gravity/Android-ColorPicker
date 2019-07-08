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

    public static void setColorViewValue(ImageView imageView, int color, boolean selected, ColorShape shape) {
        if (imageView instanceof ColorPickerPreferenceWidget) {
            ((ColorPickerPreferenceWidget)imageView).setColor(color, color);
        } else {
            Resources res = imageView.getContext().getResources();

            Drawable currentDrawable = imageView.getDrawable();
            GradientDrawable colorChoiceDrawable;
            if (currentDrawable instanceof GradientDrawable) {
                // Reuse drawable
                colorChoiceDrawable = (GradientDrawable) currentDrawable;
            } else {
                colorChoiceDrawable = new GradientDrawable();
                colorChoiceDrawable.setShape(shape == ColorShape.SQUARE ? GradientDrawable.RECTANGLE : GradientDrawable.OVAL);
            }

            // Set stroke to dark version of color
            int darkenedColor = Color.rgb(
                    Color.red(color) * 192 / 256,
                    Color.green(color) * 192 / 256,
                    Color.blue(color) * 192 / 256);

            colorChoiceDrawable.setColor(color);
            colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2, res.getDisplayMetrics()), darkenedColor);

            Drawable drawable = colorChoiceDrawable;
            if (selected) {
                BitmapDrawable checkmark = (BitmapDrawable) res.getDrawable(isColorDark(color)
                        ? R.drawable.checkmark_white
                        : R.drawable.checkmark_black);
                checkmark.setGravity(Gravity.CENTER);
                drawable = new LayerDrawable(new Drawable[]{
                        colorChoiceDrawable,
                        checkmark});
            }

            AlphaPatternDrawable apd = new AlphaPatternDrawable(imageView.getContext());
            Bitmap alphaPattern = apd.generatePatternBitmap(imageView.getWidth(), imageView.getHeight());

//                imageView.setImageBitmap(alphaPattern);
//        imageView.setImageDrawable(drawable);

            Bitmap bitmap = drawableToBitmap(drawable, alphaPattern);
            imageView.setImageBitmap(bitmap);
//        imageView.setImageDrawable(drawable);
        }
    }

//    static private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
//        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
//        Canvas canvas = new Canvas(bmOverlay);
//        canvas.drawBitmap(bmp1, new Matrix(), null);
//        canvas.drawBitmap(bmp2, new Matrix(), null);
//        return bmOverlay;
//    }

    static private Bitmap drawableToBitmap (Drawable drawable, Bitmap alphaPattern) {
        Bitmap bitmap;

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        if (alphaPattern != null) canvas.drawBitmap(alphaPattern, new Matrix(), null);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas);
        return bitmap;
    }

    private static final int BRIGHTNESS_THRESHOLD = 150;

    private static boolean isColorDark(int color) {
        return ((30 * Color.red(color) +
                59 * Color.green(color) +
                11 * Color.blue(color)) / 100) <= BRIGHTNESS_THRESHOLD;
    }

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
