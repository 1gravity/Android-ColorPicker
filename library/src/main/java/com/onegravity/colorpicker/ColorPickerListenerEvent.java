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

/**
 * We register the listener using EventBus because the ColorPickerPreference needs to re-register
 * the listener after an orientation change (the dialog is still open but the listener isn't
 * attached to it any more since we can't persist it).
 */
public class ColorPickerListenerEvent {

    /**
     * The is is used to map the publisher to the subscriber.
     * The subscriber provides the id and the publisher uses it to register its listener.
     */
    private final int mId;

    private final ColorPickerListener mListener;

    ColorPickerListenerEvent(int id, ColorPickerListener listener) {
        mId = id;
        mListener = listener;
    }

    public int getId() {
        return mId;
    }

    public ColorPickerListener getListener() {
        return mListener;
    }

}
