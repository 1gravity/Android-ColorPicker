package com.onegravity.colorpreference;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.onegravity.colorpicker.R;

/**
 * Created by Kizito Nwose on 9/28/2016.
 */
public class ColorDialog extends DialogFragment {
    private GridLayout colorGrid;
    private OnColorSelectedListener colorSelectedListener;

    private static final String NUM_COLUMNS_KEY = "num_columns";
    private static final String COLOR_SHAPE_KEY = "color_shape";
    private static final String COLOR_CHOICES_KEY = "color_choices";
    private static final String SELECTED_COLOR_KEY = "selected_color";

    public ColorDialog() { }

    public static ColorDialog newInstance(int numColumns, ColorShape colorShape, int[] colorChoices, int selectedColorValue) {
        Bundle args = new Bundle();
        args.putInt(NUM_COLUMNS_KEY, numColumns);
        args.putSerializable(COLOR_SHAPE_KEY, colorShape);
        args.putIntArray(COLOR_CHOICES_KEY, colorChoices);
        args.putInt(SELECTED_COLOR_KEY, selectedColorValue);

        ColorDialog dialog = new ColorDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener colorSelectedListener) {
        this.colorSelectedListener = colorSelectedListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnColorSelectedListener) {
            setOnColorSelectedListener((OnColorSelectedListener) context);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        sizeDialog();
    }

    private void sizeDialog() {
        if (colorSelectedListener == null || colorGrid == null) {
            return;
        }

        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }

        final Resources res = colorGrid.getContext().getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        // Can't use Integer.MAX_VALUE here (weird issue observed otherwise on 4.2)
        colorGrid.measure(
                View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.AT_MOST));
        int width = colorGrid.getMeasuredWidth();
        int height = colorGrid.getMeasuredHeight();

        int extraPadding = res.getDimensionPixelSize(R.dimen.color_grid_extra_padding);

        width += extraPadding;
        height += extraPadding;

        dialog.getWindow().setLayout(width, height);
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int newColor, String tag);
    }

    public static class Builder {
        private int numColumns = 5;
        private int[] colorChoices;
        private ColorShape colorShape = ColorShape.CIRCLE;
        private Context context;
        private int selectedColor;
        private String tag;


        public <ColorActivityType extends Activity & OnColorSelectedListener> Builder(@NonNull ColorActivityType context) {
            this.context = context;
            //default colors
            setColorChoices(R.array.default_color_choice_values);
        }

        public Builder setNumColumns(int numColumns) {
            this.numColumns = numColumns;
            return this;
        }

        public Builder setColorChoices(@ArrayRes int colorChoicesRes) {
            this.colorChoices = ColorUtils.extractColorArray(colorChoicesRes, context);
            return this;
        }

        public Builder setColorShape(ColorShape colorShape) {
            this.colorShape = colorShape;
            return this;
        }

        public Builder setSelectedColor(@ColorInt int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        protected ColorDialog build() {
            ColorDialog dialog = ColorDialog.newInstance(numColumns, colorShape, colorChoices, selectedColor);
            dialog.setOnColorSelectedListener((OnColorSelectedListener) context);
            return dialog;
        }

        public ColorDialog show() {
            ColorDialog dialog = build();
            dialog.show(Utils.resolveContext(context).getFragmentManager(), tag == null ? String.valueOf(System.currentTimeMillis()) : tag);
            return dialog;
        }

    }
}
