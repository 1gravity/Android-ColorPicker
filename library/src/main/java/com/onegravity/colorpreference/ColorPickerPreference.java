/*
 * Copyright (C) 2015-2021 Emanuel Moecklin
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

package com.onegravity.colorpreference;

import android.content.Context;
import android.util.AttributeSet;

import com.onegravity.colorpicker.ColorPicker;
import com.onegravity.colorpicker.ColorPickerListener;

/**
 * A preference type that allows a user to choose a color
 */
public class ColorPickerPreference extends ColorPreferenceCompat implements ColorPickerListener {

    public ColorPickerPreference(Context context) {
        super(context);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onClick() {
        new ColorPicker(getContext(), getValue(), true, this).show();
    }

    @Override
    public void onColorChanged(int color) {
        setValue(color);
    }

    @Override
    public void onDialogClosing() {}

}
