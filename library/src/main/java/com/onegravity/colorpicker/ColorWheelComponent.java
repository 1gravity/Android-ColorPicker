package com.onegravity.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ColorWheelComponent {

    private OnColorChangedListener mListener;

    final private int mInitialColor;
    final private boolean mUseOpacityBar;
    private int mNewColor;

    private ColorWheelView mColorPicker;

    ColorWheelComponent(int initialColor, int newColor, boolean useOpacityBar, OnColorChangedListener listener) {
        mInitialColor = initialColor;
        mNewColor = newColor;
        mUseOpacityBar = useOpacityBar;
        mListener = listener;
    }

    @SuppressLint("InflateParams")
    View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_wheel, null);

        mColorPicker = (ColorWheelView) view.findViewById(R.id.picker);

        ValueBar valueBar = (ValueBar) view.findViewById(R.id.valuebar);
        if (valueBar != null) {
            mColorPicker.addValueBar(valueBar);
        }

        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.saturationbar);
        if (saturationBar != null) {
            mColorPicker.addSaturationBar(saturationBar);
        }

        OpacityBar opacityBar = (OpacityBar) view.findViewById(R.id.opacitybar);
        if (opacityBar != null) {
            if (mUseOpacityBar) {
                mColorPicker.addOpacityBar(opacityBar);
            }
            opacityBar.setVisibility(mUseOpacityBar ? View.VISIBLE : View.GONE);
        }

        mColorPicker.setOldCenterColor(mInitialColor);
        mColorPicker.setColor(mNewColor);
        mColorPicker.setOnColorChangedListener(mListener);

        return view;
    }

    void activate(Context context, int newColor) {
        mColorPicker.setColor(newColor);
    }

    void deactivate(Context context) {
        // do nothing
    }

    int getColor() {
        return mColorPicker.getColor();
    }

}
