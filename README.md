# Android-ColorPicker

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

The Android Color Picker is a beautifully designed component based on Lars Werkman's color picker: https://github.com/LarsWerkman/HoloColorPicker.
Unlike the original it's shown in a dialog box, allows entering an (A)RGB value and includes a color picker Preference. 
A demo app can be found here: https://play.google.com/store/apps/details?id=com.onegravity.colorpicker.demo.

![Color picker dark theme](art/screenshot_0_framed_small.png?raw=true "Color picker dark theme") ![Color picker light theme](art/screenshot_1_framed_small.png?raw=true "Color picker light theme")

![Color picker demo app](art/screenshot_2_framed_small.png?raw=true "Color picker demo app")
![Color picker preference](art/screenshot_3_framed_small.png?raw=true "Color picker preference")

Features
--------

The color picker allows to:

* Pick colors using a HSV model (Hue, Saturation, Value = brightness/lightness)
* Pick colors by entering an (A)RGB value
* Change the opacity (alpha channel)
* Use the color picker as a preference (ColorPickerPreference)

Setup
-----
####**Dependencies**

Add this to your Gradle build file:
```
dependencies {
    compile 'com.1gravity:android-colorpicker:2.0.0'
}
```

#### **Proguard**

If you use Proguard in your app, please add the following lines to your configuration file:
```
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
```

####**Theming**

Simply use one of the AppCompat themes (Theme.AppCompat, Theme.AppCompat.Light or one of its derivatives).

#### **Use as Preference**
To use the color picker as a preference add this to your preferences file:
```
<com.onegravity.colorpicker.ColorPickerPreference
    android:key="my_setting"
    android:title="@string/my_setting"
    android:summary="@string/my_setting_summary"
    alphaSlider="true"
    android:defaultValue="@color/default_color_1"/>
```
The alphaSlider attribute can be used to show/hide the slider for the alpha slider (opacity).
The (A)RGB input field will reflect this attribute as well (allow/disallow entering an alpha value). 

#### **Use as Dialog**
To use the color picker as a dialog call:
```
int dialogId = new ColorPickerDialog(this, initialColor, true).show();
SetColorPickerListenerEvent.setListener(mDialogId, this);
```
The first line creates and opens the color picker dialog.
The **context** is used to build the dialog. Make sure it's the the correct context in terms of theming.
The **initialColor** is the initial color to show in the color picker. This is also the color used if the user re-sets the color or cancels the dialog.
Set **useOpacityBar** to True if the user should be able to change the opacity / alpha channel.

The color picker communicates the color changes to its caller through the ColorPickerListener. Instead of simply passing the listener to the ColorPickerDialog constructor (or using a setter method), we use EventBus.
Whoever has dealt with dialogs and orientation changes knows the numerous issues especially if you expect some return value from that dialog (a color in this case).
Using a unique identifier for the color picker dialog and EventBus allows to re-set the listener after an orientation change very easily.
All you need to do is save the unique identifier in onSaveInstanceState and restore it in onCreate (fragments will usually persist the identifier) and call SetColorPickerListenerEvent.setListener() again.
Please check out the included demo for some sample code.

Issues
------

If you have an issues with this library, please open a issue here: https://github.com/1gravity/Android-RTEditor/issues and provide enough information to reproduce it reliably. The following information needs to be provided:

* Which version of the SDK are you using?
* Which Android build are you using? (e.g. MPZ44Q)
* What device are you using?
* What steps will reproduce the problem? (Please provide the minimal reproducible test case.)
* What is the expected output?
* What do you see instead?
* Relevant logcat output.
* Optional: Link to any screenshot(s) that demonstrate the issue (shared privately in Drive.)
* Optional: Link to your APK (either downloadable in Drive or in the Play Store.)

License
-------

Copyright 2016 Emanuel Moecklin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
