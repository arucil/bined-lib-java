/*
 * Copyright (C) ExBin Project
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
package org.exbin.bined.swing.extended.color;

import java.awt.Color;
import javax.annotation.Nonnull;

/**
 * Interface for code area color profile.
 *
 * @version 0.2.0 2017/04/17
 * @author ExBin Project (http://exbin.org)
 */
public interface CodeAreaColorProfile {

    /**
     * Returns colors group for default colors.
     *
     * @return colors group
     */
    @Nonnull
    CodeAreaColorsGroup getDefaultColors();

    /**
     * Returns colors group for main colors.
     *
     * @return colors group
     */
    @Nonnull
    CodeAreaColorsGroup getMainColors();

    /**
     * Returns colors group for selection.
     *
     * @return colors group
     */
    @Nonnull
    CodeAreaColorsGroup getSelectionColors();

    /**
     * Returns colors group for selection in nonactive section.
     *
     * @return colors group
     */
    @Nonnull
    CodeAreaColorsGroup getMirrorSelectionColors();

    /**
     * Returns primary color.
     *
     * @return color
     */
    @Nonnull
    Color getPrimaryColor();

    /**
     * Returns primary background.
     *
     * @return color
     */
    @Nonnull
    Color getPrimaryBackgroundColor();
}
