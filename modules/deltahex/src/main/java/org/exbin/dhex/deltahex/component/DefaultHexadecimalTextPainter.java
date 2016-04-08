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
package org.exbin.dhex.deltahex.component;

import org.exbin.dhex.deltahex.HexadecimalUtils;
import org.exbin.dhex.deltahex.CaretPosition;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Hex editor line painter.
 *
 * @version 0.1.0 2016/04/03
 * @author ExBin Project (http://exbin.org)
 */
public class DefaultHexadecimalTextPainter implements HexadecimalTextPainter {

    private final Hexadecimal hexadecimal;

    public DefaultHexadecimalTextPainter(Hexadecimal hexadecimal) {
        this.hexadecimal = hexadecimal;
    }

    @Override
    public void paintText(Graphics g, long line, int positionY, long dataPosition, int bytesPerLine, int fontHeight, int charWidth, int byteOnLine) {
        g.setColor(hexadecimal.getTextColor());
        if (byteOnLine == 0 && hexadecimal.isShowLineNumbers()) {
            char[] lineNumberCode = HexadecimalUtils.longToHexChars(line);
            g.drawChars(lineNumberCode, 0, 8, 0, positionY - hexadecimal.getSubFontSpace());
        }
        if (dataPosition < hexadecimal.getData().getDataSize()) {
            byte dataByte = hexadecimal.getData().getByte(dataPosition);
            if (hexadecimal.getViewMode() != Hexadecimal.ViewMode.PREVIEW) {
                char[] chars = HexadecimalUtils.byteToHexChars(dataByte);
                g.drawChars(chars, 0, 2, hexadecimal.getHexadecimalX() + byteOnLine * charWidth * 3, positionY - hexadecimal.getSubFontSpace());
            }

            if (hexadecimal.getViewMode() != Hexadecimal.ViewMode.HEXADECIMAL) {
                // TODO don't compute for fonts with fixed width
                char[] previewChar = new char[]{(char) dataByte};
                int previewCharWidth = g.getFontMetrics().charWidth(previewChar[0]);
                int leftSpace = (charWidth - previewCharWidth) / 2;
                g.drawChars(previewChar, 0, 1, leftSpace + hexadecimal.getPreviewX() + byteOnLine * charWidth, positionY - hexadecimal.getSubFontSpace());
            }
        }
    }

    @Override
    public void paintBackground(Graphics g, long line, int positionY, long dataPosition, int bytesPerLine, int fontHeight, int charWidth) {
        g.setColor((line & 1) == 0 ? hexadecimal.getBackground() : hexadecimal.getOddBackgroundColor());
        g.fillRect(0, positionY - fontHeight, g.getClipBounds().width, fontHeight);

        Hexadecimal.SelectionRange selection = hexadecimal.getSelection();
        if (selection == null) {
            return;
        }

        int selectionStart = 0;
        int selectionEnd = 0;
        int selectionPreviewStart = 0;
        int selectionPreviewEnd = 0;

        long maxLinePosition = dataPosition + bytesPerLine;
        CaretPosition selectionFirst = selection.getSelectionFirst();
        CaretPosition selectionLast = selection.getSelectionLast();
        if (selectionFirst.getDataPosition() < maxLinePosition) {
            if (selectionFirst.getDataPosition() > dataPosition) {
                int linePosition = (int) (selectionFirst.getDataPosition() - dataPosition);
                int halfPosition = selectionFirst.isLowerHalf() ? 1 : 0;
                selectionStart = hexadecimal.getHexadecimalX() + charWidth * (linePosition * 3 + halfPosition);
                selectionPreviewStart = hexadecimal.getPreviewX() + charWidth * linePosition;
            } else {
                selectionStart = hexadecimal.getHexadecimalX();
                selectionPreviewStart = hexadecimal.getPreviewX();
            }
        }

        if (selectionLast.getDataPosition() > dataPosition && selectionFirst.getDataPosition() < maxLinePosition) {
            if (selectionLast.getDataPosition() > maxLinePosition) {
                selectionEnd = hexadecimal.getHexadecimalX() + bytesPerLine * charWidth * 3;
                selectionPreviewEnd = hexadecimal.getPreviewX() + bytesPerLine * charWidth;
            } else {
                int linePosition = (int) (selectionLast.getDataPosition() - dataPosition);
                int halfPosition = selectionLast.isLowerHalf() ? 1 : 0;
                selectionEnd = hexadecimal.getHexadecimalX() + charWidth * (linePosition * 3 + halfPosition);
                selectionPreviewEnd = hexadecimal.getPreviewX() + charWidth * linePosition;
            }
        }

        if (selectionEnd > 0) {
            Color hexadecimalColor;
            Color previewColor;
            switch (hexadecimal.getActiveSection()) {
                case HEXADECIMAL: {
                    hexadecimalColor = hexadecimal.getSelectionBackgroundColor();
                    previewColor = hexadecimal.getDualBackgroundColor();
                    break;
                }
                case PREVIEW: {
                    hexadecimalColor = hexadecimal.getDualBackgroundColor();
                    previewColor = hexadecimal.getSelectionBackgroundColor();
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected active section " + hexadecimal.getActiveSection().name());
                }
            }

            g.setColor(hexadecimalColor);
            g.fillRect(selectionStart, positionY - fontHeight, selectionEnd - selectionStart, fontHeight);

            if (hexadecimal.getViewMode() == Hexadecimal.ViewMode.DUAL) {
                g.setColor(previewColor);
                g.fillRect(selectionPreviewStart, positionY - fontHeight, selectionPreviewEnd - selectionPreviewStart, fontHeight);
            }
        }
    }
}
