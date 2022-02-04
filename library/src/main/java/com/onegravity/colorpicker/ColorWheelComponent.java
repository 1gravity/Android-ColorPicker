/*
 * Copyright (C) 2015-2022 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onegravity.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ColorWheelComponent {

    private final OnColorChangedListener mListener;

    final private int mInitialColor;
    final private boolean mUseOpacityBar;
    private final int mNewColor;

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

        mColorPicker = view.findViewById(R.id.picker);

        ValueBar valueBar = view.findViewById(R.id.valuebar);
        if (valueBar != null) {
            mColorPicker.addValueBar(valueBar);
        }

        SaturationBar saturationBar = view.findViewById(R.id.saturationbar);
        if (saturationBar != null) {
            mColorPicker.addSaturationBar(saturationBar);
        }

        OpacityBar opacityBar = view.findViewById(R.id.opacitybar);
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

    void activate(int newColor) {
        mColorPicker.setColor(newColor);
    }

    void deactivate() {
        // do nothing
    }

    int getColor() {
        return mColorPicker.getColor();
    }

}
