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

package com.onegravity.colorpicker.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.onegravity.colorpicker.ColorPicker;
import com.onegravity.colorpicker.ColorPickerListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ColorPickerListener {

    private View mRootLayout;
    private View mColorSetting1;
    private View mColorSetting2;

    private int mDialogId = -1;

    private int mBackgroundColor = 0x88000088;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore saved state
        if (savedInstanceState != null) {
            mDialogId = savedInstanceState.getInt("mDialogId");
            mBackgroundColor = savedInstanceState.getInt("mBackgroundColor");
        }

        // initialize layout
        setContentView(R.layout.main);

        // configure background color and set listener
        mRootLayout = findViewById(R.id.root_layout);
        mRootLayout.setBackgroundColor(mBackgroundColor);
        if (mDialogId != -1) {
            ColorPicker.setListener(mDialogId, this);
        }

        // configure color settings
        mColorSetting1 = findViewById(R.id.color_setting_1);
        mColorSetting2 = findViewById(R.id.color_setting_2);
        configureSettingColors();

        // configure OnClickListener
        findViewById(R.id.pick_color).setOnClickListener(this);
        findViewById(R.id.open_settings).setOnClickListener(this);
        mColorSetting1.setOnClickListener(this);
        mColorSetting2.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mDialogId", mDialogId);
        outState.putInt("mBackgroundColor", mBackgroundColor);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // configure background color
        if (id == R.id.pick_color) {
            if (mDialogId == -1) {
                // the dialog will stay open so don't open it again after an orientation change
                mDialogId = new ColorPicker(this, mBackgroundColor, true).show();
                ColorPicker.setListener(mDialogId, this);
            }
        }

        // color settings
        else {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // return from the settings screen
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            configureSettingColors();
        }
    }

    /**
     * Set the configured settings.
     */
    private void configureSettingColors() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int defColor1 = getResources().getColor(R.color.default_color_1);
        mColorSetting1.setBackgroundColor( prefs.getInt("color_setting_1", defColor1) );

        int defColor2 = getResources().getColor(R.color.default_color_2);
        mColorSetting2.setBackgroundColor( prefs.getInt("color_setting_2", defColor2) );
    }

    /**
     * Color picker color has changed, update the background color in "real-time".
     */
    @Override
    public void onColorChanged(int color) {
        mBackgroundColor = color;
        mRootLayout.setBackgroundColor(mBackgroundColor);
    }

    /**
     * Color picker is closing.
     */
    @Override
    public void onDialogClosing() {
        mDialogId = -1;
    }
}
