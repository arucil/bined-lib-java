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
package org.exbin.bined.delta;

import javax.annotation.Nonnull;

/**
 * Listener for delta document data changes.
 *
 * @version 0.2.0 2018/04/27
 * @author ExBin Project (http://exbin.org)
 */
public interface DeltaDocumentChangedListener {

    /**
     * Change in data occured.
     *
     * @param window updated window for reference
     */
    void dataChanged(@Nonnull DeltaDocumentWindow window);
}
