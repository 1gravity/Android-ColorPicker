/*
 * Copyright (C) 2015-2023 Emanuel Moecklin
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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ExactComponent {

    private final OnColorChangedListener mListener;

    private EditText mExactViewA;
    private EditText mExactViewR;
    private EditText mExactViewG;
    private EditText mExactViewB;
    private ColorWheelView mExactColorPicker;

    final private int mInitialColor;
    final private boolean mUseOpacityBar;
    private int mNewColor;

    private boolean mTextIgnoreChanges;

    ExactComponent(int initialColor, int newColor, boolean useOpacityBar, OnColorChangedListener listener) {
        mInitialColor = initialColor;
        mNewColor = newColor;
        mUseOpacityBar = useOpacityBar;
        mListener = listener;
    }

    @SuppressLint("InflateParams")
    View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_exact, null);

        mExactViewA = view.findViewById(R.id.exactA);
        mExactViewR = view.findViewById(R.id.exactR);
        mExactViewG = view.findViewById(R.id.exactG);
        mExactViewB = view.findViewById(R.id.exactB);

        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(2)};
        mExactViewA.setFilters(filters);
        mExactViewR.setFilters(filters);
        mExactViewG.setFilters(filters);
        mExactViewB.setFilters(filters);

        mExactViewA.setVisibility(mUseOpacityBar ? View.VISIBLE : View.GONE);

        setColor(mInitialColor);

        mExactViewA.addTextChangedListener(mExactTextWatcher);
        mExactViewR.addTextChangedListener(mExactTextWatcher);
        mExactViewG.addTextChangedListener(mExactTextWatcher);
        mExactViewB.addTextChangedListener(mExactTextWatcher);

        mExactColorPicker = view.findViewById(R.id.picker_exact);
        mExactColorPicker.setOldCenterColor(mInitialColor);
        mExactColorPicker.setNewCenterColor(mNewColor);

        return view;
    }

    void activate(Context context, int newColor) {
        mNewColor = newColor;

        setColor(mNewColor);
        mExactColorPicker.setOldCenterColor(mInitialColor);
        mExactColorPicker.setNewCenterColor(mNewColor);

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mExactViewR, 0);
    }

    void deactivate(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mExactViewA.getWindowToken(), 0);
    }

    private void setColor(int color) {
        mTextIgnoreChanges = true;

        String[] colorComponents = Util.convertToARGB(color);
        mExactViewA.setText(colorComponents[0]);
        mExactViewR.setText(colorComponents[1]);
        mExactViewG.setText(colorComponents[2]);
        mExactViewB.setText(colorComponents[3]);

        mTextIgnoreChanges = false;
    }

    private final TextWatcher mExactTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        public void afterTextChanged(Editable s) {
            if (mTextIgnoreChanges) return;

            try {
                int color = Util.convertToColorInt(
                        mExactViewA.getText().toString(), mExactViewR.getText().toString(),
                        mExactViewG.getText().toString(), mExactViewB.getText().toString(),
                        mUseOpacityBar);
                mExactColorPicker.setNewCenterColor(color);
                if (mListener != null) {
                    mListener.onColorChanged(color);
                }
            } catch (NumberFormatException ignore) {}
        }
    };

}
