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
package org.exbin.deltahex.swing.basic;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.CodeAreaSection;
import org.exbin.deltahex.CodeAreaUtils;
import org.exbin.deltahex.CodeCharactersCase;
import org.exbin.deltahex.CodeAreaViewMode;
import org.exbin.deltahex.CodeType;
import org.exbin.deltahex.capability.CaretCapable;
import org.exbin.deltahex.capability.CharsetCapable;
import org.exbin.deltahex.capability.CodeCharactersCaseCapable;
import org.exbin.deltahex.capability.CodeTypeCapable;
import org.exbin.deltahex.capability.LineWrappingCapable;
import org.exbin.deltahex.swing.capability.ScrollingCapable;
import org.exbin.deltahex.capability.ViewModeCapable;
import org.exbin.deltahex.swing.CharacterRenderingMode;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.deltahex.swing.CodeAreaPainter;
import org.exbin.deltahex.swing.CodeAreaSwingUtils;
import org.exbin.deltahex.swing.CodeAreaWorker;
import org.exbin.deltahex.swing.capability.AntialiasingCapable;
import org.exbin.utils.binary_data.OutOfBoundsException;
import org.exbin.deltahex.swing.capability.BorderPaintCapable;
import org.exbin.deltahex.swing.capability.FontCapable;

/**
 * Code area component default painter.
 *
 * @version 0.2.0 2017/12/10
 * @author ExBin Project (http://exbin.org)
 */
public class DefaultCodeAreaPainter implements CodeAreaPainter {

    @Nonnull
    protected final CodeAreaWorker worker;
    private boolean initialized = false;

    @Nonnull
    private final CodeAreaDataView dataView;
    @Nonnull
    private final JScrollPane scrollPanel;

    private CodeAreaViewMode viewMode;
    private CodeAreaScrollPosition scrollPosition;
    private VerticalScrollUnit verticalScrollUnit;
    private HorizontalScrollUnit horizontalScrollUnit;
    private final Colors colors = new Colors();
    private long dataSize;

    private int areaWidth;
    private int areaHeight;
    private int mainAreaX;
    private int mainAreaY;
    private int mainAreaWidth;
    private int mainAreaHeight;

    private int lineNumbersLength;
    private int lineNumbersAreaWidth;
    private int headerAreaHeight;
    private int lineHeight;
    private int linesPerRect;
    private int bytesPerLine;
    private int charactersPerRect;
    private int charactersPerLine;
    private CodeType codeType;
    private CodeCharactersCase hexCharactersCase;
    private BasicBorderPaintMode borderPaintMode;

    private int previewCharPos;
    private int visibleCharStart;
    private int visibleCharEnd;
    private int visiblePreviewStart;
    private int visiblePreviewEnd;
    private int visibleCodeStart;
    private int visibleCodeEnd;

    private Charset charset;
    @Nullable
    private Font font;
    @Nullable
    private CharacterRenderingMode characterRenderingMode;
    private int maxCharLength;

    private byte[] lineData;
    private char[] lineNumberCode;
    private char[] lineCharacters;

    private int subFontSpace = 3;

    @Nullable
    private Charset charMappingCharset = null;
    private final char[] charMapping = new char[256];
    private long paintCounter = 0;

    private FontMetrics fontMetrics;
    private boolean monospaceFont;
    private int characterWidth;

    public DefaultCodeAreaPainter(@Nonnull CodeAreaWorker worker) {
        this.worker = worker;
        CodeArea codeArea = worker.getCodeArea();
        dataView = new CodeAreaDataView(codeArea);
        dataView.setOpaque(false);
        scrollPanel = new JScrollPane();
        scrollPanel.setBorder(null);
        JScrollBar verticalScrollBar = scrollPanel.getVerticalScrollBar();
        verticalScrollBar.setVisible(false);
        verticalScrollBar.setIgnoreRepaint(true);
        verticalScrollBar.addAdjustmentListener(new VerticalAdjustmentListener());
        JScrollBar horizontalScrollBar = scrollPanel.getHorizontalScrollBar();
        horizontalScrollBar.setIgnoreRepaint(false);
        horizontalScrollBar.setVisible(true);
        horizontalScrollBar.addAdjustmentListener(new HorizontalAdjustmentListener());
        codeArea.add(scrollPanel);
        scrollPanel.setOpaque(false);
        scrollPanel.setBackground(Color.RED);
        scrollPanel.setViewportView(dataView);
        scrollPanel.getViewport().setOpaque(false);

        DefaultCodeAreaMouseListener codeAreaMouseListener = new DefaultCodeAreaMouseListener(codeArea);
        codeArea.addMouseListener(codeAreaMouseListener);
        codeArea.addMouseMotionListener(codeAreaMouseListener);
        codeArea.addMouseWheelListener(codeAreaMouseListener);
        dataView.addMouseListener(codeAreaMouseListener);
        dataView.addMouseMotionListener(codeAreaMouseListener);
        dataView.addMouseWheelListener(codeAreaMouseListener);
    }

    @Override
    public void reset() {
        resetSizes();
        resetScrollState();
        resetColors();

        viewMode = ((ViewModeCapable) worker).getViewMode();
        characterRenderingMode = ((AntialiasingCapable) worker).getCharacterRenderingMode();
        hexCharactersCase = ((CodeCharactersCaseCapable) worker).getCodeCharactersCase();
        borderPaintMode = ((BorderPaintCapable) worker).getBorderPaintMode();
        dataSize = worker.getCodeArea().getDataSize();

        linesPerRect = computeLinesPerRectangle();
        bytesPerLine = computeBytesPerLine();

        codeType = ((CodeTypeCapable) worker).getCodeType();

        int charactersPerLine = 0;
        if (viewMode != CodeAreaViewMode.TEXT_PREVIEW) {
            charactersPerLine += computeLastCodeCharPos(bytesPerLine - 1) + 1;
        }
        if (viewMode != CodeAreaViewMode.CODE_MATRIX) {
            charactersPerLine += bytesPerLine;
            if (viewMode == CodeAreaViewMode.DUAL) {
                charactersPerLine++;
            }
        }

        this.charactersPerLine = charactersPerLine;
        hexCharactersCase = CodeCharactersCase.UPPER;
    }

    private void resetCharPositions() {
        charactersPerRect = computeLastCodeCharPos(bytesPerLine - 1);
        // Compute first and last visible character of the code area
        if (viewMode == CodeAreaViewMode.DUAL) {
            previewCharPos = charactersPerRect + 2;
        } else {
            previewCharPos = 0;
        }

        if (viewMode == CodeAreaViewMode.DUAL || viewMode == CodeAreaViewMode.CODE_MATRIX) {
            visibleCharStart = (scrollPosition.getScrollCharPosition() * characterWidth + scrollPosition.getScrollCharOffset()) / characterWidth;
            if (visibleCharStart < 0) {
                visibleCharStart = 0;
            }
            visibleCharEnd = (mainAreaWidth + (scrollPosition.getScrollCharPosition() + charactersPerLine) * characterWidth + scrollPosition.getScrollCharOffset()) / characterWidth;
            if (visibleCharEnd > charactersPerRect) {
                visibleCharEnd = charactersPerRect;
            }
            visibleCodeStart = computePositionByte(visibleCharStart);
            visibleCodeEnd = computePositionByte(visibleCharEnd - 1) + 1;
        } else {
            visibleCharStart = 0;
            visibleCharEnd = -1;
            visibleCodeStart = 0;
            visibleCodeEnd = -1;
        }

        if (viewMode == CodeAreaViewMode.DUAL || viewMode == CodeAreaViewMode.TEXT_PREVIEW) {
            visiblePreviewStart = (scrollPosition.getScrollCharPosition() * characterWidth + scrollPosition.getScrollCharOffset()) / characterWidth - previewCharPos;
            if (visiblePreviewStart < 0) {
                visiblePreviewStart = 0;
            }
            if (visibleCodeEnd < 0) {
                visibleCharStart = visiblePreviewStart + previewCharPos;
            }
            visiblePreviewEnd = (mainAreaWidth + (scrollPosition.getScrollCharPosition() + 1) * characterWidth + scrollPosition.getScrollCharOffset()) / characterWidth - previewCharPos;
            if (visiblePreviewEnd > bytesPerLine) {
                visiblePreviewEnd = bytesPerLine;
            }
            if (visiblePreviewEnd >= 0) {
                visibleCharEnd = visiblePreviewEnd + previewCharPos;
            }
        } else {
            visiblePreviewStart = 0;
            visiblePreviewEnd = -1;
        }

        lineData = new byte[bytesPerLine + maxCharLength - 1];
        lineNumberCode = new char[lineNumbersLength];
        lineCharacters = new char[charactersPerLine];
    }

    public void resetFont(@Nonnull Graphics g) {
        if (font == null) {
            reset();
        }

        charset = ((CharsetCapable) worker).getCharset();
        CharsetEncoder encoder = charset.newEncoder();
        maxCharLength = (int) encoder.maxBytesPerChar();

        font = ((FontCapable) worker).getFont();
        fontMetrics = g.getFontMetrics(font);
        /**
         * Use small 'w' character to guess normal font width.
         */
        characterWidth = fontMetrics.charWidth('w');
        /**
         * Compare it to small 'i' to detect if font is monospaced.
         *
         * TODO: Is there better way?
         */
        monospaceFont = characterWidth == fontMetrics.charWidth(' ') && characterWidth == fontMetrics.charWidth('i');
        int fontSize = font.getSize();
        lineHeight = fontSize + subFontSpace;

        lineNumbersLength = getLineNumberLength();
        resetSizes();
        resetCharPositions();
    }

    public void dataViewScrolled(@Nonnull Graphics g) {
        if (!isInitialized()) {
            return;
        }

        resetScrollState();
        if (characterWidth > 0) {
            resetCharPositions();
            paintComponent(g);
        }
    }

    private void resetScrollState() {
        scrollPosition = ((ScrollingCapable) worker).getScrollPosition();
        verticalScrollUnit = ((ScrollingCapable) worker).getVerticalScrollUnit();
        horizontalScrollUnit = ((ScrollingCapable) worker).getHorizontalScrollUnit();

        // TODO Overflow mode
        switch (horizontalScrollUnit) {
            case CHARACTER: {
                mainAreaX = scrollPosition.getScrollCharPosition();
                break;
            }
            case PIXEL: {
                mainAreaX = scrollPosition.getScrollCharPosition() * characterWidth + scrollPosition.getScrollCharOffset();
                break;
            }
        }

        switch (verticalScrollUnit) {
            case LINE: {
                mainAreaY = (int) scrollPosition.getScrollLinePosition();
                break;
            }
            case PIXEL: {
                mainAreaY = ((int) scrollPosition.getScrollLinePosition() * lineHeight) + scrollPosition.getScrollLineOffset();
                break;
            }
        }

        // TODO on resize only
        scrollPanel.setBounds(getDataViewRectangle());
        scrollPanel.revalidate();
    }

    private void resetSizes() {
        areaWidth = worker.getCodeArea().getWidth();
        areaHeight = worker.getCodeArea().getHeight();
        lineNumbersAreaWidth = characterWidth * (lineNumbersLength + 1);
        headerAreaHeight = 20;
        mainAreaWidth = areaWidth - lineNumbersAreaWidth;
        mainAreaHeight = areaHeight - headerAreaHeight;
    }

    private void resetColors() {
        CodeArea codeArea = worker.getCodeArea();
        colors.foreground = codeArea.getForeground();
        if (colors.foreground == null) {
            colors.foreground = Color.BLACK;
        }

        colors.background = codeArea.getBackground();
        if (colors.background == null) {
            colors.background = Color.WHITE;
        }
        colors.selectionForeground = UIManager.getColor("TextArea.selectionForeground");
        if (colors.selectionForeground == null) {
            colors.selectionForeground = Color.WHITE;
        }
        colors.selectionBackground = UIManager.getColor("TextArea.selectionBackground");
        if (colors.selectionBackground == null) {
            colors.selectionBackground = new Color(96, 96, 255);
        }
        colors.selectionMirrorForeground = colors.selectionForeground;
        int grayLevel = (colors.selectionBackground.getRed() + colors.selectionBackground.getGreen() + colors.selectionBackground.getBlue()) / 3;
        colors.selectionMirrorBackground = new Color(grayLevel, grayLevel, grayLevel);
        colors.cursor = UIManager.getColor("TextArea.caretForeground");
        colors.negativeCursor = CodeAreaSwingUtils.createNegativeColor(colors.cursor);
        if (colors.cursor == null) {
            colors.cursor = Color.BLACK;
        }
        colors.decorationLine = Color.GRAY;

        colors.stripes = CodeAreaSwingUtils.createOddColor(colors.background);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void paintComponent(@Nonnull Graphics g) {
        if (!initialized) {
            reset();
        }
        if (font == null) {
            resetFont(g);
        }

        paintOutsiteArea(g);
        paintHeader(g);
        paintLineNumbers(g);
        paintMainArea(g);
        paintCounter++;
    }

    public void paintOutsiteArea(@Nonnull Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, areaWidth, headerAreaHeight);
        g.setColor(Color.BLACK);
        g.fillRect(0, headerAreaHeight - 1, lineNumbersAreaWidth, 1);
    }

    public void paintHeader(@Nonnull Graphics g) {
        Rectangle clipBounds = g.getClipBounds();
        Rectangle headerArea = new Rectangle(lineNumbersAreaWidth, 0, areaWidth - lineNumbersAreaWidth, headerAreaHeight); // TODO minus scrollbar width
        g.setClip(clipBounds != null ? headerArea.intersection(clipBounds) : headerArea);

        g.setColor(colors.background);
        g.fillRect(headerArea.x, headerArea.y, headerArea.width, headerArea.height);

        // Black line
        g.setColor(Color.BLACK);
        g.fillRect(0, headerAreaHeight - 1, areaWidth, 1);

        if (viewMode != CodeAreaViewMode.TEXT_PREVIEW) {
            int charsPerLine = computeFirstCodeCharPos(bytesPerLine);
            int headerX = lineNumbersAreaWidth + mainAreaX - scrollPosition.getScrollCharPosition() * characterWidth - scrollPosition.getScrollCharOffset();
            int headerY = lineHeight - subFontSpace;

            int visibleCharStart = (scrollPosition.getScrollCharPosition() * characterWidth + scrollPosition.getScrollCharOffset()) / characterWidth;
            if (visibleCharStart < 0) {
                visibleCharStart = 0;
            }
            int visibleCharEnd = (mainAreaWidth + (scrollPosition.getScrollCharPosition() + charsPerLine) * characterWidth + scrollPosition.getScrollCharOffset()) / characterWidth;
            if (visibleCharEnd > charsPerLine) {
                visibleCharEnd = charsPerLine;
            }
            int visibleStart = computePositionByte(visibleCharStart);
            int visibleEnd = computePositionByte(visibleCharEnd - 1) + 1;

            g.setColor(colors.foreground);
            char[] headerChars = new char[charsPerLine];
            Arrays.fill(headerChars, ' ');

            boolean interleaving = false;
            int lastPos = 0;
            for (int index = visibleStart; index < visibleEnd; index++) {
                int codePos = computeFirstCodeCharPos(index);
                if (codePos == lastPos + 2 && !interleaving) {
                    interleaving = true;
                } else {
                    CodeAreaUtils.longToBaseCode(headerChars, codePos, index, 16, 2, true, hexCharactersCase);
                    lastPos = codePos;
                    interleaving = false;
                }
            }

            int renderOffset = visibleCharStart;
//            ColorsGroup.ColorType renderColorType = null;
            Color renderColor = null;
            for (int characterOnLine = visibleCharStart; characterOnLine < visibleCharEnd; characterOnLine++) {
                int byteOnLine;
                byteOnLine = computePositionByte(characterOnLine);
                boolean sequenceBreak = false;
                boolean nativeWidth = true;

                int currentCharWidth = 0;
//                ColorsGroup.ColorType colorType = ColorsGroup.ColorType.TEXT;
                if (characterRenderingMode != CharacterRenderingMode.LINE_AT_ONCE) {
                    char currentChar = ' ';
//                    if (colorType == ColorsGroup.ColorType.TEXT) {
                    currentChar = headerChars[characterOnLine];
//                    }
                    if (currentChar == ' ' && renderOffset == characterOnLine) {
                        renderOffset++;
                        continue;
                    }
                    if (characterRenderingMode == CharacterRenderingMode.AUTO && monospaceFont) {
                        // Detect if character is in unicode range covered by monospace fonts
                        if (CodeAreaSwingUtils.isMonospaceFullWidthCharater(currentChar)) {
                            currentCharWidth = characterWidth;
                        }
                    }

                    if (currentCharWidth == 0) {
                        currentCharWidth = fontMetrics.charWidth(currentChar);
                        nativeWidth = currentCharWidth == characterWidth;
                    }
                } else {
                    currentCharWidth = characterWidth;
                }

                Color color = Color.BLACK;
//                getHeaderPositionColor(byteOnLine, charOnLine);
//                if (renderColorType == null) {
//                    renderColorType = colorType;
//                    renderColor = color;
//                    g.setColor(color);
//                }

                if (!nativeWidth || !CodeAreaSwingUtils.areSameColors(color, renderColor)) { // || !colorType.equals(renderColorType)
                    sequenceBreak = true;
                }
                if (sequenceBreak) {
                    if (renderOffset < characterOnLine) {
                        g.drawChars(headerChars, renderOffset, characterOnLine - renderOffset, headerX + renderOffset * characterWidth, headerY);
                    }

//                    if (!colorType.equals(renderColorType)) {
//                        renderColorType = colorType;
//                    }
                    if (!CodeAreaSwingUtils.areSameColors(color, renderColor)) {
                        renderColor = color;
                        g.setColor(color);
                    }

                    if (!nativeWidth) {
                        renderOffset = characterOnLine + 1;
                        if (characterRenderingMode == CharacterRenderingMode.TOP_LEFT) {
                            g.drawChars(headerChars, characterOnLine, 1, headerX + characterOnLine * characterWidth, headerY);
                        } else {
                            drawShiftedChar(g, headerChars, characterOnLine, characterWidth, headerX + characterOnLine * characterWidth, headerY, (characterWidth + 1 - currentCharWidth) >> 1);
                        }
                    } else {
                        renderOffset = characterOnLine;
                    }
                }
            }

            if (renderOffset < charsPerLine) {
                g.drawChars(headerChars, renderOffset, charsPerLine - renderOffset, headerX + renderOffset * characterWidth, headerY);
            }
        }

//        int decorationMode = codeArea.getDecorationMode();
//        if ((decorationMode & CodeArea.DECORATION_HEADER_LINE) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(compRect.x, codeRect.y - 1, compRect.x + compRect.width, codeRect.y - 1);
//        }
//        if ((decorationMode & CodeArea.DECORATION_BOX) > 0) {
//            g.setColor(codeArea.getDecorationLineColor());
//            g.drawLine(codeRect.x - 1, codeRect.y - 1, codeRect.x + codeRect.width, codeRect.y - 1);
//        }
//        if ((decorationMode & CodeArea.DECORATION_PREVIEW_LINE) > 0) {
//            int lineX = codeArea.getPreviewX() - scrollPosition.getScrollCharPosition() * codeArea.getCharWidth() - scrollPosition.getScrollCharOffset() - codeArea.getCharWidth() / 2;
//            if (lineX >= codeRect.x) {
//                g.setColor(codeArea.getDecorationLineColor());
//                g.drawLine(lineX, compRect.y, lineX, codeRect.y);
//            }
//        }
        g.setClip(clipBounds);
    }

    public void paintLineNumbers(@Nonnull Graphics g) {
        Rectangle clipBounds = g.getClipBounds();
        Rectangle lineNumbersArea = new Rectangle(0, headerAreaHeight, lineNumbersAreaWidth, areaHeight - headerAreaHeight); // TODO minus scrollbar height
        g.setClip(clipBounds != null ? lineNumbersArea.intersection(clipBounds) : lineNumbersArea);

        g.setColor(colors.background);
        g.fillRect(lineNumbersArea.x, lineNumbersArea.y, lineNumbersArea.width, lineNumbersArea.height);

        int lineNumberLength = lineNumbersLength;

        if (borderPaintMode == BasicBorderPaintMode.STRIPED) {
            long dataPosition = scrollPosition.getScrollLinePosition() * bytesPerLine;
            int stripePositionY = headerAreaHeight + ((scrollPosition.getScrollLinePosition() & 1) > 0 ? 0 : lineHeight);
            g.setColor(colors.stripes);
            for (int line = 0; line <= linesPerRect / 2; line++) {
                if (dataPosition >= dataSize) {
                    break;
                }

                g.fillRect(0, stripePositionY, lineNumbersAreaWidth, lineHeight);
                stripePositionY += lineHeight * 2;
                dataPosition += bytesPerLine * 2;
            }
        }

        long dataPosition = bytesPerLine * scrollPosition.getScrollLinePosition();
        int positionY = headerAreaHeight + lineHeight - subFontSpace - scrollPosition.getScrollLineOffset();
        g.setColor(colors.foreground);
        Rectangle compRect = new Rectangle();
        for (int line = 0; line <= linesPerRect; line++) {
            if (dataPosition >= dataSize) {
                break;
            }

            CodeAreaUtils.longToBaseCode(lineNumberCode, 0, dataPosition < 0 ? 0 : dataPosition, codeType.getBase(), lineNumberLength, true, CodeCharactersCase.UPPER);
            if (characterRenderingMode == CharacterRenderingMode.LINE_AT_ONCE) {
                g.drawChars(lineNumberCode, 0, lineNumberLength, compRect.x, positionY);
            } else {
                for (int digitIndex = 0; digitIndex < lineNumberLength; digitIndex++) {
                    drawCenteredChar(g, lineNumberCode, digitIndex, characterWidth, compRect.x + characterWidth * digitIndex, positionY);
                }
            }

            positionY += lineHeight;
            dataPosition += bytesPerLine;
        }
        g.setClip(clipBounds);
    }

    @Override
    public void paintMainArea(@Nonnull Graphics g) {
        if (!initialized) {
            reset();
        }
        if (font == null) {
            resetFont(g);
        }

        Rectangle clipBounds = g.getClipBounds();
        Rectangle mainArea = new Rectangle(lineNumbersAreaWidth, headerAreaHeight, areaWidth - lineNumbersAreaWidth - 20, areaHeight - headerAreaHeight - 20);
        g.setClip(clipBounds != null ? mainArea.intersection(clipBounds) : mainArea);
        paintBackground(g);
        paintLines(g);
        g.setClip(clipBounds);
        paintCursor(g);

        // TODO: Remove later
        int x = areaWidth - lineNumbersAreaWidth - 220;
        int y = areaHeight - headerAreaHeight - 20;
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 200, 16);
        g.setColor(Color.BLACK);
        char[] headerCode = (String.valueOf(scrollPosition.getScrollCharPosition()) + "+" + String.valueOf(scrollPosition.getScrollCharOffset()) + " : " + String.valueOf(scrollPosition.getScrollLinePosition()) + "+" + String.valueOf(scrollPosition.getScrollLineOffset()) + " P: " + String.valueOf(paintCounter)).toCharArray();
        g.drawChars(headerCode, 0, headerCode.length, x, y + lineHeight);
    }

    /**
     * Paints main area background.
     *
     * @param g graphics
     */
    public void paintBackground(@Nonnull Graphics g) {
        int linePositionX = lineNumbersAreaWidth;
        g.setColor(colors.background);
        if (borderPaintMode != BasicBorderPaintMode.TRANSPARENT) {
            g.fillRect(linePositionX, headerAreaHeight, mainAreaWidth, mainAreaHeight);
        }

        if (borderPaintMode == BasicBorderPaintMode.STRIPED) {
            long dataPosition = scrollPosition.getScrollLinePosition() * bytesPerLine;
            int stripePositionY = headerAreaHeight + (int) ((scrollPosition.getScrollLinePosition() & 1) > 0 ? 0 : lineHeight);
            g.setColor(colors.stripes);
            for (int line = 0; line <= linesPerRect / 2; line++) {
                if (dataPosition >= dataSize) {
                    break;
                }

                g.fillRect(linePositionX, stripePositionY, mainAreaWidth, lineHeight);
                stripePositionY += lineHeight * 2;
                dataPosition += bytesPerLine * 2;
            }
        }
    }

    public void paintLines(@Nonnull Graphics g) {
        Color randomColor = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        g.setColor(randomColor);
        long dataPosition = scrollPosition.getScrollLinePosition() * bytesPerLine + scrollPosition.getLineDataOffset();
        int linePositionX = lineNumbersAreaWidth - scrollPosition.getScrollCharPosition() * characterWidth - scrollPosition.getScrollCharOffset();
        int linePositionY = headerAreaHeight + lineHeight - subFontSpace;
        g.setColor(Color.BLACK);
        for (int line = 0; line <= linesPerRect; line++) {
            prepareLineData(dataPosition);
            paintLineBackground(g, linePositionX, linePositionY);
            paintLineText(g, linePositionX, linePositionY);
//            CodeAreaUtils.longToBaseCode(lineNumberCode, 0, dataPosition < 0 ? 0 : dataPosition, 16, lineNumberLength, true, HexCharactersCase.UPPER);
//            if (characterRenderingMode == CharacterRenderingMode.LINE_AT_ONCE) {
//                g.drawChars(lineNumberCode, 0, lineNumberLength, compRect.x, positionY);
//            } else {
//                for (int digitIndex = 0; digitIndex < lineNumberLength; digitIndex++) {
//                    drawCenteredChar(g, lineNumberCode, digitIndex, characterWidth, compRect.x + characterWidth * digitIndex, positionY);
//                }
//            }

            linePositionY += lineHeight;
            dataPosition += bytesPerLine;
        }
    }

    private void prepareLineData(long dataPosition) {
        int lineBytesLimit = bytesPerLine;
        int lineStart = 0;
        if (dataPosition < dataSize) {
            int lineDataSize = bytesPerLine + maxCharLength - 1;
            if (dataPosition + lineDataSize > dataSize) {
                lineDataSize = (int) (dataSize - dataPosition);
            }
            if (dataPosition < 0) {
                lineStart = (int) -dataPosition;
            }
            worker.getCodeArea().getData().copyToArray(dataPosition + lineStart, lineData, lineStart, lineDataSize - lineStart);
            if (dataPosition + lineBytesLimit > dataSize) {
                lineBytesLimit = (int) (dataSize - dataPosition);
            }
        } else {
            lineBytesLimit = 0;
        }

        // Fill codes
        if (viewMode != CodeAreaViewMode.TEXT_PREVIEW) {
            for (int byteOnLine = Math.max(visibleCodeStart, lineStart); byteOnLine < Math.min(visibleCodeEnd, lineBytesLimit); byteOnLine++) {
                byte dataByte = lineData[byteOnLine];
                CodeAreaUtils.byteToCharsCode(dataByte, codeType, lineCharacters, computeFirstCodeCharPos(byteOnLine), hexCharactersCase);
            }
            if (bytesPerLine > lineBytesLimit) {
                Arrays.fill(lineCharacters, computePositionByte(lineBytesLimit), lineCharacters.length, ' ');
            }
        }

        // Fill preview characters
        if (viewMode != CodeAreaViewMode.CODE_MATRIX) {
            for (int byteOnLine = visiblePreviewStart; byteOnLine < Math.min(visiblePreviewEnd, lineBytesLimit); byteOnLine++) {
                byte dataByte = lineData[byteOnLine];

                if (maxCharLength > 1) {
                    if (dataPosition + maxCharLength > dataSize) {
                        maxCharLength = (int) (dataSize - dataPosition);
                    }

                    int charDataLength = maxCharLength;
                    if (byteOnLine + charDataLength > lineData.length) {
                        charDataLength = lineData.length - byteOnLine;
                    }
                    String displayString = new String(lineData, byteOnLine, charDataLength, charset);
                    if (!displayString.isEmpty()) {
                        lineCharacters[previewCharPos + byteOnLine] = displayString.charAt(0);
                    }
                } else {
                    if (charMappingCharset == null || charMappingCharset != charset) {
                        buildCharMapping(charset);
                    }

                    lineCharacters[previewCharPos + byteOnLine] = charMapping[dataByte & 0xFF];
                }
            }
            if (bytesPerLine > lineBytesLimit) {
                Arrays.fill(lineCharacters, previewCharPos + lineBytesLimit, previewCharPos + bytesPerLine, ' ');
            }
        }
    }

    private void paintLineBackground(@Nonnull Graphics g, int linePositionX, int linePositionY) {
        int renderOffset = visibleCharStart;
        Color renderColor = null;
        for (int charOnLine = visibleCharStart; charOnLine < visibleCharEnd; charOnLine++) {
            CodeAreaSection section;
            int byteOnLine;
            if (charOnLine >= previewCharPos && viewMode != CodeAreaViewMode.CODE_MATRIX) {
                byteOnLine = charOnLine - previewCharPos;
                section = CodeAreaSection.TEXT_PREVIEW;
            } else {
                byteOnLine = computePositionByte(charOnLine);
                section = CodeAreaSection.CODE_MATRIX;
            }
            boolean sequenceBreak = false;

            Color color = null; //Color.getPositionColor(byteOnLine, charOnLine, section, colorType, paintData);
//            if (renderColorType == null) {
//                renderColorType = colorType;
//                renderColor = color;
//                g.setColor(color);
//            }

            if (!CodeAreaSwingUtils.areSameColors(color, renderColor)) {
                sequenceBreak = true;
            }
            if (sequenceBreak) {
                if (renderOffset < charOnLine) {
                    if (renderColor != null) {
                        renderBackgroundSequence(g, renderOffset, charOnLine, linePositionX, linePositionY);
                    }
                }

                if (!CodeAreaSwingUtils.areSameColors(color, renderColor)) {
                    renderColor = color;
                    g.setColor(color);
                }

                renderOffset = charOnLine;
            }
        }

        if (renderOffset < charactersPerLine) {
            if (renderColor != null) {
                renderBackgroundSequence(g, renderOffset, charactersPerLine, linePositionX, linePositionY);
            }
        }
    }

    @Override
    public boolean revealPosition(@Nonnull CaretPosition caretPosition) {
        boolean scrolled = false;
        /*        Rectangle hexRect = getDataViewRectangle();
        int bytesPerRect = getBytesPerRectangle();
        int linesPerRect = getLinesPerRectangle();
        int bytesPerLine = getBytesPerLine();
        long caretLine = position / bytesPerLine;

        int positionByte = painter.computePositionByte((int) (position % bytesPerLine));

        if (caretLine <= scrollPosition.getScrollLinePosition()) {
            scrollPosition.setScrollLinePosition(caretLine);
            scrollPosition.setScrollLineOffset(0);
            scrolled = true;
        } else if (caretLine >= scrollPosition.getScrollLinePosition() + linesPerRect) {
            scrollPosition.setScrollLinePosition(caretLine - linesPerRect);
            if (verticalScrollUnit == VerticalScrollUnit.PIXEL) {
                scrollPosition.setScrollLineOffset(getLineHeight() - (hexRect.height % getLineHeight()));
            } else {
                scrollPosition.setScrollLinePosition(scrollPosition.getScrollLinePosition() + 1);
            }
            scrolled = true;
        }
        if (positionByte <= scrollPosition.getScrollCharPosition()) {
            scrollPosition.setScrollCharPosition(positionByte);
            scrollPosition.setScrollCharOffset(0);
            scrolled = true;
        } else if (positionByte >= scrollPosition.getScrollCharPosition() + bytesPerRect) {
            scrollPosition.setScrollCharPosition(positionByte - bytesPerRect);
            if (horizontalScrollUnit == HorizontalScrollUnit.PIXEL) {
                scrollPosition.setScrollCharOffset(getCharacterWidth() - (hexRect.width % getCharacterWidth()));
            } else {
                scrollPosition.setScrollCharPosition(scrollPosition.getScrollCharPosition() + 1);
            }
            scrolled = true;
        }
         */
        return scrolled;
    }

    private void paintLineText(@Nonnull Graphics g, int linePositionX, int linePositionY) {
        int positionY = linePositionY; // - codeArea.getSubFontSpace();

        g.setColor(Color.BLACK);
//        Rectangle dataViewRectangle = codeArea.getDataViewRectangle();
//        g.drawString("[" + String.valueOf(dataViewRectangle.x) + "," + String.valueOf(dataViewRectangle.y) + "," + String.valueOf(dataViewRectangle.width) + "," + String.valueOf(dataViewRectangle.height) + "]", linePositionX, positionY);

//    public void paintLineText(Graphics g, int linePositionX, int linePositionY, PaintDataCache paintData) {
//        int positionY = linePositionY + paintData.lineHeight - codeArea.getSubFontSpace();
//
        int renderOffset = visibleCharStart;
        for (int charOnLine = visibleCharStart; charOnLine < visibleCharEnd; charOnLine++) {
            CodeAreaSection section;
            int byteOnLine;
            if (charOnLine >= previewCharPos) {
                byteOnLine = charOnLine - previewCharPos;
                section = CodeAreaSection.TEXT_PREVIEW;
            } else {
                byteOnLine = computePositionByte(charOnLine);
                section = CodeAreaSection.CODE_MATRIX;
            }
            boolean sequenceBreak = false;
            boolean nativeWidth = true;

            int currentCharWidth = 0;
            if (characterRenderingMode != CharacterRenderingMode.LINE_AT_ONCE) {
                char currentChar = lineCharacters[charOnLine];
                if (currentChar == ' ' && renderOffset == charOnLine) {
                    renderOffset++;
                    continue;
                }
                if (characterRenderingMode == CharacterRenderingMode.AUTO && monospaceFont) {
                    // Detect if character is in unicode range covered by monospace fonts
                    if (CodeAreaSwingUtils.isMonospaceFullWidthCharater(currentChar)) {
                        currentCharWidth = characterWidth;
                    }
                }

                if (currentCharWidth == 0) {
                    currentCharWidth = fontMetrics.charWidth(currentChar);
                    nativeWidth = currentCharWidth == characterWidth;
                }
            } else {
                currentCharWidth = characterWidth;
            }

            if (!nativeWidth) {
                sequenceBreak = true;
            }
            if (sequenceBreak) {
                if (renderOffset < charOnLine) {
                    renderCharSequence(g, renderOffset, charOnLine, linePositionX, positionY);
                }

                if (!nativeWidth) {
                    renderOffset = charOnLine + 1;
                    if (characterRenderingMode == CharacterRenderingMode.TOP_LEFT) {
                        g.drawChars(lineCharacters, charOnLine, 1, linePositionX + charOnLine * characterWidth, positionY);
                    } else {
                        drawShiftedChar(g, lineCharacters, charOnLine, characterWidth, linePositionX + charOnLine * characterWidth, positionY, (characterWidth + 1 - currentCharWidth) >> 1);
                    }
                } else {
                    renderOffset = charOnLine;
                }
            }
        }

        if (renderOffset < charactersPerLine) {
            renderCharSequence(g, renderOffset, charactersPerLine, linePositionX, positionY);
        }
    }

    @Override
    public void paintCursor(@Nonnull Graphics g) {
        if (!worker.getCodeArea().hasFocus()) {
            return;
        }

        DefaultCodeAreaCaret caret = (DefaultCodeAreaCaret) ((CaretCapable) worker).getCaret();
        Point cursorPoint = getCursorPoint(bytesPerLine, lineHeight, characterWidth, linesPerRect);
        boolean cursorVisible = caret.isCursorVisible();
        DefaultCodeAreaCaret.CursorRenderingMode renderingMode = caret.getRenderingMode();

        /*        if (cursorVisible && cursorPoint != null) {
            cursorPoint.setLocation(cursorPoint.x + lineNumbersAreaWidth, cursorPoint.y + headerAreaHeight);
            g.setColor(colors.cursor);
            if (renderingMode == DefaultCodeAreaCaret.CursorRenderingMode.XOR) {
                g.setXORMode(Color.WHITE);
            }

            DefaultCodeAreaCaret.CursorShape cursorShape = codeArea.getEditationMode() == EditationMode.INSERT ? caret.getInsertCursorShape() : caret.getOverwriteCursorShape();
            int cursorThickness = 0;
            if (cursorShape.getWidth() != DefaultCodeAreaCaret.CursorShapeWidth.FULL) {
                cursorThickness = caret.getCursorThickness(cursorShape, characterWidth, lineHeight);
            }
            switch (cursorShape) {
                case LINE_TOP:
                case DOUBLE_TOP:
                case QUARTER_TOP:
                case HALF_TOP: {
                    paintCursorRect(g, cursorPoint.x, cursorPoint.y,
                            characterWidth, cursorThickness, renderingMode);
                    break;
                }
                case LINE_BOTTOM:
                case DOUBLE_BOTTOM:
                case QUARTER_BOTTOM:
                case HALF_BOTTOM: {
                    paintCursorRect(g, cursorPoint.x, cursorPoint.y + lineHeight - cursorThickness,
                            characterWidth, cursorThickness, renderingMode);
                    break;
                }
                case LINE_LEFT:
                case DOUBLE_LEFT:
                case QUARTER_LEFT:
                case HALF_LEFT: {
                    paintCursorRect(g, cursorPoint.x, cursorPoint.y, cursorThickness, lineHeight, renderingMode);
                    break;
                }
                case LINE_RIGHT:
                case DOUBLE_RIGHT:
                case QUARTER_RIGHT:
                case HALF_RIGHT: {
                    paintCursorRect(g, cursorPoint.x + characterWidth - cursorThickness, cursorPoint.y, cursorThickness, lineHeight, renderingMode);
                    break;
                }
                case BOX: {
                    paintCursorRect(g, cursorPoint.x, cursorPoint.y,
                            characterWidth, lineHeight, renderingMode);
                    break;
                }
                case FRAME: {
                    g.drawRect(cursorPoint.x, cursorPoint.y, characterWidth, lineHeight - 1);
                    break;
                }
                case BOTTOM_CORNERS:
                case CORNERS: {
                    int quarterWidth = characterWidth / 4;
                    int quarterLine = lineHeight / 4;
                    if (cursorShape == DefaultCodeAreaCaret.CursorShape.CORNERS) {
                        g.drawLine(cursorPoint.x, cursorPoint.y,
                                cursorPoint.x + quarterWidth, cursorPoint.y);
                        g.drawLine(cursorPoint.x + characterWidth - quarterWidth, cursorPoint.y,
                                cursorPoint.x + characterWidth, cursorPoint.y);

                        g.drawLine(cursorPoint.x, cursorPoint.y + 1,
                                cursorPoint.x, cursorPoint.y + quarterLine);
                        g.drawLine(cursorPoint.x + characterWidth, cursorPoint.y + 1,
                                cursorPoint.x + characterWidth, cursorPoint.y + quarterLine);
                    }

                    g.drawLine(cursorPoint.x, cursorPoint.y + lineHeight - quarterLine - 1,
                            cursorPoint.x, cursorPoint.y + lineHeight - 2);
                    g.drawLine(cursorPoint.x + characterWidth, cursorPoint.y + lineHeight - quarterLine - 1,
                            cursorPoint.x + characterWidth, cursorPoint.y + lineHeight - 2);

                    g.drawLine(cursorPoint.x, cursorPoint.y + lineHeight - 1,
                            cursorPoint.x + quarterWidth, cursorPoint.y + lineHeight - 1);
                    g.drawLine(cursorPoint.x + characterWidth - quarterWidth, cursorPoint.y + lineHeight - 1,
                            cursorPoint.x + characterWidth, cursorPoint.y + lineHeight - 1);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected cursor shape type " + cursorShape.name());
                }
            }

            if (renderingMode == DefaultCodeAreaCaret.CursorRenderingMode.XOR) {
                g.setPaintMode();
            }
        }

        // Paint shadow cursor
        if (getViewMode() == CodeAreaViewMode.DUAL && showShadowCursor) {
            g.setColor(colors.cursor);
            Point shadowCursorPoint = getShadowCursorPoint(bytesPerLine, lineHeight, characterWidth, linesPerRect);
            shadowCursorPoint.setLocation(shadowCursorPoint.x + lineNumbersAreaWidth, shadowCursorPoint.y + headerAreaHeight);
            Graphics2D g2d = (Graphics2D) g.create();
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
            g2d.setStroke(dashed);
            g2d.drawRect(shadowCursorPoint.x, shadowCursorPoint.y,
                    characterWidth * (getActiveSection() == CodeAreaSection.TEXT_PREVIEW ? codeDigits : 1), lineHeight - 1);
        } */
    }

    private void paintCursorRect(@Nonnull Graphics g, int x, int y, int width, int height, @Nonnull DefaultCodeAreaCaret.CursorRenderingMode renderingMode) {
        /*        switch (renderingMode) {
            case PAINT: {
                g.fillRect(x, y, width, height);
                break;
            }
            case XOR: {
                Rectangle rect = new Rectangle(x, y, width, height);
                Rectangle intersection = rect.intersection(g.getClipBounds());
                if (!intersection.isEmpty()) {
                    g.fillRect(intersection.x, intersection.y, intersection.width, intersection.height);
                }
                break;
            }
            case NEGATIVE: {
                Rectangle rect = new Rectangle(x, y, width, height);
                Rectangle clipBounds = g.getClipBounds();
                Rectangle intersection;
                if (clipBounds != null) {
                    intersection = rect.intersection(clipBounds);
                } else {
                    intersection = rect;
                }
                if (intersection.isEmpty()) {
                    break;
                }
                Shape clip = g.getClip();
                g.setClip(intersection.x, intersection.y, intersection.width, intersection.height);
                g.fillRect(x, y, width, height);
                g.setColor(colors.negativeCursor);
                Rectangle codeRect = getDataViewRect();
                int previewX = getPreviewX();
                int charWidth = getCharacterWidth();
                int lineHeight = getLineHeight();
                int line = (y + scrollPosition.getScrollLineOffset() - codeRect.y) / lineHeight;
                int scrolledX = x + scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset();
                int posY = codeRect.y + (line + 1) * lineHeight - subFontSpace - scrollPosition.getScrollLineOffset();
                if (getViewMode() != CodeAreaViewMode.CODE_MATRIX && scrolledX >= previewX) {
                    int charPos = (scrolledX - previewX) / charWidth;
                    long dataSize = codeArea.getDataSize();
                    long dataPosition = (line + scrollPosition.getScrollLinePosition()) * codeArea.getBytesPerLine() + charPos - scrollPosition.getLineDataOffset();
                    if (dataPosition >= dataSize) {
                        g.setClip(clip);
                        break;
                    }

                    char[] previewChars = new char[1];
                    Charset charset = codeArea.getCharset();
                    CharsetEncoder encoder = charset.newEncoder();
                    int maxCharLength = (int) encoder.maxBytesPerChar();
                    byte[] data = new byte[maxCharLength];

                    if (maxCharLength > 1) {
                        int charDataLength = maxCharLength;
                        if (dataPosition + maxCharLength > dataSize) {
                            charDataLength = (int) (dataSize - dataPosition);
                        }

                        codeArea.getData().copyToArray(dataPosition, data, 0, charDataLength);
                        String displayString = new String(data, 0, charDataLength, charset);
                        if (!displayString.isEmpty()) {
                            previewChars[0] = displayString.charAt(0);
                        }
                    } else {
                        if (charMappingCharset == null || charMappingCharset != charset) {
                            buildCharMapping(charset);
                        }

                        previewChars[0] = charMapping[codeArea.getData().getByte(dataPosition) & 0xFF];
                    }
                    int posX = previewX + charPos * charWidth - scrollPosition.getScrollCharPosition() * charWidth - scrollPosition.getScrollCharOffset();
                    if (characterRenderingMode == CharacterRenderingMode.LINE_AT_ONCE) {
                        g.drawChars(previewChars, 0, 1, posX, posY);
                    } else {
                        drawCenteredChar(g, previewChars, 0, charWidth, posX, posY);
                    }
                } else {
                    int charPos = (scrolledX - codeRect.x) / charWidth;
                    int byteOffset = computePositionByte(charPos);
                    int codeCharPos = computeFirstCodeCharPos(byteOffset);
                    char[] lineChars = new char[getCodeType().getMaxDigitsForByte()];
                    long dataSize = codeArea.getDataSize();
                    long dataPosition = (line + scrollPosition.getScrollLinePosition()) * codeArea.getBytesPerLine() + byteOffset - scrollPosition.getLineDataOffset();
                    if (dataPosition >= dataSize) {
                        g.setClip(clip);
                        break;
                    }

                    byte dataByte = codeArea.getData().getByte(dataPosition);
                    CodeAreaUtils.byteToCharsCode(dataByte, getCodeType(), lineChars, 0, hexCharactersCase);
                    int posX = codeRect.x + codeCharPos * charWidth - scrollPosition.getScrollCharPosition() * charWidth - scrollPosition.getScrollCharOffset();
                    int charsOffset = charPos - codeCharPos;
                    if (characterRenderingMode == CharacterRenderingMode.LINE_AT_ONCE) {
                        g.drawChars(lineChars, charsOffset, 1, posX + (charsOffset * charWidth), posY);
                    } else {
                        drawCenteredChar(g, lineChars, charsOffset, charWidth, posX + (charsOffset * charWidth), posY);
                    }
                }
                g.setClip(clip);
                break;
            }
        } */
    }

    @Nonnull
    public CaretPosition mousePositionToCaretPosition(int mouseX, int mouseY) {
        /*        if (mouseX < hexRect.x) {
            mouseX = hexRect.x;
        }
        int cursorCharX = computeCodeAreaCharacter(mouseX - hexRect.x + scrollPosition.getScrollCharOffset()) + scrollPosition.getScrollCharPosition();
        long cursorLineY = computeCodeAreaLine(mouseY - hexRect.y + scrollPosition.getScrollLineOffset()) + scrollPosition.getScrollLinePosition();
        if (cursorLineY < 0) {
            cursorLineY = 0;
        }
        if (cursorCharX < 0) {
            cursorCharX = 0;
        }

        long dataPosition;
        int codeOffset = 0;
        int byteOnLine;
        if ((viewMode == CodeAreaViewMode.DUAL && cursorCharX < painter.getPreviewFirstChar()) || viewMode == CodeAreaViewMode.CODE_MATRIX) {
            caret.setSection(CodeAreaSection.CODE_MATRIX);
            byteOnLine = painter.computePositionByte(cursorCharX);
            if (byteOnLine >= bytesPerLine) {
                codeOffset = 0;
            } else {
                codeOffset = cursorCharX - painter.computeFirstCodeCharPos(byteOnLine);
                if (codeOffset >= codeType.getMaxDigitsForByte()) {
                    codeOffset = codeType.getMaxDigitsForByte() - 1;
                }
            }
        } else {
            caret.setSection(CodeAreaSection.TEXT_PREVIEW);
            byteOnLine = cursorCharX;
            if (viewMode == CodeAreaViewMode.DUAL) {
                byteOnLine -= painter.getPreviewFirstChar();
            }
        }

        if (byteOnLine >= bytesPerLine) {
            byteOnLine = bytesPerLine - 1;
        }

        dataPosition = byteOnLine + (cursorLineY * bytesPerLine) - scrollPosition.getLineDataOffset();
        if (dataPosition < 0) {
            dataPosition = 0;
            codeOffset = 0;
        }

        long dataSize = codeArea.getDataSize();
        if (dataPosition >= dataSize) {
            dataPosition = dataSize;
            codeOffset = 0;
        }

        CaretPosition caretPosition = caret.getCaretPosition();
        caret.setCaretPosition(dataPosition, codeOffset);

        return caretPosition; */
        return new CaretPosition();
    }

    /**
     * Returns relative cursor position in code area or null if cursor is not
     * visible.
     *
     * @param bytesPerLine bytes per line
     * @param lineHeight line height
     * @param charWidth character width
     * @param linesPerRect lines per visible rectangle
     * @return cursor position or null
     */
    @Nullable
    public Point getCursorPoint(int bytesPerLine, int lineHeight, int charWidth, int linesPerRect) {
        /*        CaretPosition caretPosition = getCaretPosition();
        long shiftedPosition = caretPosition.getDataPosition() + scrollPosition.getLineDataOffset();
        long line = shiftedPosition / bytesPerLine - scrollPosition.getScrollLinePosition();
        if (line < -1 || line > linesPerRect) {
            return null;
        }

        int byteOffset = (int) (shiftedPosition % bytesPerLine);

        Rectangle dataViewRect = getDataViewRectangle();
        int caretY = (int) (dataViewRect.y + line * lineHeight) - scrollPosition.getScrollLineOffset();
        int caretX;
        if (caretPosition.getSection() == CodeAreaSection.TEXT_PREVIEW) {
            caretX = codeArea.getPreviewX() + charWidth * byteOffset;
        } else {
            caretX = dataViewRect.x + charWidth * (computeFirstCodeCharPos(byteOffset) + caretPosition.getCodeOffset());
        }
        caretX -= scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset();

        return new Point(caretX, caretY); */
        return new Point();
    }

    /**
     * Returns relative shadow cursor position in code area or null if cursor is
     * not visible.
     *
     * @param bytesPerLine bytes per line
     * @param lineHeight line height
     * @param charWidth character width
     * @param linesPerRect lines per visible rectangle
     * @return cursor position or null
     */
    @Nullable
    public Point getShadowCursorPoint(int bytesPerLine, int lineHeight, int charWidth, int linesPerRect) {
        /*        CaretPosition caretPosition = getCaretPosition();
        long shiftedPosition = caretPosition.getDataPosition() + scrollPosition.getLineDataOffset();
        long line = shiftedPosition / bytesPerLine - scrollPosition.getScrollLinePosition();
        if (line < -1 || line + 1 > linesPerRect) {
            return null;
        }

        int byteOffset = (int) (shiftedPosition % bytesPerLine);

        Rectangle dataViewRect = getDataViewRectangle();
        int caretY = (int) (dataViewRect.y + line * lineHeight) - scrollPosition.getScrollLineOffset();
        int caretX;
        if (caretPosition.getSection() == CodeAreaSection.TEXT_PREVIEW) {
            caretX = dataViewRect.x + charWidth * computeFirstCodeCharPos(byteOffset);
        } else {
            caretX = codeArea.getPreviewX() + charWidth * byteOffset;
        }
        caretX -= scrollPosition.getScrollCharPosition() * charWidth + scrollPosition.getScrollCharOffset();

        return new Point(caretX, caretY); */
        return new Point();
    }

    @Override
    public int getCursorShape(int x, int y) {
        Rectangle dataViewRectangle = getDataViewRectangle();
        if (x >= dataViewRectangle.x && y >= dataViewRectangle.y) {
            return Cursor.TEXT_CURSOR;
        }

        return Cursor.DEFAULT_CURSOR;
    }

    @Nonnull
    public Rectangle getDataViewRectangle() {
        return dataView.getBounds();
    }

    public int computePositionByte(int lineCharPosition) {
        return lineCharPosition / (codeType.getMaxDigitsForByte() + 1);
    }

    public int computeCodeAreaCharacter(int pixelX) {
        return pixelX / characterWidth;
    }

    public int computeCodeAreaLine(int pixelY) {
        return pixelY / lineHeight;
    }

    public int computeFirstCodeCharPos(int byteOffset) {
        return byteOffset * (codeType.getMaxDigitsForByte() + 1);
    }

    public int computeLastCodeCharPos(int byteOffset) {
        return computeFirstCodeCharPos(byteOffset + 1) - 2;
    }

    public long cursorPositionToDataPosition(long line, int byteOffset) throws OutOfBoundsException {
        return 16;
    }

    /**
     * Draws char in array centering it in precomputed space.
     *
     * @param g graphics
     * @param drawnChars array of chars
     * @param charOffset index of target character in array
     * @param charWidthSpace default character width
     * @param startX X position of drawing area start
     * @param positionY Y position of drawing area start
     */
    protected void drawCenteredChar(@Nonnull Graphics g, char[] drawnChars, int charOffset, int charWidthSpace, int startX, int positionY) {
        int charWidth = fontMetrics.charWidth(drawnChars[charOffset]);
        drawShiftedChar(g, drawnChars, charOffset, charWidthSpace, startX, positionY, (charWidthSpace + 1 - charWidth) >> 1);
    }

    protected void drawShiftedChar(@Nonnull Graphics g, char[] drawnChars, int charOffset, int charWidthSpace, int startX, int positionY, int shift) {
        g.drawChars(drawnChars, charOffset, 1, startX + shift, positionY);
    }

    private void buildCharMapping(@Nonnull Charset charset) {
        for (int i = 0; i < 256; i++) {
            charMapping[i] = new String(new byte[]{(byte) i}, charset).charAt(0);
        }
        charMappingCharset = charset;
    }

    private int getLineNumberLength() {
        return 8;
    }

    /**
     * Returns cursor rectangle.
     *
     * @param bytesPerLine bytes per line
     * @param lineHeight line height
     * @param charWidth character width
     * @param linesPerRect lines per visible rectangle
     * @return cursor rectangle or null
     */
    @Nullable
    public Rectangle getCursorRect(int bytesPerLine, int lineHeight, int charWidth, int linesPerRect) {
        /*        CodeAreaPainter painter = codeArea.getPainter();
        Point cursorPoint = painter.getCursorPoint(bytesPerLine, lineHeight, charWidth, linesPerRect);
        if (cursorPoint == null) {
            return null;
        }

        DefaultCodeAreaCaret.CursorShape cursorShape = codeArea.getEditationMode() == EditationMode.INSERT ? insertCursorShape : overwriteCursorShape;
        int cursorThickness = 0;
        if (cursorShape.getWidth() != DefaultCodeAreaCaret.CursorShapeWidth.FULL) {
            cursorThickness = getCursorThickness(cursorShape, charWidth, lineHeight);
        }
        switch (cursorShape) {
            case BOX:
            case FRAME:
            case BOTTOM_CORNERS:
            case CORNERS: {
                int width = charWidth;
                if (cursorShape != DefaultCodeAreaCaret.CursorShape.BOX) {
                    width++;
                }
                return new Rectangle(cursorPoint.x, cursorPoint.y, width, lineHeight);
            }
            case LINE_TOP:
            case DOUBLE_TOP:
            case QUARTER_TOP:
            case HALF_TOP: {
                return new Rectangle(cursorPoint.x, cursorPoint.y,
                        charWidth, cursorThickness);
            }
            case LINE_BOTTOM:
            case DOUBLE_BOTTOM:
            case QUARTER_BOTTOM:
            case HALF_BOTTOM: {
                return new Rectangle(cursorPoint.x, cursorPoint.y + lineHeight - cursorThickness,
                        charWidth, cursorThickness);
            }
            case LINE_LEFT:
            case DOUBLE_LEFT:
            case QUARTER_LEFT:
            case HALF_LEFT: {
                return new Rectangle(cursorPoint.x, cursorPoint.y, cursorThickness, lineHeight);
            }
            case LINE_RIGHT:
            case DOUBLE_RIGHT:
            case QUARTER_RIGHT:
            case HALF_RIGHT: {
                return new Rectangle(cursorPoint.x + charWidth - cursorThickness, cursorPoint.y, cursorThickness, lineHeight);
            }
            default: {
                throw new IllegalStateException("Unexpected cursor shape type " + cursorShape.name());
            }
        } */
        return new Rectangle();
    }

    /**
     * Render sequence of characters.
     *
     * Doesn't include character at offset end.
     */
    private void renderCharSequence(Graphics g, int startOffset, int endOffset, int linePositionX, int positionY) {
        g.drawChars(lineCharacters, startOffset, endOffset - startOffset, linePositionX + startOffset * characterWidth, positionY);
    }

    /**
     * Render sequence of background rectangles.
     *
     * Doesn't include character at offset end.
     */
    private void renderBackgroundSequence(Graphics g, int startOffset, int endOffset, int linePositionX, int positionY) {
        g.fillRect(linePositionX + startOffset * characterWidth, positionY, (endOffset - startOffset) * characterWidth, lineHeight);
    }

    private int computeLinesPerRectangle() {
        return 30;
    }

    private int computeBytesPerLine() {
        boolean lineWrapping = ((LineWrappingCapable) worker).isLineWrapping();
        int maxBytesPerLine = ((LineWrappingCapable) worker).getMaxBytesPerLine();

        int computedBytesPerLine = 16;
        if (lineWrapping) {

        }

        return computedBytesPerLine;
    }

    @Override
    public void updateScrollBars() {
        if (scrollPosition.getVerticalOverflowMode() == CodeAreaScrollPosition.VerticalOverflowMode.OVERFLOW) {
            long lines = ((dataSize + scrollPosition.getLineDataOffset()) / bytesPerLine) + 1;
            int scrollValue;
            if (scrollPosition.getScrollCharPosition() < Long.MAX_VALUE / Integer.MAX_VALUE) {
                scrollValue = (int) ((scrollPosition.getScrollLinePosition() * Integer.MAX_VALUE) / lines);
            } else {
                scrollValue = (int) (scrollPosition.getScrollLinePosition() / (lines / Integer.MAX_VALUE));
            }
            scrollPanel.getVerticalScrollBar().setValue(scrollValue);
        } else if (verticalScrollUnit == VerticalScrollUnit.LINE) {
            scrollPanel.getVerticalScrollBar().setValue((int) scrollPosition.getScrollLinePosition());
        } else {
            scrollPanel.getVerticalScrollBar().setValue((int) (scrollPosition.getScrollLinePosition() * lineHeight + scrollPosition.getScrollLineOffset()));
        }

        if (horizontalScrollUnit == HorizontalScrollUnit.CHARACTER) {
            scrollPanel.getHorizontalScrollBar().setValue(scrollPosition.getScrollCharPosition());
        } else {
            scrollPanel.getHorizontalScrollBar().setValue(scrollPosition.getScrollCharPosition() * characterWidth + scrollPosition.getScrollCharOffset());
        }
    }

    private class VerticalAdjustmentListener implements AdjustmentListener {

        public VerticalAdjustmentListener() {
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            int scrollBarValue = scrollPanel.getVerticalScrollBar().getValue();
            if (scrollPosition.getVerticalOverflowMode() == CodeAreaScrollPosition.VerticalOverflowMode.OVERFLOW) {
                int maxValue = Integer.MAX_VALUE - scrollPanel.getVerticalScrollBar().getVisibleAmount();
                long lines = ((dataSize + scrollPosition.getLineDataOffset()) / bytesPerLine) - computeLinesPerRectangle() + 1;
                long targetLine;
                if (scrollBarValue > 0 && lines > maxValue / scrollBarValue) {
                    targetLine = scrollBarValue * (lines / maxValue);
                    long rest = lines % maxValue;
                    targetLine += (rest * scrollBarValue) / maxValue;
                } else {
                    targetLine = (scrollBarValue * lines) / Integer.MAX_VALUE;
                }
                scrollPosition.setScrollLinePosition(targetLine);
                if (verticalScrollUnit != VerticalScrollUnit.LINE) {
                    scrollPosition.setScrollLineOffset(0);
                }
            } else if (verticalScrollUnit == VerticalScrollUnit.LINE) {
                scrollPosition.setScrollLinePosition(scrollBarValue);
            } else {
                scrollPosition.setScrollLinePosition(scrollBarValue / lineHeight);
                scrollPosition.setScrollLineOffset(scrollBarValue % lineHeight);
            }

            // TODO
            notifyScrolled();
//            repaint();
//            dataViewScrolled(codeArea.getGraphics());
        }
    }

    private class HorizontalAdjustmentListener implements AdjustmentListener {

        public HorizontalAdjustmentListener() {
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (horizontalScrollUnit == HorizontalScrollUnit.CHARACTER) {
                scrollPosition.setScrollCharPosition(scrollPanel.getHorizontalScrollBar().getValue());
            } else {
                if (characterWidth > 0) {
                    int horizontalScroll = scrollPanel.getHorizontalScrollBar().getValue();
                    scrollPosition.setScrollCharPosition(horizontalScroll / characterWidth);
                    scrollPosition.setScrollCharOffset(horizontalScroll % characterWidth);
                }
            }

            //          repaint();
//            dataViewScrolled(codeArea.getGraphics());
            notifyScrolled();
        }
    }

    private void notifyScrolled() {
        // TODO
    }

    private static class Colors {

        Color foreground;
        Color background;
        Color selectionForeground;
        Color selectionBackground;
        Color selectionMirrorForeground;
        Color selectionMirrorBackground;
        Color cursor;
        Color negativeCursor;
        Color cursorMirror;
        Color negativeCursorMirror;
        Color decorationLine;
        Color stripes;
    }
}
