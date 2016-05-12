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

    private OnColorChangedListener mListener;

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

        mExactViewA = (EditText) view.findViewById(R.id.exactA);
        mExactViewR = (EditText) view.findViewById(R.id.exactR);
        mExactViewG = (EditText) view.findViewById(R.id.exactG);
        mExactViewB = (EditText) view.findViewById(R.id.exactB);

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

        mExactColorPicker = (ColorWheelView) view.findViewById(R.id.picker_exact);
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

    private TextWatcher mExactTextWatcher = new TextWatcher() {
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
                    mListener.onColorChanged(color, false);
                }
            } catch (NumberFormatException ignore) {}
        }
    };

}
