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
package org.exbin.bined.swing.extended;

import javax.annotation.Nonnull;
import org.exbin.bined.swing.CodeAreaWorker;
import org.exbin.bined.swing.basic.DefaultCodeAreaPainter;

/**
 * Extended code area component default painter.
 *
 * @version 0.2.0 2017/04/23
 * @author ExBin Project (http://exbin.org)
 */
public class ExtCodeAreaPainter extends DefaultCodeAreaPainter {

    public static final int DECORATION_HEADER_LINE = 1;
    public static final int DECORATION_LINENUM_LINE = 2;
    public static final int DECORATION_PREVIEW_LINE = 4;
    public static final int DECORATION_BOX = 8;
    public static final int DECORATION_DEFAULT = DECORATION_PREVIEW_LINE | DECORATION_LINENUM_LINE | DECORATION_HEADER_LINE;

//    protected final CodeArea codeArea;
//    private CodeAreaColorProfile colorProfile = null;
//
//    private Charset charMappingCharset = null;
//    protected final char[] charMapping = new char[256];
//    protected Map<Character, Character> unprintableCharactersMapping = null;
//
    public ExtCodeAreaPainter(@Nonnull CodeAreaWorker worker) {
        super(worker);
    }

//    @Override
//    public void paintOverall(Graphics g) {
//        // Fill header area background
//        Rectangle compRect = codeArea.getComponentRectangle();
//        Rectangle codeRect = codeArea.getCodeSectionRectangle();
//        if (compRect.y < codeRect.y) {
//            g.setColor(codeArea.getBackground());
//            g.fillRect(compRect.x, compRect.y, compRect.x + compRect.width, codeRect.y - compRect.y);
//        }
//
//        // Draw decoration lines
//        int decorationMode = codeArea.getDecorationMode();
//        if ((decorationMode & DECORATION_LINENUM_LINE) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            int lineX = codeRect.x - 1 - codeArea.getLineNumberSpace() / 2;
//            g.drawLine(lineX, compRect.y, lineX, codeRect.y);
//        }
//        if ((decorationMode & DECORATION_HEADER_LINE) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(compRect.x, codeRect.y - 1, compRect.x + compRect.width, codeRect.y - 1);
//        }
//        if ((decorationMode & DECORATION_BOX) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(codeRect.x - 1, codeRect.y - 1, codeRect.x + codeRect.width, codeRect.y - 1);
//        }
//    }
//
//    @Override
//    public void paintHeader(Graphics g) {
//        CodeArea.ScrollPosition scrollPosition = codeArea.getScrollPosition();
//        Rectangle compRect = codeArea.getComponentRectangle();
//        Rectangle codeRect = codeArea.getCodeSectionRectangle();
//        boolean monospaceFont = codeArea.isMonospaceFontDetected();
//        FontMetrics fontMetrics = codeArea.getFontMetrics();
//        int codeDigits = codeArea.getCodeType().getMaxDigits();
//        if (codeArea.getViewMode() != ViewMode.TEXT_PREVIEW) {
//            int charWidth = codeArea.getCharWidth();
//            int bytesPerLine = codeArea.getBytesPerLine();
//            int charsPerLine = codeArea.computeByteCharPos(bytesPerLine, false);
//            int headerX = codeRect.x - scrollPosition.getScrollCharPosition() * charWidth - scrollPosition.getScrollCharOffset();
//            int headerY = codeArea.getInsets().top + codeArea.getLineHeight() - codeArea.getSubFontSpace();
//
//            int visibleCharStart = (scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset()) / charWidth;
//            if (visibleCharStart < 0) {
//                visibleCharStart = 0;
//            }
//            int visibleCharEnd = (codeRect.width + (scrollPosition.getScrollCharPosition() + charsPerLine) * charWidth + scrollPosition.getScrollCharOffset()) / charWidth;
//            if (visibleCharEnd > charsPerLine) {
//                visibleCharEnd = charsPerLine;
//            }
//            int visibleStart = codeArea.computeByteOffsetPerCodeCharOffset(visibleCharStart);
//            int visibleEnd = codeArea.computeByteOffsetPerCodeCharOffset(visibleCharEnd - 1) + 1;
//
//            if (codeArea.getBackgroundMode() == CodeArea.BackgroundMode.GRIDDED) {
//                ColorsGroup stripColors = codeArea.getAlternateColors();
//                g.setColor(stripColors.getBackgroundColor());
//                int positionX = codeRect.x - scrollPosition.getScrollCharOffset() - scrollPosition.getScrollCharPosition() * charWidth;
//                for (int i = visibleStart / 2; i < visibleEnd / 2; i++) {
//                    g.fillRect(positionX + charWidth * codeArea.computeByteCharPos(i * 2 + 1), compRect.y, charWidth * codeDigits, codeRect.y - compRect.y);
//                }
//            }
//
//            g.setColor(codeArea.getForeground());
//            char[] headerChars = new char[charsPerLine];
//            Arrays.fill(headerChars, ' ');
//            CodeArea.CharRenderingMode charRenderingMode = codeArea.getCharRenderingMode();
//
//            boolean upperCase = codeArea.getHexCharactersCase() == HexCharactersCase.UPPER;
//            boolean interleaving = false;
//            int lastPos = 0;
//            for (int index = visibleStart; index < visibleEnd; index++) {
//                int codePos = codeArea.computeByteCharPos(index);
//                if (codePos == lastPos + 2 && !interleaving) {
//                    interleaving = true;
//                } else {
//                    CodeAreaUtils.longToBaseCode(headerChars, codePos, index, codeArea.getPositionCodeType().getBase(), 2, true, upperCase);
//                    lastPos = codePos;
//                    interleaving = false;
//                }
//            }
//
//            int renderOffset = visibleCharStart;
//            ColorsGroup.ColorType renderColorType = null;
//            Color renderColor = null;
//            for (int charOnLine = visibleCharStart; charOnLine < visibleCharEnd; charOnLine++) {
//                int byteOnLine;
//                byteOnLine = codeArea.computeByteOffsetPerCodeCharOffset(charOnLine);
//                boolean sequenceBreak = false;
//                boolean nativeWidth = true;
//
//                int currentCharWidth = 0;
//                ColorsGroup.ColorType colorType = ColorsGroup.ColorType.TEXT;
//                if (charRenderingMode != CodeArea.CharRenderingMode.LINE_AT_ONCE) {
//                    char currentChar = ' ';
//                    if (colorType == ColorsGroup.ColorType.TEXT) {
//                        currentChar = headerChars[charOnLine];
//                    }
//                    if (currentChar == ' ' && renderOffset == charOnLine) {
//                        renderOffset++;
//                        continue;
//                    }
//                    if (charRenderingMode == CodeArea.CharRenderingMode.AUTO && monospaceFont) {
//                        // Detect if character is in unicode range covered by monospace fonts
//                        if (currentChar > MIN_MONOSPACE_CODE_POINT && (int) currentChar < MAX_MONOSPACE_CODE_POINT
//                                && currentChar != INV_SPACE_CODE_POINT
//                                && currentChar != EXCEPTION1_CODE_POINT && currentChar != EXCEPTION2_CODE_POINT) {
//                            currentCharWidth = charWidth;
//                        }
//                    }
//
//                    if (currentCharWidth == 0) {
//                        currentCharWidth = fontMetrics.charWidth(currentChar);
//                        nativeWidth = currentCharWidth == charWidth;
//                    }
//                } else {
//                    currentCharWidth = charWidth;
//                }
//
//                Color color = getHeaderPositionColor(byteOnLine, charOnLine);
//                if (renderColorType == null) {
//                    renderColorType = colorType;
//                    renderColor = color;
//                    g.setColor(color);
//                }
//
//                if (!nativeWidth || !areSameColors(color, renderColor) || !colorType.equals(renderColorType)) {
//                    sequenceBreak = true;
//                }
//                if (sequenceBreak) {
//                    if (renderOffset < charOnLine) {
//                        g.drawChars(headerChars, renderOffset, charOnLine - renderOffset, headerX + renderOffset * charWidth, headerY);
//                    }
//
//                    if (!colorType.equals(renderColorType)) {
//                        renderColorType = colorType;
//                    }
//                    if (!areSameColors(color, renderColor)) {
//                        renderColor = color;
//                        g.setColor(color);
//                    }
//
//                    if (!nativeWidth) {
//                        renderOffset = charOnLine + 1;
//                        if (charRenderingMode == CodeArea.CharRenderingMode.TOP_LEFT) {
//                            g.drawChars(headerChars, charOnLine, 1, headerX + charOnLine * charWidth, headerY);
//                        } else {
//                            drawShiftedChar(g, headerChars, charOnLine, charWidth, headerX + charOnLine * charWidth, headerY, (charWidth + 1 - currentCharWidth) >> 1);
//                        }
//                    } else {
//                        renderOffset = charOnLine;
//                    }
//                }
//            }
//
//            if (renderOffset < charsPerLine) {
//                g.drawChars(headerChars, renderOffset, charsPerLine - renderOffset, headerX + renderOffset * charWidth, headerY);
//            }
//        }
//
//        int decorationMode = codeArea.getDecorationMode();
//        if ((decorationMode & DECORATION_HEADER_LINE) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(compRect.x, codeRect.y - 1, compRect.x + compRect.width, codeRect.y - 1);
//        }
//        if ((decorationMode & DECORATION_BOX) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(codeRect.x - 1, codeRect.y - 1, codeRect.x + codeRect.width, codeRect.y - 1);
//        }
//        if ((decorationMode & DECORATION_PREVIEW_LINE) > 0) {
//            int lineX = codeArea.getPreviewX() - scrollPosition.getScrollCharPosition() * codeArea.getCharWidth() - scrollPosition.getScrollCharOffset() - codeArea.getCharWidth() / 2;
//            if (lineX >= codeRect.x) {
//                g.setColor(codeArea.getDecorationLineColor());
//                g.drawLine(lineX, compRect.y, lineX, codeRect.y);
//            }
//        }
//    }
//
//    public Color getHeaderPositionColor(int byteOnLine, int charOnLine) {
//        return codeArea.getForeground();
//    }
//
//    @Override
//    public void paintBackground(Graphics g) {
//        Rectangle clipBounds = g.getClipBounds();
//        Rectangle codeRect = codeArea.getCodeSectionRectangle();
//        ColorsGroup mainColors = codeArea.getMainColors();
//        ColorsGroup stripColors = codeArea.getAlternateColors();
//        int bytesPerLine = codeArea.getBytesPerLine();
//        int lineHeight = codeArea.getLineHeight();
//        int startX = clipBounds.x;
//        int width = clipBounds.width;
//        if (!codeArea.isLineNumberBackground() && codeArea.isShowLineNumbers()) {
//            int lineNumberWidth = codeRect.x - 1 - codeArea.getLineNumberSpace() / 2;
//            if (startX < lineNumberWidth) {
//                int diff = lineNumberWidth - startX;
//                startX = lineNumberWidth;
//                width -= diff;
//            }
//        }
//        if (codeArea.getBackgroundMode() != CodeArea.BackgroundMode.NONE) {
//            g.setColor(mainColors.getBackgroundColor());
//            g.fillRect(startX, clipBounds.y, width, clipBounds.height);
//        }
//
//        CodeArea.ScrollPosition scrollPosition = codeArea.getScrollPosition();
//        long line = scrollPosition.getScrollLinePosition();
//        long maxDataPosition = codeArea.getDataSize();
//        int maxY = clipBounds.y + clipBounds.height;
//
//        int positionY;
//        long dataPosition = line * bytesPerLine;
//        if (codeArea.getBackgroundMode() == CodeArea.BackgroundMode.STRIPPED || codeArea.getBackgroundMode() == CodeArea.BackgroundMode.GRIDDED) {
//            g.setColor(stripColors.getBackgroundColor());
//
//            positionY = codeRect.y - scrollPosition.getScrollLineOffset();
//            if ((line & 1) == 0) {
//                positionY += lineHeight;
//                dataPosition += bytesPerLine;
//            }
//            while (positionY <= maxY && dataPosition < maxDataPosition) {
//                g.fillRect(startX, positionY, width, lineHeight);
//                positionY += lineHeight * 2;
//                dataPosition += bytesPerLine * 2;
//            }
//        }
//    }
//
//    @Override
//    public void paintLineNumbers(Graphics g) {
//        Rectangle clipBounds = g.getClipBounds();
//        Rectangle compRect = codeArea.getComponentRectangle();
//        Rectangle codeRect = codeArea.getCodeSectionRectangle();
//        int bytesPerLine = codeArea.getBytesPerLine();
//        int lineHeight = codeArea.getLineHeight();
//
//        CodeArea.ScrollPosition scrollPosition = codeArea.getScrollPosition();
//        long line = scrollPosition.getScrollLinePosition();
//        long maxDataPosition = codeArea.getDataSize();
//        int maxY = clipBounds.y + clipBounds.height + lineHeight;
//        long dataPosition = line * bytesPerLine - scrollPosition.getLineByteShift();
//        int charWidth = codeArea.getCharWidth();
//        int positionY = codeRect.y - codeArea.getSubFontSpace() - scrollPosition.getScrollLineOffset() + codeArea.getLineHeight();
//
//        g.setColor(codeArea.getForeground());
//        int lineNumberLength = codeArea.getLineNumberLength();
//        char[] lineNumberCode = new char[lineNumberLength];
//        boolean upperCase = codeArea.getHexCharactersCase() == HexCharactersCase.UPPER;
//        while (positionY <= maxY && dataPosition <= maxDataPosition) {
//            CodeAreaUtils.longToBaseCode(lineNumberCode, 0, dataPosition < 0 ? 0 : dataPosition, codeArea.getPositionCodeType().getBase(), lineNumberLength, true, upperCase);
//            if (codeArea.getCharRenderingMode() == CodeArea.CharRenderingMode.LINE_AT_ONCE) {
//                g.drawChars(lineNumberCode, 0, lineNumberLength, compRect.x, positionY);
//            } else {
//                for (int i = 0; i < lineNumberLength; i++) {
//                    drawCenteredChar(g, lineNumberCode, i, charWidth, compRect.x + charWidth * i, positionY);
//                }
//            }
//            positionY += lineHeight;
//            dataPosition += bytesPerLine;
//        }
//
//        // Draw decoration lines
//        int decorationMode = codeArea.getDecorationMode();
//        if ((decorationMode & DECORATION_LINENUM_LINE) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            int lineX = codeRect.x - 1 - codeArea.getLineNumberSpace() / 2;
//            g.drawLine(lineX, compRect.y, lineX, codeRect.y + codeRect.height);
//        }
//        if ((decorationMode & DECORATION_BOX) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(codeRect.x - 1, codeRect.y - 1, codeRect.x - 1, codeRect.y + codeRect.height);
//        }
//    }
//
//    @Override
//    public void paintMainArea(Graphics g) {
//        PaintData paintData = new PaintData(codeArea);
//        paintMainArea(g, paintData);
//    }
//
//    public void paintMainArea(Graphics g, PaintData paintData) {
//        if (paintData.viewMode != ViewMode.TEXT_PREVIEW && codeArea.getBackgroundMode() == CodeArea.BackgroundMode.GRIDDED) {
//            g.setColor(paintData.alternateColors.getBackgroundColor());
//            int positionX = paintData.codeSectionRect.x - paintData.scrollPosition.getScrollCharOffset() - paintData.scrollPosition.getScrollCharPosition() * paintData.charWidth;
//            for (int i = paintData.visibleCodeStart / 2; i < paintData.visibleCodeEnd / 2; i++) {
//                g.fillRect(positionX + paintData.charWidth * codeArea.computeByteCharPos(i * 2 + 1), paintData.codeSectionRect.y, paintData.charWidth * paintData.codeDigits, paintData.codeSectionRect.height);
//            }
//        }
//
//        int positionY = paintData.codeSectionRect.y - paintData.scrollPosition.getScrollLineOffset();
//        paintData.line = paintData.scrollPosition.getScrollLinePosition();
//        int positionX = paintData.codeSectionRect.x - paintData.scrollPosition.getScrollCharPosition() * paintData.charWidth - paintData.scrollPosition.getScrollCharOffset();
//        paintData.lineDataPosition = paintData.line * paintData.bytesPerLine - paintData.scrollPosition.getLineByteShift();
//        long dataSize = codeArea.getDataSize();
//
//        do {
//            if (paintData.showUnprintableCharacters) {
//                Arrays.fill(paintData.unprintableChars, ' ');
//            }
//            int lineBytesLimit = paintData.bytesPerLine;
//            if (paintData.lineDataPosition < dataSize) {
//                int lineDataSize = paintData.bytesPerLine + paintData.maxCharLength - 1;
//                if (paintData.lineDataPosition + lineDataSize > dataSize) {
//                    lineDataSize = (int) (dataSize - paintData.lineDataPosition);
//                }
//                if (paintData.lineDataPosition < 0) {
//                    paintData.lineStart = (int) -paintData.lineDataPosition;
//                } else {
//                    paintData.lineStart = 0;
//                }
//                codeArea.getData().copyToArray(paintData.lineDataPosition + paintData.lineStart, paintData.lineData, paintData.lineStart, lineDataSize - paintData.lineStart);
//                if (paintData.lineDataPosition + lineBytesLimit > dataSize) {
//                    lineBytesLimit = (int) (dataSize - paintData.lineDataPosition);
//                }
//            } else {
//                lineBytesLimit = 0;
//            }
//
//            // Fill codes
//            if (paintData.viewMode != ViewMode.TEXT_PREVIEW) {
//                for (int byteOnLine = Math.max(paintData.visibleCodeStart, paintData.lineStart); byteOnLine < Math.min(paintData.visibleCodeEnd, lineBytesLimit); byteOnLine++) {
//                    byte dataByte = paintData.lineData[byteOnLine];
//                    CodeAreaUtils.byteToCharsCode(dataByte, codeArea.getCodeType(), paintData.lineChars, codeArea.computeByteCharPos(byteOnLine), codeArea.getHexCharactersCase());
//                }
//                if (paintData.bytesPerLine > lineBytesLimit) {
//                    Arrays.fill(paintData.lineChars, codeArea.computeByteCharPos(lineBytesLimit), paintData.lineChars.length, ' ');
//                }
//            }
//
//            // Fill preview characters
//            if (paintData.viewMode != ViewMode.CODE_MATRIX) {
//                for (int byteOnLine = paintData.visiblePreviewStart; byteOnLine < Math.min(paintData.visiblePreviewEnd, lineBytesLimit); byteOnLine++) {
//                    byte dataByte = paintData.lineData[byteOnLine];
//
//                    if (paintData.maxCharLength > 1) {
//                        if (paintData.lineDataPosition + paintData.maxCharLength > dataSize) {
//                            paintData.maxCharLength = (int) (dataSize - paintData.lineDataPosition);
//                        }
//
//                        int charDataLength = paintData.maxCharLength;
//                        if (byteOnLine + charDataLength > paintData.lineData.length) {
//                            charDataLength = paintData.lineData.length - byteOnLine;
//                        }
//                        String displayString = new String(paintData.lineData, byteOnLine, charDataLength, paintData.charset);
//                        if (!displayString.isEmpty()) {
//                            paintData.lineChars[paintData.previewCharPos + byteOnLine] = displayString.charAt(0);
//                        }
//                    } else {
//                        if (charMappingCharset == null || charMappingCharset != paintData.charset) {
//                            buildCharMapping(paintData.charset);
//                        }
//
//                        paintData.lineChars[paintData.previewCharPos + byteOnLine] = charMapping[dataByte & 0xFF];
//                    }
//
//                    if (paintData.showUnprintableCharacters || paintData.charRenderingMode == CodeArea.CharRenderingMode.LINE_AT_ONCE) {
//                        if (unprintableCharactersMapping == null) {
//                            buildUnprintableCharactersMapping();
//                        }
//                        Character replacement = unprintableCharactersMapping.get(paintData.lineChars[paintData.previewCharPos + byteOnLine]);
//                        if (replacement != null) {
//                            if (paintData.showUnprintableCharacters) {
//                                paintData.unprintableChars[paintData.previewCharPos + byteOnLine] = replacement;
//                            }
//                            paintData.lineChars[paintData.previewCharPos + byteOnLine] = ' ';
//                        }
//                    }
//                }
//                if (paintData.bytesPerLine > lineBytesLimit) {
//                    Arrays.fill(paintData.lineChars, paintData.previewCharPos + lineBytesLimit, paintData.previewCharPos + paintData.bytesPerLine, ' ');
//                }
//            }
//            paintLineBackground(g, positionX, positionY, paintData);
//            paintLineText(g, positionX, positionY, paintData);
//            paintData.lineDataPosition += paintData.bytesPerLine;
//            paintData.line++;
//            positionY += paintData.lineHeight;
//        } while (positionY - paintData.lineHeight < paintData.codeSectionRect.y + paintData.codeSectionRect.height);
//
//        // Draw decoration lines
//        int decorationMode = codeArea.getDecorationMode();
//        if ((decorationMode & CodeArea.DECORATION_PREVIEW_LINE) > 0) {
//            int lineX = codeArea.getPreviewX() - paintData.scrollPosition.getScrollCharPosition() * codeArea.getCharWidth() - paintData.scrollPosition.getScrollCharOffset() - codeArea.getCharWidth() / 2;
//            if (lineX >= paintData.codeSectionRect.x) {
//                g.setColor(codeArea.getDecorationLineColor());
//                g.drawLine(lineX, paintData.codeSectionRect.y, lineX, paintData.codeSectionRect.y + paintData.codeSectionRect.height);
//            }
//        }
//    }
//
//    public void paintLineBackground(Graphics g, int linePositionX, int linePositionY, PaintData paintData) {
//        int renderOffset = paintData.visibleCharStart;
//        ColorsGroup.ColorType renderColorType = null;
//        Color renderColor = null;
//        for (int charOnLine = paintData.visibleCharStart; charOnLine < paintData.visibleCharEnd; charOnLine++) {
//            CodeAreaSection section;
//            int byteOnLine;
//            if (charOnLine >= paintData.previewCharPos && paintData.viewMode != ViewMode.CODE_MATRIX) {
//                byteOnLine = charOnLine - paintData.previewCharPos;
//                section = Section.TEXT_PREVIEW;
//            } else {
//                byteOnLine = codeArea.computeByteOffsetPerCodeCharOffset(charOnLine);
//                section = CodeAreaSection.CODE_MATRIX;
//            }
//            boolean sequenceBreak = false;
//
//            ColorsGroup.ColorType colorType = ColorsGroup.ColorType.BACKGROUND;
//            if (paintData.showUnprintableCharacters) {
//                if (paintData.unprintableChars[charOnLine] != ' ') {
//                    colorType = ColorsGroup.ColorType.UNPRINTABLES_BACKGROUND;
//                }
//            }
//
//            Color color = getPositionColor(byteOnLine, charOnLine, section, colorType, paintData);
//            if (renderColorType == null) {
//                renderColorType = colorType;
//                renderColor = color;
//                g.setColor(color);
//            }
//
//            if (!areSameColors(color, renderColor) || !colorType.equals(renderColorType)) {
//                sequenceBreak = true;
//            }
//            if (sequenceBreak) {
//                if (renderOffset < charOnLine) {
//                    if (renderColor != null) {
//                        renderBackgroundSequence(g, renderOffset, charOnLine, linePositionX, linePositionY, paintData);
//                    }
//                }
//
//                if (!colorType.equals(renderColorType)) {
//                    renderColorType = colorType;
//                }
//                if (!areSameColors(color, renderColor)) {
//                    renderColor = color;
//                    g.setColor(color);
//                }
//
//                renderOffset = charOnLine;
//            }
//        }
//
//        if (renderOffset < paintData.charsPerLine) {
//            if (renderColor != null) {
//                renderBackgroundSequence(g, renderOffset, paintData.charsPerLine, linePositionX, linePositionY, paintData);
//            }
//        }
//    }
//
//    private boolean areSameColors(Color color, Color comparedColor) {
//        return (color == null && comparedColor == null) || (color != null && color.equals(comparedColor));
//    }
//
//    public void paintLineText(Graphics g, int linePositionX, int linePositionY, PaintData paintData) {
//        int positionY = linePositionY + paintData.lineHeight - codeArea.getSubFontSpace();
//
//        int renderOffset = paintData.visibleCharStart;
//        ColorsGroup.ColorType renderColorType = null;
//        Color renderColor = null;
//        for (int charOnLine = paintData.visibleCharStart; charOnLine < paintData.visibleCharEnd; charOnLine++) {
//            Section section;
//            int byteOnLine;
//            if (charOnLine >= paintData.previewCharPos) {
//                byteOnLine = charOnLine - paintData.previewCharPos;
//                section = Section.TEXT_PREVIEW;
//            } else {
//                byteOnLine = codeArea.computeByteOffsetPerCodeCharOffset(charOnLine);
//                section = Section.CODE_MATRIX;
//            }
//            boolean sequenceBreak = false;
//            boolean nativeWidth = true;
//
//            int currentCharWidth = 0;
//            ColorsGroup.ColorType colorType = ColorsGroup.ColorType.TEXT;
//            if (paintData.charRenderingMode != CodeArea.CharRenderingMode.LINE_AT_ONCE) {
//                char currentChar = ' ';
//                if (paintData.showUnprintableCharacters) {
//                    currentChar = paintData.unprintableChars[charOnLine];
//                    if (currentChar != ' ') {
//                        colorType = ColorsGroup.ColorType.UNPRINTABLES;
//                    }
//                }
//                if (colorType == ColorsGroup.ColorType.TEXT) {
//                    currentChar = paintData.lineChars[charOnLine];
//                }
//                if (currentChar == ' ' && renderOffset == charOnLine) {
//                    renderOffset++;
//                    continue;
//                }
//                if (paintData.charRenderingMode == CodeArea.CharRenderingMode.AUTO && paintData.monospaceFont) {
//                    // Detect if character is in unicode range covered by monospace fonts
//                    if (currentChar > MIN_MONOSPACE_CODE_POINT && (int) currentChar < MAX_MONOSPACE_CODE_POINT
//                            && currentChar != INV_SPACE_CODE_POINT
//                            && currentChar != EXCEPTION1_CODE_POINT && currentChar != EXCEPTION2_CODE_POINT) {
//                        currentCharWidth = paintData.charWidth;
//                    }
//                }
//
//                if (currentCharWidth == 0) {
//                    currentCharWidth = paintData.fontMetrics.charWidth(currentChar);
//                    nativeWidth = currentCharWidth == paintData.charWidth;
//                }
//            } else {
//                currentCharWidth = paintData.charWidth;
//                if (paintData.showUnprintableCharacters) {
//                    char currentChar = paintData.unprintableChars[charOnLine];
//                    if (currentChar != ' ') {
//                        colorType = ColorsGroup.ColorType.UNPRINTABLES;
//                        currentCharWidth = paintData.fontMetrics.charWidth(currentChar);
//                        nativeWidth = currentCharWidth == paintData.charWidth;
//                    }
//                }
//            }
//
//            Color color = getPositionColor(byteOnLine, charOnLine, section, colorType, paintData);
//            if (renderColorType == null) {
//                renderColorType = colorType;
//                renderColor = color;
//                g.setColor(color);
//            }
//
//            if (!nativeWidth || !areSameColors(color, renderColor) || !colorType.equals(renderColorType)) {
//                sequenceBreak = true;
//            }
//            if (sequenceBreak) {
//                if (renderOffset < charOnLine) {
//                    renderCharSequence(g, renderOffset, charOnLine, linePositionX, positionY, renderColorType, paintData);
//                }
//
//                if (!colorType.equals(renderColorType)) {
//                    renderColorType = colorType;
//                }
//                if (!areSameColors(color, renderColor)) {
//                    renderColor = color;
//                    g.setColor(color);
//                }
//
//                if (!nativeWidth) {
//                    renderOffset = charOnLine + 1;
//                    if (paintData.charRenderingMode == CodeArea.CharRenderingMode.TOP_LEFT) {
//                        g.drawChars(
//                                renderColorType == ColorsGroup.ColorType.UNPRINTABLES ? paintData.unprintableChars : paintData.lineChars,
//                                charOnLine, 1, linePositionX + charOnLine * paintData.charWidth, positionY);
//                    } else {
//                        drawShiftedChar(g,
//                                renderColorType == ColorsGroup.ColorType.UNPRINTABLES ? paintData.unprintableChars : paintData.lineChars,
//                                charOnLine, paintData.charWidth, linePositionX + charOnLine * paintData.charWidth, positionY, (paintData.charWidth + 1 - currentCharWidth) >> 1);
//                    }
//                } else {
//                    renderOffset = charOnLine;
//                }
//            }
//        }
//
//        if (renderOffset < paintData.charsPerLine) {
//            renderCharSequence(g, renderOffset, paintData.charsPerLine, linePositionX, positionY, renderColorType, paintData);
//        }
//    }
//
//    /**
//     * Returns color of given type for specified position.
//     *
//     * Child implementation can override this to change rendering colors.
//     *
//     * @param byteOnLine byte on line
//     * @param charOnLine character on line
//     * @param section rendering section
//     * @param colorType color type
//     * @param paintData cached paint data
//     * @return color
//     */
//    public Color getPositionColor(int byteOnLine, int charOnLine, Section section, ColorsGroup.ColorType colorType, PaintData paintData) {
//        long dataPosition = paintData.lineDataPosition + byteOnLine;
//        SelectionRange selection = codeArea.getSelection();
//        if (selection != null && dataPosition >= selection.getFirst() && dataPosition <= selection.getLast() && (section == Section.TEXT_PREVIEW || charOnLine < paintData.charsPerCodeArea)) {
//            Section activeSection = codeArea.getActiveSection();
//            if (activeSection == section) {
//                return codeArea.getSelectionColors().getColor(colorType);
//            } else {
//                return codeArea.getMirrorSelectionColors().getColor(colorType);
//            }
//        }
//        if (colorType == ColorsGroup.ColorType.BACKGROUND) {
//            // Background is prepainted
//            return null;
//        }
//        if (((paintData.backgroundMode == CodeArea.BackgroundMode.STRIPPED || paintData.backgroundMode == CodeArea.BackgroundMode.GRIDDED) && (paintData.line & 1) > 0)
//                || (paintData.backgroundMode == CodeArea.BackgroundMode.GRIDDED && ((byteOnLine & 1) > 0)) && section == Section.CODE_MATRIX) {
//            return codeArea.getAlternateColors().getColor(colorType);
//        }
//
//        return codeArea.getMainColors().getColor(colorType);
//    }
//
//    /**
//     * Render sequence of characters.
//     *
//     * Doesn't include character at offset end.
//     */
//    private void renderCharSequence(Graphics g, int startOffset, int endOffset, int linePositionX, int positionY, ColorsGroup.ColorType colorType, PaintData paintData) {
//        if (colorType == ColorsGroup.ColorType.UNPRINTABLES) {
//            g.drawChars(paintData.unprintableChars, startOffset, endOffset - startOffset, linePositionX + startOffset * paintData.charWidth, positionY);
//        } else {
//            g.drawChars(paintData.lineChars, startOffset, endOffset - startOffset, linePositionX + startOffset * paintData.charWidth, positionY);
//        }
//    }
//
//    /**
//     * Render sequence of background rectangles.
//     *
//     * Doesn't include character at offset end.
//     */
//    private void renderBackgroundSequence(Graphics g, int startOffset, int endOffset, int linePositionX, int positionY, PaintData paintData) {
//        g.fillRect(linePositionX + startOffset * paintData.charWidth, positionY, (endOffset - startOffset) * paintData.charWidth, paintData.lineHeight);
//    }
//
//    /**
//     * Draws char in array centering it in precomputed space.
//     *
//     * @param g graphics
//     * @param drawnChars array of chars
//     * @param charOffset index of target character in array
//     * @param charWidthSpace default character width
//     * @param startX X position of drawing area start
//     * @param positionY Y position of drawing area start
//     */
//    protected void drawCenteredChar(Graphics g, char[] drawnChars, int charOffset, int charWidthSpace, int startX, int positionY) {
//        FontMetrics fontMetrics = codeArea.getFontMetrics();
//        int charWidth = fontMetrics.charWidth(drawnChars[charOffset]);
//        drawShiftedChar(g, drawnChars, charOffset, charWidthSpace, startX, positionY, (charWidthSpace + 1 - charWidth) >> 1);
//    }
//
//    protected void drawShiftedChar(Graphics g, char[] drawnChars, int charOffset, int charWidthSpace, int startX, int positionY, int shift) {
//        g.drawChars(drawnChars, charOffset, 1, startX + shift, positionY);
//    }
//
//    @Override
//    public void paintCursor(Graphics g) {
//        if (!codeArea.hasFocus()) {
//            return;
//        }
//
//        CodeAreaCaret caret = codeArea.getCaret();
//        int bytesPerLine = codeArea.getBytesPerLine();
//        int lineHeight = codeArea.getLineHeight();
//        int charWidth = codeArea.getCharWidth();
//        int linesPerRect = codeArea.getLinesPerRect();
//        int codeDigits = codeArea.getCodeType().getMaxDigits();
//        Point cursorPoint = caret.getCursorPoint(bytesPerLine, lineHeight, charWidth, linesPerRect);
//        boolean cursorVisible = caret.isCursorVisible();
//        CodeAreaCaret.CursorRenderingMode renderingMode = caret.getRenderingMode();
//
//        if (cursorVisible && cursorPoint != null) {
//            g.setColor(codeArea.getCursorColor());
//            if (renderingMode == CodeAreaCaret.CursorRenderingMode.XOR) {
//                g.setXORMode(Color.WHITE);
//            }
//
//            CodeAreaCaret.CursorShape cursorShape = codeArea.getEditationMode() == EditationMode.INSERT ? caret.getInsertCursorShape() : caret.getOverwriteCursorShape();
//            int cursorThickness = 0;
//            if (cursorShape.getWidth() != CodeAreaCaret.CursorShapeWidth.FULL) {
//                cursorThickness = caret.getCursorThickness(cursorShape, charWidth, lineHeight);
//            }
//            switch (cursorShape) {
//                case LINE_TOP:
//                case DOUBLE_TOP:
//                case QUARTER_TOP:
//                case HALF_TOP: {
//                    paintCursorRect(g, cursorPoint.x, cursorPoint.y,
//                            charWidth, cursorThickness, renderingMode);
//                    break;
//                }
//                case LINE_BOTTOM:
//                case DOUBLE_BOTTOM:
//                case QUARTER_BOTTOM:
//                case HALF_BOTTOM: {
//                    paintCursorRect(g, cursorPoint.x, cursorPoint.y + lineHeight - cursorThickness,
//                            charWidth, cursorThickness, renderingMode);
//                    break;
//                }
//                case LINE_LEFT:
//                case DOUBLE_LEFT:
//                case QUARTER_LEFT:
//                case HALF_LEFT: {
//                    paintCursorRect(g, cursorPoint.x, cursorPoint.y, cursorThickness, lineHeight, renderingMode);
//                    break;
//                }
//                case LINE_RIGHT:
//                case DOUBLE_RIGHT:
//                case QUARTER_RIGHT:
//                case HALF_RIGHT: {
//                    paintCursorRect(g, cursorPoint.x + charWidth - cursorThickness, cursorPoint.y, cursorThickness, lineHeight, renderingMode);
//                    break;
//                }
//                case BOX: {
//                    paintCursorRect(g, cursorPoint.x, cursorPoint.y,
//                            charWidth, lineHeight, renderingMode);
//                    break;
//                }
//                case FRAME: {
//                    g.drawRect(cursorPoint.x, cursorPoint.y, charWidth, lineHeight - 1);
//                    break;
//                }
//                case BOTTOM_CORNERS:
//                case CORNERS: {
//                    int quarterWidth = charWidth / 4;
//                    int quarterLine = lineHeight / 4;
//                    if (cursorShape == CodeAreaCaret.CursorShape.CORNERS) {
//                        g.drawLine(cursorPoint.x, cursorPoint.y,
//                                cursorPoint.x + quarterWidth, cursorPoint.y);
//                        g.drawLine(cursorPoint.x + charWidth - quarterWidth, cursorPoint.y,
//                                cursorPoint.x + charWidth, cursorPoint.y);
//
//                        g.drawLine(cursorPoint.x, cursorPoint.y + 1,
//                                cursorPoint.x, cursorPoint.y + quarterLine);
//                        g.drawLine(cursorPoint.x + charWidth, cursorPoint.y + 1,
//                                cursorPoint.x + charWidth, cursorPoint.y + quarterLine);
//                    }
//
//                    g.drawLine(cursorPoint.x, cursorPoint.y + lineHeight - quarterLine - 1,
//                            cursorPoint.x, cursorPoint.y + lineHeight - 2);
//                    g.drawLine(cursorPoint.x + charWidth, cursorPoint.y + lineHeight - quarterLine - 1,
//                            cursorPoint.x + charWidth, cursorPoint.y + lineHeight - 2);
//
//                    g.drawLine(cursorPoint.x, cursorPoint.y + lineHeight - 1,
//                            cursorPoint.x + quarterWidth, cursorPoint.y + lineHeight - 1);
//                    g.drawLine(cursorPoint.x + charWidth - quarterWidth, cursorPoint.y + lineHeight - 1,
//                            cursorPoint.x + charWidth, cursorPoint.y + lineHeight - 1);
//                    break;
//                }
//                default: {
//                    throw new IllegalStateException("Unexpected cursor shape type " + cursorShape.name());
//                }
//            }
//
//            if (renderingMode == CodeAreaCaret.CursorRenderingMode.XOR) {
//                g.setPaintMode();
//            }
//        }
//
//        // Paint shadow cursor
//        if (codeArea.getViewMode() == ViewMode.DUAL && codeArea.isShowShadowCursor()) {
//            g.setColor(codeArea.getCursorColor());
//            Point shadowCursorPoint = caret.getShadowCursorPoint(bytesPerLine, lineHeight, charWidth, linesPerRect);
//            if (shadowCursorPoint != null) {
//                Graphics2D g2d = (Graphics2D) g.create();
//                Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
//                g2d.setStroke(dashed);
//                g2d.drawRect(shadowCursorPoint.x, shadowCursorPoint.y,
//                        charWidth * (codeArea.getActiveSection() == Section.TEXT_PREVIEW ? codeDigits : 1), lineHeight - 1);
//            }
//        }
//    }
//
//    private void paintCursorRect(Graphics g, int x, int y, int width, int height, CodeAreaCaret.CursorRenderingMode renderingMode) {
//        switch (renderingMode) {
//            case PAINT: {
//                g.fillRect(x, y, width, height);
//                break;
//            }
//            case XOR: {
//                Rectangle rect = new Rectangle(x, y, width, height);
//                Rectangle intersection = rect.intersection(g.getClipBounds());
//                if (!intersection.isEmpty()) {
//                    g.fillRect(intersection.x, intersection.y, intersection.width, intersection.height);
//                }
//                break;
//            }
//            case NEGATIVE: {
//                Rectangle rect = new Rectangle(x, y, width, height);
//                Rectangle intersection = rect.intersection(g.getClipBounds());
//                if (intersection.isEmpty()) {
//                    break;
//                }
//                Shape clip = g.getClip();
//                g.setClip(intersection.x, intersection.y, intersection.width, intersection.height);
//                CodeArea.ScrollPosition scrollPosition = codeArea.getScrollPosition();
//                g.fillRect(x, y, width, height);
//                g.setColor(codeArea.getNegativeCursorColor());
//                Rectangle codeRect = codeArea.getCodeSectionRectangle();
//                int previewX = codeArea.getPreviewX();
//                int charWidth = codeArea.getCharWidth();
//                int lineHeight = codeArea.getLineHeight();
//                int line = (y + scrollPosition.getScrollLineOffset() - codeRect.y) / lineHeight;
//                int scrolledX = x + scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset();
//                int posY = codeRect.y + (line + 1) * lineHeight - codeArea.getSubFontSpace() - scrollPosition.getScrollLineOffset();
//                if (codeArea.getViewMode() != ViewMode.CODE_MATRIX && scrolledX >= previewX) {
//                    int charPos = (scrolledX - previewX) / charWidth;
//                    long dataSize = codeArea.getDataSize();
//                    long dataPosition = (line + scrollPosition.getScrollLinePosition()) * codeArea.getBytesPerLine() + charPos - scrollPosition.getLineByteShift();
//                    if (dataPosition >= dataSize) {
//                        g.setClip(clip);
//                        break;
//                    }
//
//                    char[] previewChars = new char[1];
//                    Charset charset = codeArea.getCharset();
//                    CharsetEncoder encoder = charset.newEncoder();
//                    int maxCharLength = (int) encoder.maxBytesPerChar();
//                    byte[] data = new byte[maxCharLength];
//
//                    if (maxCharLength > 1) {
//                        int charDataLength = maxCharLength;
//                        if (dataPosition + maxCharLength > dataSize) {
//                            charDataLength = (int) (dataSize - dataPosition);
//                        }
//
//                        codeArea.getData().copyToArray(dataPosition, data, 0, charDataLength);
//                        String displayString = new String(data, 0, charDataLength, charset);
//                        if (!displayString.isEmpty()) {
//                            previewChars[0] = displayString.charAt(0);
//                        }
//                    } else {
//                        if (charMappingCharset == null || charMappingCharset != charset) {
//                            buildCharMapping(charset);
//                        }
//
//                        previewChars[0] = charMapping[codeArea.getData().getByte(dataPosition) & 0xFF];
//                    }
//
//                    if (codeArea.isShowUnprintableCharacters()) {
//                        if (unprintableCharactersMapping == null) {
//                            buildUnprintableCharactersMapping();
//                        }
//                        Character replacement = unprintableCharactersMapping.get(previewChars[0]);
//                        if (replacement != null) {
//                            previewChars[0] = replacement;
//                        }
//                    }
//                    int posX = previewX + charPos * charWidth - scrollPosition.getScrollCharPosition() * charWidth - scrollPosition.getScrollCharOffset();
//                    if (codeArea.getCharRenderingMode() == CodeArea.CharRenderingMode.LINE_AT_ONCE) {
//                        g.drawChars(previewChars, 0, 1, posX, posY);
//                    } else {
//                        drawCenteredChar(g, previewChars, 0, charWidth, posX, posY);
//                    }
//                } else {
//                    int charPos = (scrolledX - codeRect.x) / charWidth;
//                    int byteOffset = codeArea.computeByteOffsetPerCodeCharOffset(charPos);
//                    int codeCharPos = codeArea.computeByteCharPos(byteOffset);
//                    char[] lineChars = new char[codeArea.getCodeType().getMaxDigits()];
//                    long dataSize = codeArea.getDataSize();
//                    long dataPosition = (line + scrollPosition.getScrollLinePosition()) * codeArea.getBytesPerLine() + byteOffset - scrollPosition.getLineByteShift();
//                    if (dataPosition >= dataSize) {
//                        g.setClip(clip);
//                        break;
//                    }
//
//                    byte dataByte = codeArea.getData().getByte(dataPosition);
//                    CodeAreaUtils.byteToCharsCode(dataByte, codeArea.getCodeType(), lineChars, 0, codeArea.getHexCharactersCase());
//                    int posX = codeRect.x + codeCharPos * charWidth - scrollPosition.getScrollCharPosition() * charWidth - scrollPosition.getScrollCharOffset();
//                    int charsOffset = charPos - codeCharPos;
//                    if (codeArea.getCharRenderingMode() == CodeArea.CharRenderingMode.LINE_AT_ONCE) {
//                        g.drawChars(lineChars, charsOffset, 1, posX + (charsOffset * charWidth), posY);
//                    } else {
//                        drawCenteredChar(g, lineChars, charsOffset, charWidth, posX + (charsOffset * charWidth), posY);
//                    }
//                }
//                g.setClip(clip);
//                break;
//            }
//        }
//    }
//
//    private void buildCharMapping(Charset charset) {
//        for (int i = 0; i < 256; i++) {
//            charMapping[i] = new String(new byte[]{(byte) i}, charset).charAt(0);
//        }
//        charMappingCharset = charset;
//    }
//
//    @Override
//    public void buildColors() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void rebuildColors() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void paintCursor() {
//        int bytesPerLine = codeArea.getBytesPerLine();
//        if (bytesPerLine > 0) {
//            int lineHeight = codeArea.getLineHeight();
//            int charWidth = codeArea.getCharWidth();
//            int linesPerRect = codeArea.getLinesPerRect();
//            Rectangle cursorRect = getCursorRect(bytesPerLine, lineHeight, charWidth, linesPerRect);
//            if (cursorRect != null) {
//                codeArea.paintImmediately(cursorRect);
//            }
//        }
//    }
//
//    @Override
//    public void notifyModified() {
//        computePaintData();
//        validateLineOffset();
//    }
//
//    private int computeCharsPerRect(int width) {
//        if (showLineNumbers) {
//            width -= paintDataCache.charWidth * paintDataCache.lineNumbersLength + getLineNumberSpace();
//        }
//
//        return width / paintDataCache.charWidth;
//    }
//
//    /**
//     * Computes how many bytes would fit into given number of characters.
//     *
//     * @param charsPerRect available characters space
//     * @return maximum byte offset index
//     */
//    public int computeFittingBytes(int charsPerRect) {
//        if (viewMode == ViewMode.TEXT_PREVIEW) {
//            return charsPerRect;
//        }
//
//        int fittingBytes;
//        if (byteGroupSize == 0) {
//            if (spaceGroupSize == 0) {
//                fittingBytes = (charsPerRect - 1)
//                        / (codeType.getMaxDigits() + 1);
//            } else {
//                fittingBytes = spaceGroupSize
//                        * (int) ((charsPerRect - 1) / (long) ((codeType.getMaxDigits() + 1) * spaceGroupSize + 2));
//                int remains = (int) ((charsPerRect - 1) % (long) ((codeType.getMaxDigits() + 1) * spaceGroupSize + 2)) / (codeType.getMaxDigits() + 1);
//                fittingBytes += remains;
//            }
//        } else if (spaceGroupSize == 0) {
//            fittingBytes = byteGroupSize
//                    * (int) ((charsPerRect - 1) / (long) ((codeType.getMaxDigits() + 1) * byteGroupSize + 1));
//            int remains = (int) ((charsPerRect - 1) % (long) ((codeType.getMaxDigits() + 1) * byteGroupSize + 1)) / (codeType.getMaxDigits() + 1);
//            fittingBytes += remains;
//        } else {
//            fittingBytes = 0;
//            int charsPerLine = 1;
//            while (charsPerLine < charsPerRect) {
//                charsPerLine += codeType.getMaxDigits() + 1;
//                fittingBytes++;
//                if ((fittingBytes % byteGroupSize) == 0) {
//                    if ((fittingBytes % spaceGroupSize) == 0) {
//                        charsPerLine += 2;
//                    } else {
//                        charsPerLine++;
//                    }
//                } else if ((fittingBytes % spaceGroupSize) == 0) {
//                    charsPerLine += 2;
//                }
//                if (charsPerLine > charsPerRect) {
//                    return fittingBytes - 1;
//                }
//            }
//
//            if (computeCharsPerLine(fittingBytes + 1) <= charsPerRect) {
//                fittingBytes++;
//            }
//        }
//
//        return fittingBytes;
//    }
//
//    /**
//     * Computes byte offset index for given code line offset.
//     *
//     * @param charOffset char offset position
//     * @return byte offset index
//     */
//    public int computeByteOffsetPerCodeCharOffset(int charOffset) {
//        int byteOffset;
//        if (byteGroupSize == 0) {
//            if (spaceGroupSize == 0) {
//                byteOffset = charOffset / codeType.getMaxDigits();
//            } else {
//                byteOffset = spaceGroupSize
//                        * (int) (charOffset / (long) (codeType.getMaxDigits() * spaceGroupSize + 2));
//                int remains = (int) (charOffset % (long) (codeType.getMaxDigits() * spaceGroupSize + 2)) / codeType.getMaxDigits();
//                if (remains >= spaceGroupSize) {
//                    remains = spaceGroupSize - 1;
//                }
//                byteOffset += remains;
//            }
//        } else if (spaceGroupSize == 0) {
//            byteOffset = byteGroupSize
//                    * (int) (charOffset / (long) (codeType.getMaxDigits() * byteGroupSize + 1));
//            int remains = (int) (charOffset % (long) (codeType.getMaxDigits() * byteGroupSize + 1)) / codeType.getMaxDigits();
//            if (remains >= byteGroupSize) {
//                remains = byteGroupSize - 1;
//            }
//            byteOffset += remains;
//        } else {
//            byteOffset = 0;
//            int charsPerLine = 0;
//            while (charsPerLine < charOffset) {
//                charsPerLine += codeType.getMaxDigits();
//                byteOffset++;
//                if ((byteOffset % byteGroupSize) == 0) {
//                    if ((byteOffset % spaceGroupSize) == 0) {
//                        charsPerLine += 2;
//                    } else {
//                        charsPerLine++;
//                    }
//                } else if ((byteOffset % spaceGroupSize) == 0) {
//                    charsPerLine += 2;
//                }
//                if (charsPerLine > charOffset) {
//                    return byteOffset - 1;
//                }
//            }
//        }
//
//        return byteOffset;
//    }
//
//    /**
//     * Computes number of characters for given number of bytes / offset.
//     *
//     * @param bytesPerLine number of bytes per line
//     * @return characters count
//     */
//    public int computeCharsPerLine(int bytesPerLine) {
//        if (viewMode == ViewMode.TEXT_PREVIEW) {
//            return bytesPerLine;
//        }
//
//        int charsPerLine = computeByteCharPos(bytesPerLine, false);
//
//        if (viewMode == ViewMode.DUAL) {
//            charsPerLine += bytesPerLine + 1;
//        }
//
//        return charsPerLine;
//    }
//
//    /**
//     * Computes character position for byte code of given offset position.
//     *
//     * @param byteOffset byte start offset
//     * @return characters position
//     */
//    public int computeByteCharPos(int byteOffset) {
//        return computeByteCharPos(byteOffset, true);
//    }
//
//    public int computeByteCharPos(int byteOffset, boolean includeTail) {
//        int charsPerLine = codeType.getMaxDigits() * byteOffset;
//        if (!includeTail) {
//            byteOffset--;
//        }
//        if (byteGroupSize == 0) {
//            if (spaceGroupSize != 0) {
//                charsPerLine += (byteOffset / spaceGroupSize) * 2;
//            }
//        } else if (spaceGroupSize == 0) {
//            charsPerLine += (byteOffset / byteGroupSize);
//        } else {
//            for (int index = 1; index <= byteOffset; index++) {
//                if ((index % byteGroupSize) == 0) {
//                    if ((index % spaceGroupSize) == 0) {
//                        charsPerLine += 2;
//                    } else {
//                        charsPerLine++;
//                    }
//                } else if ((index % spaceGroupSize) == 0) {
//                    charsPerLine += 2;
//                }
//            }
//        }
//
//        return charsPerLine;
//    }
//
//    public void computePaintData() {
//        if (paintDataCache.fontMetrics == null) {
//            return;
//        }
//
//        boolean verticalScrollBarVisible;
//        boolean horizontalScrollBarVisible;
//
//        Insets insets = getInsets();
//        Dimension size = getSize();
//        Rectangle compRect = paintDataCache.componentRectangle;
//        compRect.x = insets.left;
//        compRect.y = insets.top;
//        compRect.width = size.width - insets.left - insets.right;
//        compRect.height = size.height - insets.top - insets.bottom;
//
//        switch (lineNumberLength.getLineNumberType()) {
//            case AUTO: {
//                long dataSize = getDataSize();
//                if (dataSize > 0) {
//                    double natLog = Math.log(dataSize);
//                    paintDataCache.lineNumbersLength = (int) Math.ceil(natLog / positionCodeType.getBaseLog());
//                    if (paintDataCache.lineNumbersLength == 0) {
//                        paintDataCache.lineNumbersLength = 1;
//                    }
//                } else {
//                    paintDataCache.lineNumbersLength = 1;
//                }
//                break;
//            }
//            case SPECIFIED: {
//                paintDataCache.lineNumbersLength = lineNumberLength.getLineNumberLength();
//                break;
//            }
//        }
//
//        int charsPerRect = computeCharsPerRect(compRect.width);
//        int bytesPerLine;
//        if (wrapMode) {
//            bytesPerLine = computeFittingBytes(charsPerRect);
//            if (bytesPerLine == 0) {
//                bytesPerLine = 1;
//            }
//        } else {
//            bytesPerLine = lineLength;
//        }
//        long lines = ((data.getDataSize() + scrollPosition.lineByteShift) / bytesPerLine) + 1;
//        CodeAreaSpace.SpaceType headerSpaceType = headerSpace.getSpaceType();
//        switch (headerSpaceType) {
//            case NONE: {
//                paintDataCache.headerSpace = 0;
//                break;
//            }
//            case SPECIFIED: {
//                paintDataCache.headerSpace = headerSpace.getSpaceSize();
//                break;
//            }
//            case QUARTER_UNIT: {
//                paintDataCache.headerSpace = paintDataCache.lineHeight / 4;
//                break;
//            }
//            case HALF_UNIT: {
//                paintDataCache.headerSpace = paintDataCache.lineHeight / 2;
//                break;
//            }
//            case ONE_UNIT: {
//                paintDataCache.headerSpace = paintDataCache.lineHeight;
//                break;
//            }
//            case ONE_AND_HALF_UNIT: {
//                paintDataCache.headerSpace = (int) (paintDataCache.lineHeight * 1.5f);
//                break;
//            }
//            case DOUBLE_UNIT: {
//                paintDataCache.headerSpace = paintDataCache.lineHeight * 2;
//                break;
//            }
//            default:
//                throw new IllegalStateException("Unexpected header space type " + headerSpaceType.name());
//        }
//
//        CodeAreaSpace.SpaceType lineNumberSpaceType = lineNumberSpace.getSpaceType();
//        switch (lineNumberSpaceType) {
//            case NONE: {
//                paintDataCache.lineNumberSpace = 0;
//                break;
//            }
//            case SPECIFIED: {
//                paintDataCache.lineNumberSpace = lineNumberSpace.getSpaceSize();
//                break;
//            }
//            case QUARTER_UNIT: {
//                paintDataCache.lineNumberSpace = paintDataCache.charWidth / 4;
//                break;
//            }
//            case HALF_UNIT: {
//                paintDataCache.lineNumberSpace = paintDataCache.charWidth / 2;
//                break;
//            }
//            case ONE_UNIT: {
//                paintDataCache.lineNumberSpace = paintDataCache.charWidth;
//                break;
//            }
//            case ONE_AND_HALF_UNIT: {
//                paintDataCache.lineNumberSpace = (int) (paintDataCache.charWidth * 1.5f);
//                break;
//            }
//            case DOUBLE_UNIT: {
//                paintDataCache.lineNumberSpace = paintDataCache.charWidth * 2;
//                break;
//            }
//            default:
//                throw new IllegalStateException("Unexpected line number space type " + lineNumberSpaceType.name());
//        }
//
//        Rectangle hexRect = paintDataCache.codeSectionRectangle;
//        hexRect.y = insets.top + (showHeader ? paintDataCache.lineHeight + paintDataCache.headerSpace : 0);
//        hexRect.x = insets.left + (showLineNumbers ? paintDataCache.charWidth * paintDataCache.lineNumbersLength + paintDataCache.lineNumberSpace : 0);
//
//        if (verticalScrollBarVisibility == ScrollBarVisibility.IF_NEEDED) {
//            verticalScrollBarVisible = lines > paintDataCache.linesPerRect;
//        } else {
//            verticalScrollBarVisible = verticalScrollBarVisibility == ScrollBarVisibility.ALWAYS;
//        }
//        if (verticalScrollBarVisible) {
//            charsPerRect = computeCharsPerRect(compRect.x + compRect.width - paintDataCache.scrollBarThickness);
//            if (wrapMode) {
//                bytesPerLine = computeFittingBytes(charsPerRect);
//                if (bytesPerLine <= 0) {
//                    bytesPerLine = 1;
//                }
//                lines = ((data.getDataSize() + scrollPosition.lineByteShift) / bytesPerLine) + 1;
//            }
//        }
//
//        paintDataCache.bytesPerLine = bytesPerLine;
//        paintDataCache.charsPerLine = computeCharsPerLine(bytesPerLine);
//
//        int maxWidth = compRect.x + compRect.width - hexRect.x;
//        if (verticalScrollBarVisible) {
//            maxWidth -= paintDataCache.scrollBarThickness;
//        }
//
//        if (horizontalScrollBarVisibility == ScrollBarVisibility.IF_NEEDED) {
//            horizontalScrollBarVisible = paintDataCache.charsPerLine * paintDataCache.charWidth > maxWidth;
//        } else {
//            horizontalScrollBarVisible = horizontalScrollBarVisibility == ScrollBarVisibility.ALWAYS;
//        }
//        if (horizontalScrollBarVisible) {
//            paintDataCache.linesPerRect = (hexRect.height - paintDataCache.scrollBarThickness) / paintDataCache.lineHeight;
//        }
//
//        hexRect.width = compRect.x + compRect.width - hexRect.x;
//        if (verticalScrollBarVisible) {
//            hexRect.width -= paintDataCache.scrollBarThickness;
//        }
//        hexRect.height = compRect.y + compRect.height - hexRect.y;
//        if (horizontalScrollBarVisible) {
//            hexRect.height -= paintDataCache.scrollBarThickness;
//        }
//
//        paintDataCache.bytesPerRect = hexRect.width / paintDataCache.charWidth;
//        paintDataCache.linesPerRect = hexRect.height / paintDataCache.lineHeight;
//
//        // Compute sections positions
//        paintDataCache.previewStartChar = 0;
//        if (viewMode == ViewMode.CODE_MATRIX) {
//            paintDataCache.previewX = -1;
//        } else {
//            paintDataCache.previewX = hexRect.x;
//            if (viewMode == ViewMode.DUAL) {
//                paintDataCache.previewStartChar = paintDataCache.charsPerLine - paintDataCache.bytesPerLine;
//                paintDataCache.previewX += (paintDataCache.charsPerLine - paintDataCache.bytesPerLine) * paintDataCache.charWidth;
//            }
//        }
//
//        // Compute scrollbar positions
//        boolean scrolled = false;
//        verticalScrollBar.setVisible(verticalScrollBarVisible);
//        if (verticalScrollBarVisible) {
//            int verticalScrollBarHeight = compRect.y + compRect.height - hexRect.y;
//            if (horizontalScrollBarVisible) {
//                verticalScrollBarHeight -= paintDataCache.scrollBarThickness - 2;
//            }
//            verticalScrollBar.setBounds(compRect.x + compRect.width - paintDataCache.scrollBarThickness, hexRect.y, paintDataCache.scrollBarThickness, verticalScrollBarHeight);
//
//            int verticalVisibleAmount;
//            scrollPosition.verticalMaxMode = false;
//            int verticalMaximum;
//            if (verticalScrollMode == VerticalScrollMode.PIXEL) {
//                if (lines * paintDataCache.lineHeight > Integer.MAX_VALUE) {
//                    scrollPosition.verticalMaxMode = true;
//                    verticalMaximum = Integer.MAX_VALUE;
//                    verticalVisibleAmount = (int) (hexRect.height * Integer.MAX_VALUE / lines);
//                } else {
//                    verticalMaximum = (int) (lines * paintDataCache.lineHeight);
//                    verticalVisibleAmount = hexRect.height;
//                }
//            } else if (lines > Integer.MAX_VALUE) {
//                scrollPosition.verticalMaxMode = true;
//                verticalMaximum = Integer.MAX_VALUE;
//                verticalVisibleAmount = (int) (hexRect.height * Integer.MAX_VALUE / paintDataCache.lineHeight / lines);
//            } else {
//                verticalMaximum = (int) lines;
//                verticalVisibleAmount = hexRect.height / paintDataCache.lineHeight;
//            }
//            if (verticalVisibleAmount == 0) {
//                verticalVisibleAmount = 1;
//            }
//            verticalScrollBar.setMaximum(verticalMaximum);
//            verticalScrollBar.setVisibleAmount(verticalVisibleAmount);
//
//            // Cap vertical scrolling
//            if (!scrollPosition.verticalMaxMode && verticalVisibleAmount < verticalMaximum) {
//                long maxLineScroll = verticalMaximum - verticalVisibleAmount;
//                if (verticalScrollMode == VerticalScrollMode.PER_LINE) {
//                    long lineScroll = scrollPosition.scrollLinePosition;
//                    if (lineScroll > maxLineScroll) {
//                        scrollPosition.scrollLinePosition = maxLineScroll;
//                        scrolled = true;
//                    }
//                } else {
//                    long lineScroll = scrollPosition.scrollLinePosition * paintDataCache.lineHeight + scrollPosition.scrollLineOffset;
//                    if (lineScroll > maxLineScroll) {
//                        scrollPosition.scrollLinePosition = maxLineScroll / paintDataCache.lineHeight;
//                        scrollPosition.scrollLineOffset = (int) (maxLineScroll % paintDataCache.lineHeight);
//                        scrolled = true;
//                    }
//                }
//            }
//        } else if (scrollPosition.scrollLinePosition > 0 || scrollPosition.scrollLineOffset > 0) {
//            scrollPosition.scrollLinePosition = 0;
//            scrollPosition.scrollLineOffset = 0;
//            scrolled = true;
//        }
//
//        horizontalScrollBar.setVisible(horizontalScrollBarVisible);
//        if (horizontalScrollBarVisible) {
//            int horizontalScrollBarWidth = compRect.x + compRect.width - hexRect.x;
//            if (verticalScrollBarVisible) {
//                horizontalScrollBarWidth -= paintDataCache.scrollBarThickness - 2;
//            }
//            horizontalScrollBar.setBounds(hexRect.x, compRect.y + compRect.height - paintDataCache.scrollBarThickness, horizontalScrollBarWidth, paintDataCache.scrollBarThickness);
//
//            int horizontalVisibleAmount;
//            int horizontalMaximum = paintDataCache.charsPerLine;
//            if (horizontalScrollMode == HorizontalScrollMode.PIXEL) {
//                horizontalVisibleAmount = hexRect.width;
//                horizontalMaximum *= paintDataCache.charWidth;
//            } else {
//                horizontalVisibleAmount = hexRect.width / paintDataCache.charWidth;
//            }
//            horizontalScrollBar.setMaximum(horizontalMaximum);
//            horizontalScrollBar.setVisibleAmount(horizontalVisibleAmount);
//
//            // Cap horizontal scrolling
//            int maxByteScroll = horizontalMaximum - horizontalVisibleAmount;
//            if (horizontalVisibleAmount < horizontalMaximum) {
//                if (horizontalScrollMode == HorizontalScrollMode.PIXEL) {
//                    int byteScroll = scrollPosition.scrollCharPosition * paintDataCache.charWidth + scrollPosition.scrollCharOffset;
//                    if (byteScroll > maxByteScroll) {
//                        scrollPosition.scrollCharPosition = maxByteScroll / paintDataCache.charWidth;
//                        scrollPosition.scrollCharOffset = maxByteScroll % paintDataCache.charWidth;
//                        scrolled = true;
//                    }
//                } else {
//                    int byteScroll = scrollPosition.scrollCharPosition;
//                    if (byteScroll > maxByteScroll) {
//                        scrollPosition.scrollCharPosition = maxByteScroll;
//                        scrolled = true;
//                    }
//                }
//            }
//        } else if (scrollPosition.scrollCharPosition > 0 || scrollPosition.scrollCharOffset > 0) {
//            scrollPosition.scrollCharPosition = 0;
//            scrollPosition.scrollCharOffset = 0;
//            scrolled = true;
//        }
//
//        if (scrolled) {
//            updateScrollBars();
//            notifyScrolled();
//        }
//    }
//
//    private void buildUnprintableCharactersMapping() {
//        unprintableCharactersMapping = new HashMap<>();
//        // Unicode control characters, might not be supported by font
//        for (int i = 0; i < 32; i++) {
//            unprintableCharactersMapping.put((char) i, Character.toChars(9216 + i)[0]);
//        }
//        // Space -> Middle Dot
//        unprintableCharactersMapping.put(' ', Character.toChars(183)[0]);
//        // Tab -> Right-Pointing Double Angle Quotation Mark
//        unprintableCharactersMapping.put('\t', Character.toChars(187)[0]);
//        // Line Feed -> Currency Sign
//        unprintableCharactersMapping.put('\r', Character.toChars(164)[0]);
//        // Carriage Return -> Pilcrow Sign
//        unprintableCharactersMapping.put('\n', Character.toChars(182)[0]);
//        // Ideographic Space -> Degree Sign
//        unprintableCharactersMapping.put(Character.toChars(127)[0], Character.toChars(176)[0]);
//    }
//
//    /**
//     * Paint cache data structure for single paint operation.
//     *
//     * Data copied from CodeArea for faster access + array space for line data.
//     */
//    protected static class PaintData {
//
//        protected ViewMode viewMode;
//        protected CodeArea.BackgroundMode backgroundMode;
//        protected Rectangle codeSectionRect;
//        protected CodeAreaScrollPosition scrollPosition;
//        protected int charWidth;
//        protected int bytesPerLine;
//        protected int lineHeight;
//        protected int codeDigits;
//        protected int byteGroupSize;
//        protected int spaceGroupSize;
//        protected int charsPerLine;
//        protected Charset charset;
//        protected int maxCharLength;
//        protected boolean showUnprintableCharacters;
//        protected CodeArea.CharRenderingMode charRenderingMode;
//        protected FontMetrics fontMetrics;
//        protected boolean monospaceFont;
//        protected int charsPerCodeArea;
//        protected int previewCharPos;
//        protected int visibleCharStart;
//        protected int visibleCharEnd;
//        protected int visibleCodeStart;
//        protected int visibleCodeEnd;
//        protected int visiblePreviewStart;
//        protected int visiblePreviewEnd;
//
//        protected ColorsGroup mainColors;
//        protected ColorsGroup alternateColors;
//
//        // Line related fields
//        protected int lineStart;
//        protected long lineDataPosition;
//        protected long line;
//
//        protected char[] lineChars;
//
//        /**
//         * Line data cache.
//         */
//        protected byte[] lineData;
//
//        /**
//         * Single line of unprintable characters.
//         */
//        protected char[] unprintableChars;
//
//        public PaintData(CodeArea codeArea) {
//            viewMode = codeArea.getViewMode();
//            backgroundMode = codeArea.getBackgroundMode();
//            codeSectionRect = codeArea.getCodeSectionRectangle();
//            scrollPosition = codeArea.getScrollPosition();
//            charWidth = codeArea.getCharWidth();
//            bytesPerLine = codeArea.getBytesPerLine();
//            lineHeight = codeArea.getLineHeight();
//            codeDigits = codeArea.getCodeType().getMaxDigits();
//            charset = codeArea.getCharset();
//            mainColors = codeArea.getMainColors();
//            alternateColors = codeArea.getAlternateColors();
//            charRenderingMode = codeArea.getCharRenderingMode();
//            fontMetrics = codeArea.getFontMetrics();
//            monospaceFont = codeArea.isMonospaceFontDetected();
//            byteGroupSize = codeArea.getByteGroupSize();
//            spaceGroupSize = codeArea.getSpaceGroupSize();
//
//            CharsetEncoder encoder = charset.newEncoder();
//            maxCharLength = (int) encoder.maxBytesPerChar();
//            lineData = new byte[bytesPerLine + maxCharLength - 1];
//            charsPerLine = codeArea.getCharsPerLine();
//
//            lineChars = new char[charsPerLine];
//            Arrays.fill(lineChars, ' ');
//
//            showUnprintableCharacters = codeArea.isShowUnprintableCharacters();
//            if (showUnprintableCharacters) {
//                unprintableChars = new char[charsPerLine];
//            }
//
//            charsPerCodeArea = codeArea.computeByteCharPos(bytesPerLine, false);
//            // Compute first and last visible character of the code area
//            if (viewMode == ViewMode.DUAL) {
//                previewCharPos = charsPerCodeArea + 1;
//            } else {
//                previewCharPos = 0;
//            }
//
//            if (viewMode == ViewMode.DUAL || viewMode == ViewMode.CODE_MATRIX) {
//                visibleCharStart = (scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset()) / charWidth;
//                if (visibleCharStart < 0) {
//                    visibleCharStart = 0;
//                }
//                visibleCharEnd = (codeSectionRect.width + (scrollPosition.getScrollCharPosition() + charsPerLine) * charWidth + scrollPosition.getScrollCharOffset()) / charWidth;
//                if (visibleCharEnd > charsPerCodeArea) {
//                    visibleCharEnd = charsPerCodeArea;
//                }
//                visibleCodeStart = codeArea.computeByteOffsetPerCodeCharOffset(visibleCharStart);
//                visibleCodeEnd = codeArea.computeByteOffsetPerCodeCharOffset(visibleCharEnd - 1) + 1;
//            } else {
//                visibleCharStart = 0;
//                visibleCharEnd = -1;
//                visibleCodeStart = 0;
//                visibleCodeEnd = -1;
//            }
//
//            if (viewMode == ViewMode.DUAL || viewMode == ViewMode.TEXT_PREVIEW) {
//                visiblePreviewStart = (scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset()) / charWidth - previewCharPos;
//                if (visiblePreviewStart < 0) {
//                    visiblePreviewStart = 0;
//                }
//                if (visibleCodeEnd < 0) {
//                    visibleCharStart = visiblePreviewStart + previewCharPos;
//                }
//                visiblePreviewEnd = (codeSectionRect.width + (scrollPosition.getScrollCharPosition() + 1) * charWidth + scrollPosition.getScrollCharOffset()) / charWidth - previewCharPos;
//                if (visiblePreviewEnd > bytesPerLine) {
//                    visiblePreviewEnd = bytesPerLine;
//                }
//                if (visiblePreviewEnd >= 0) {
//                    visibleCharEnd = visiblePreviewEnd + previewCharPos;
//                }
//            } else {
//                visiblePreviewStart = 0;
//                visiblePreviewEnd = -1;
//            }
//        }
//
//        public ViewMode getViewMode() {
//            return viewMode;
//        }
//
//        public CodeArea.BackgroundMode getBackgroundMode() {
//            return backgroundMode;
//        }
//
//        public Rectangle getCodeSectionRect() {
//            return codeSectionRect;
//        }
//
//        public CodeArea.ScrollPosition getScrollPosition() {
//            return scrollPosition;
//        }
//
//        public CodeArea.CharRenderingMode getCharRenderingMode() {
//            return charRenderingMode;
//        }
//
//        public int getCharWidth() {
//            return charWidth;
//        }
//
//        public int getBytesPerLine() {
//            return bytesPerLine;
//        }
//
//        public int getByteGroupSize() {
//            return byteGroupSize;
//        }
//
//        public int getSpaceGroupSize() {
//            return spaceGroupSize;
//        }
//
//        public int getLineHeight() {
//            return lineHeight;
//        }
//
//        public int getCodeDigits() {
//            return codeDigits;
//        }
//
//        public int getCharsPerLine() {
//            return charsPerLine;
//        }
//
//        public Charset getCharset() {
//            return charset;
//        }
//
//        public int getMaxCharLength() {
//            return maxCharLength;
//        }
//
//        public int getPreviewCharPos() {
//            return previewCharPos;
//        }
//
//        public boolean isShowUnprintableCharacters() {
//            return showUnprintableCharacters;
//        }
//
//        public ColorsGroup getMainColors() {
//            return mainColors;
//        }
//
//        public ColorsGroup getStripColors() {
//            return alternateColors;
//        }
//
//        public long getLineDataPosition() {
//            return lineDataPosition;
//        }
//
//        public long getLine() {
//            return line;
//        }
//
//        public int getVisibleCharStart() {
//            return visibleCharStart;
//        }
//
//        public int getVisibleCharEnd() {
//            return visibleCharEnd;
//        }
//
//        public int getVisibleCodeStart() {
//            return visibleCodeStart;
//        }
//
//        public int getVisibleCodeEnd() {
//            return visibleCodeEnd;
//        }
//
//        public int getVisiblePreviewStart() {
//            return visiblePreviewStart;
//        }
//
//        public int getVisiblePreviewEnd() {
//            return visiblePreviewEnd;
//        }
//    }
}
