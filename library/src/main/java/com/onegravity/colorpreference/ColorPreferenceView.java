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

package com.onegravity.colorpreference;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.onegravity.colorpicker.Util;
import com.onegravity.utils.AlphaPatternDrawable;

/**
 * The widget that shows the selected color for a Preference.
 * It's optimized to update in "real-time" when the user picks a color from the color wheel.
 */
public class ColorPreferenceView extends AppCompatImageView {

    private static final String IMAGE_VIEW_TAG = "#IMAGE_VIEW_TAG#";

    private int mDefaultSize;
    private int mCurrentSize;

    private Paint mColorPaint;

    private Paint mBorderColorPaint;

    public ColorPreferenceView(Context context) {
        super(context);
        init(context);
    }

    public ColorPreferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorPreferenceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setTag(IMAGE_VIEW_TAG);
        setBackgroundColor(Color.TRANSPARENT);

        // create alpha pattern and set as image
        mDefaultSize = (int) (Util.getDisplayDensity(context) * 31); // 30dip
        mCurrentSize = mDefaultSize;
        setAlphaPattern(context, mDefaultSize);

        int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(new ViewGroup.LayoutParams(wrap, wrap));
    }

    private final Rect mCenterRectangle = new Rect();

    private AlphaPatternDrawable mAlphaPattern;

    private void setAlphaPattern(Context context, int size) {
        mAlphaPattern = new AlphaPatternDrawable(context);
        mAlphaPattern.generatePatternBitmap(size - 2, size - 2);
        mCenterRectangle.set(new Rect(0, 0, size, size));
    }

    public void setColor(int color, int borderColor) {
        mColorPaint = new Paint();
        mColorPaint.setColor(color);

        mBorderColorPaint = new Paint();
        mBorderColorPaint.setColor(borderColor);
        mBorderColorPaint.setStrokeWidth(2f);

        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            resize(getWidth(), getHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            resize(getWidth(), getHeight());
        }
    }

    private void resize(int width, int height) {
        int size = Math.min(Math.min(mDefaultSize, width), height);
        if (size != mCurrentSize) {
            mCurrentSize = size;
            setAlphaPattern(getContext(), mCurrentSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = 0;
        int y = 0;

        if (mAlphaPattern != null) {
            mAlphaPattern.setBounds(mCenterRectangle);
            mAlphaPattern.draw(canvas);
        }

        // draw color
        canvas.drawRect(x, y, mCurrentSize, mCurrentSize, mColorPaint);

        // draw border
        canvas.drawLine(x, y, x + mCurrentSize, y, mBorderColorPaint);
        canvas.drawLine(x, y, x, y + mCurrentSize, mBorderColorPaint);
        canvas.drawLine(x + mCurrentSize, y, x + mCurrentSize, y + mCurrentSize, mBorderColorPaint);
        canvas.drawLine(x, y + mCurrentSize, x + mCurrentSize, y + mCurrentSize, mBorderColorPaint);
    }

    public void setPreviewColor(int color, boolean isEnabled) {
        setTag(IMAGE_VIEW_TAG);

        // determine and set colors
        int borderColor = Color.WHITE;
        if (!isEnabled) {
            color = reduceBrightness(color, 1);
            borderColor = reduceBrightness(borderColor, 1);
        }

        setColor(color, borderColor);
    }

    private static int reduceBrightness(int color, int factor) {
        return (color & 0xff000000) +
                reduceBrightness(color, 0xff0000, factor) +
                reduceBrightness(color, 0x00ff00, factor) +
                reduceBrightness(color, 0x0000ff, factor);
    }

    private static int reduceBrightness(int color, int mask, int factor) {
        return ((color & mask) >>> factor) & mask;
    }
}
