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
package org.exbin.bined;

/**
 * Enumeration of basic cursor position section.
 *
 * @version 0.2.0 2017/04/17
 * @author ExBin Project (http://exbin.org)
 */
public enum BasicCodeAreaSection {

    /**
     * Section of code area with codes for binary data representation.
     */
    CODE_MATRIX(0),
    /**
     * Section of code area with textual preview characters.
     */
    TEXT_PREVIEW(1);

    private final int section;

    private BasicCodeAreaSection(int section) {
        this.section = section;
    }

    public int getSection() {
        return section;
    }
}
