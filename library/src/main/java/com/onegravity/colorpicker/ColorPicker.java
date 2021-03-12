/*
 * Copyright (C) 2015-2019 Emanuel Moecklin
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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

import androidx.appcompat.app.AlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ColorPicker implements OnColorChangedListener, OnTabChangeListener {

    private static final String WHEEL_TAG = "wheel";
    private static final String EXACT_TAG = "exact";

    private static int sCount;

    final private int mId;

    final private Context mContext;

    final private int mInitialColor;

    private int mNewColor;

    final private boolean mUseOpacityBar;

    private Dialog mDialog;

    private ColorPickerListener mListener;

    private int mOrientation;

    private View mRootLayout;

    private ColorWheelComponent mColorWheelComponent;
    private ExactComponent mExactComponent;

    /**
     * @param context The context the color picker is using to build the dialog. Make sure it's the
     *                the correct context in terms of theming.
     * @param initialColor The initial color to show in the color picker. This is also the color
     *                     used if the user re-sets the color or cancels the dialog.
     * @param useOpacityBar True if the user should be able to change the opacity / alpha channel.
     */
    public ColorPicker(Context context, int initialColor, boolean useOpacityBar) {
        mId = sCount++;
        mContext = context;
        mInitialColor = initialColor;
        mNewColor = initialColor;
        mUseOpacityBar = useOpacityBar;
    }

    /**
     * Call this to register an ColorPickerListenerEvent with a certain dialog
     *
     * @param dialogId This identifies the dialog. It's the id returned by the ColorPicker.show() method.
     * @param listener The listener to register.
     */
    static public void setListener(int dialogId, ColorPickerListener listener) {
        ColorPickerListenerEvent event = new ColorPickerListenerEvent(dialogId, listener);
        EventBus.getDefault().post(event);
    }

    @SuppressLint("InflateParams")
    public int show() {
        mOrientation = Util.getScreenOrientation(mContext);

        mRootLayout = LayoutInflater.from(mContext).inflate(R.layout.dialog_color_picker, null);

        // init tabs
        initTabs(mRootLayout);

        /*
         * Create Dialog
         */
        mDialog = new AlertDialog.Builder(mContext)
                .setView(mRootLayout)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalizeChanges(mNewColor);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalizeChanges(mInitialColor);
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finalizeChanges(mInitialColor);
                    }
                })
                .create();

        mDialog.setCanceledOnTouchOutside(false);

        mDialog.show();

        // otherwise the keyboard won't show
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        EventBus.getDefault().register(this);

        return mId;
    }

    private void finalizeChanges(int color) {
        if (mListener != null) {
            mListener.onColorChanged(color);
            mListener.onDialogClosing();
        }
        EventBus.getDefault().unregister(this);
    }

    public void dismiss() {
        try { mDialog.dismiss(); } catch (Exception ignore) {}
    }

    /**
     * Set a OnColorChangedListener to get notified when the color selected by the user has changed.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ColorPickerListenerEvent event) {
        if (event.getId() == mId) {
            int screenOrientation = Util.getScreenOrientation(mContext);
            if (mOrientation != screenOrientation) {
                mOrientation = screenOrientation;
                initTabs(mRootLayout);
            }

            mListener = event.getListener();
        }
    }

    @Override
    public void onColorChanged(int color) {
        mNewColor = color;
        if (mListener != null) {
            mListener.onColorChanged(mNewColor);
        }
    }

    public int getColor() {
        return mColorWheelComponent.getColor();
    }

   // ****************************************** Tab Methods *******************************************

    private String mCurrentTab;

    private void initTabs(View rootLayout) {
        /*
         * Create tabs
         */
        TabHost tabHost = rootLayout.findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.clearAllTabs();
        tabHost.setOnTabChangedListener(null);        // or we would get NPEs in onTabChanged() when calling addTab()

        // TabContentFactory
        TabContentFactory tabFactory = new TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                if (tag.equals(WHEEL_TAG)) {
                    mColorWheelComponent = new ColorWheelComponent(mInitialColor, mNewColor, mUseOpacityBar, ColorPicker.this);
                    return mColorWheelComponent.createView(mContext);
                }
                else if (tag.equals(EXACT_TAG)) {
                    mExactComponent = new ExactComponent(mInitialColor, mNewColor, mUseOpacityBar, ColorPicker.this);
                    return mExactComponent.createView(mContext);
                }
                return null;
            }
        };

        // color wheel
        TabSpec tabSpec1 = tabHost.newTabSpec(WHEEL_TAG)
                .setIndicator(mContext.getString(R.string.color_picker_wheel))
                .setContent(tabFactory);
        tabHost.addTab(tabSpec1);

        // ARGB input field
        TabSpec tabSpec2 = tabHost.newTabSpec(EXACT_TAG)
                .setIndicator(mContext.getString(R.string.color_picker_exact))
                .setContent(tabFactory);
        tabHost.addTab(tabSpec2);

        tabHost.setOnTabChangedListener(this);
        String tag = mCurrentTab != null ? mCurrentTab : WHEEL_TAG;
        tabHost.setCurrentTabByTag(tag);
    }

    @Override
    public void onTabChanged(String tabId) {
        mCurrentTab = tabId;
        if (tabId.equals(WHEEL_TAG) && mColorWheelComponent != null) {
            mExactComponent.deactivate(mContext);
            mColorWheelComponent.activate(mContext, mNewColor);
        }
        else if (tabId.equals(EXACT_TAG) && mExactComponent != null) {
            mColorWheelComponent.deactivate(mContext);
            mExactComponent.activate(mContext, mNewColor);
        }
    }

}
