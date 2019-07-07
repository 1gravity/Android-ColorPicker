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

public interface ColorPickerListener extends OnColorChangedListener {

    /**
     * This method is called if the color picker dialog is closing.
     *
     * The color picker makes sure to call the onColorChanged before calling this to communicate
     * the last needed color change (reset to the initial color if the user selected cancel, set the
     * last picked color if the user selected ok).
     */
    void onDialogClosing();

}
