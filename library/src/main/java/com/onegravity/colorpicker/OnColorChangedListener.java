/*
 * Copyright 2012 Lars Werkman
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
 * An interface that is called whenever the color is changed.
 */
public interface OnColorChangedListener {
    /**
     * This method is called whenever the color changes.
     * This allows to update the element to configure in "real-time".
     *
     * @param color The new color.
     * @param dialogClosing True if the dialog is about to close meaning this will be the last call
     *                      to onColorChanged. False if the dialog is still open.
     */
    void onColorChanged(int color, boolean dialogClosing);
}