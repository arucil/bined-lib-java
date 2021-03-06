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
package org.exbin.bined.javafx;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.exbin.bined.CaretMovedListener;
import org.exbin.bined.CaretPosition;
import org.exbin.bined.CodeAreaLineNumberLength;
import org.exbin.bined.CodeType;
import org.exbin.bined.DataChangedListener;
import org.exbin.bined.EditationAllowed;
import org.exbin.bined.EditationMode;
import org.exbin.bined.EditationModeChangedListener;
import org.exbin.bined.HexCharactersCase;
import org.exbin.bined.PositionCodeType;
import org.exbin.bined.ScrollBarVisibility;
import org.exbin.bined.ScrollingListener;
import org.exbin.bined.Section;
import org.exbin.bined.SelectionChangedListener;
import org.exbin.bined.SelectionRange;
import org.exbin.bined.ViewMode;
import org.exbin.utils.binary_data.BinaryData;

/**
 * Hexadecimal viewer/editor JavaFX component.
 *
 * Also supports binary, octal and decimal codes.
 *
 * @version 0.1.1 2016/10/05
 * @author ExBin Project (http://exbin.org)
 */
public class CodeAreaFX extends Control {

    public static final int NO_MODIFIER = 0;
    public static final int DECORATION_HEADER_LINE = 1;
    public static final int DECORATION_LINENUM_LINE = 2;
    public static final int DECORATION_PREVIEW_LINE = 4;
    public static final int DECORATION_BOX = 8;
    public static final int DECORATION_DEFAULT = DECORATION_PREVIEW_LINE | DECORATION_LINENUM_LINE | DECORATION_HEADER_LINE;
    public static final int MOUSE_SCROLL_LINES = 3;

    private BinaryData data;
    private Font font;
    private final MainAreaControl mainArea = new MainAreaControl();
    private final HeaderControl headerArea = new HeaderControl();
    private final LineNumbersControl lineNumbersArea = new LineNumbersControl();
    private CodeAreaPainter painter;
    private CodeAreaCommandHandler commandHandler;
    private final CodeAreaCaret caret;
    private SelectionRange selection;

    private ViewMode viewMode = ViewMode.DUAL;
    private CodeType codeType = CodeType.HEXADECIMAL;
    private PositionCodeType positionCodeType = PositionCodeType.HEXADECIMAL;
    private BackgroundMode backgroundMode = BackgroundMode.STRIPPED;
    private boolean lineNumberBackground = true;
    private Charset charset = Charset.defaultCharset();
    private int decorationMode = DECORATION_DEFAULT;
    private EditationAllowed editationAllowed = EditationAllowed.ALLOWED;
    private EditationMode editationMode = EditationMode.OVERWRITE;
    private CharRenderingMode charRenderingMode = CharRenderingMode.AUTO;
    private CharAntialiasingMode charAntialiasingMode = CharAntialiasingMode.AUTO;
    private HexCharactersCase hexCharactersCase = HexCharactersCase.UPPER;
    private final CodeAreaSpace headerSpace = new CodeAreaSpace(CodeAreaSpace.SpaceType.HALF_UNIT);
    private final CodeAreaSpace lineNumberSpace = new CodeAreaSpace();
    private final CodeAreaLineNumberLength lineNumberLength = new CodeAreaLineNumberLength();

    private int lineLength = 16;
    private int byteGroupSize = 1;
    private int spaceGroupSize = 0;
    private int subFontSpace = 3;
    private boolean showHeader = true;
    private boolean showLineNumbers = true;
    private boolean mouseDown;
    private boolean wrapMode = false;
    private boolean handleClipboard = true;
    private boolean showUnprintableCharacters = false;
    private boolean showShadowCursor = true;

    private ScrollBarVisibility verticalScrollBarVisibility = ScrollBarVisibility.IF_NEEDED;
    private VerticalScrollMode verticalScrollMode = VerticalScrollMode.PER_LINE;
    private ScrollBarVisibility horizontalScrollBarVisibility = ScrollBarVisibility.IF_NEEDED;
    private HorizontalScrollMode horizontalScrollMode = HorizontalScrollMode.PIXEL;
    private ScrollBar horizontalScrollBar;
    private ScrollBar verticalScrollBar;
    private final ScrollPosition scrollPosition = new ScrollPosition();

    /**
     * Component colors.
     *
     * Parent foreground and background are used for header and line numbers
     * section.
     */
    private Color textColor;
    private Color backgroundColor;
    private final ColorsGroup mainColors = new ColorsGroup();
    private final ColorsGroup alternateColors = new ColorsGroup();
    private final ColorsGroup selectionColors = new ColorsGroup();
    private final ColorsGroup mirrorSelectionColors = new ColorsGroup();
    private Color cursorColor;
    private Color negativeCursorColor;
    private Color decorationLineColor;

    /**
     * Listeners.
     */
    private final List<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final List<CaretMovedListener> caretMovedListeners = new ArrayList<>();
    private final List<EditationModeChangedListener> editationModeChangedListeners = new ArrayList<>();
    private final List<DataChangedListener> dataChangedListeners = new ArrayList<>();
    private final List<ScrollingListener> scrollingListeners = new ArrayList<>();

    private final PaintDataCache paintDataCache = new PaintDataCache();

    public CodeAreaFX() {
        super();

        caret = new CodeAreaCaret(this);
        painter = new DefaultCodeAreaPainter(this);
        commandHandler = new DefaultCodeAreaCommandHandler(this);
        this.font = new Font("MONOSPACED", 10);

        setSkin(new CodeAreaFXSkin());
        init();
    }

    private void init() {
        textColor = Color.BLACK;
        backgroundColor = Color.WHITE;
        Color unprintablesColor = new Color(textColor.getRed(), (textColor.getGreen() + 0.5) % 1, textColor.getBlue(), textColor.getOpacity());
        mainColors.setTextColor(textColor);
        mainColors.setBothBackgroundColors(backgroundColor);
        mainColors.setUnprintablesColor(unprintablesColor);
        alternateColors.setTextColor(textColor);
        alternateColors.setBothBackgroundColors(createOddColor(backgroundColor));
        alternateColors.setUnprintablesColor(unprintablesColor);
        Color selectionTextColor = Color.WHITE;
        Color selectionBackgroundColor = new Color(96 / 256f, 96 / 256f, 255 / 256f, 1);
        selectionColors.setTextColor(selectionTextColor);
        selectionColors.setBothBackgroundColors(selectionBackgroundColor);
        selectionColors.setUnprintablesColor(unprintablesColor);
        mirrorSelectionColors.setTextColor(selectionTextColor);
        double grayLevel = (selectionBackgroundColor.getRed() + selectionBackgroundColor.getGreen() + selectionBackgroundColor.getBlue()) / 3;
        mirrorSelectionColors.setBothBackgroundColors(new Color(grayLevel, grayLevel, grayLevel, 1));
        mirrorSelectionColors.setUnprintablesColor(unprintablesColor);

        cursorColor = Color.BLACK;
        negativeCursorColor = createNegativeColor(cursorColor);
        decorationLineColor = Color.GRAY;

        verticalScrollBar = new ScrollBar();
        verticalScrollBar.setOrientation(Orientation.VERTICAL);
//        verticalScrollBar.addAdjustmentListener(new VerticalAdjustmentListener());
//        add(verticalScrollBar);
        horizontalScrollBar = new ScrollBar();
        horizontalScrollBar.setOrientation(Orientation.HORIZONTAL);
//        horizontalScrollBar.addAdjustmentListener(new HorizontalAdjustmentListener());
//        add(horizontalScrollBar);

//        setFocusable(true);
//        setFocusTraversalKeysEnabled(false);
//        addComponentListener(new CodeAreaComponentListener());
//
//        CodeAreaMouseListener codeAreaMouseListener = new CodeAreaMouseListener();
//        addMouseListener(codeAreaMouseListener);
//        addMouseMotionListener(codeAreaMouseListener);
//        addMouseWheelListener(codeAreaMouseListener);
//        addKeyListener(new CodeAreaKeyListener());
        focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            repaint();
        });
    }

    public class CodeAreaFXSkin implements Skin<CodeAreaFX> {

        private Group rootNode;

        public CodeAreaFXSkin() {
        }

        protected void redraw() {
            List<Node> nodes = new ArrayList<>();

            Insets insets = getInsets();
            double width = getWidth();
            double height = getHeight();
            Rectangle compRect = new Rectangle(
                    (int) insets.getLeft(),
                    (int) insets.getTop(),
                    (int) (width - insets.getLeft() - insets.getRight()),
                    (int) (height - insets.getTop() - insets.getBottom()));

            if (!paintDataCache.componentRectangle.equals(compRect)) {
                computePaintData();
            }

//          // TODO Antialiasing not available?
//            if (charAntialiasingMode != CharAntialiasingMode.OFF && g instanceof Graphics2D) {
//                Object antialiasingHint = getAntialiasingHint((Graphics2D) g);
//                ((Graphics2D) g).setRenderingHint(
//                        RenderingHints.KEY_TEXT_ANTIALIASING,
//                        antialiasingHint);
//            }
//            text.setFontSmoothingType(FontSmoothingType.LCD);
            if (paintDataCache.fontMetrics == null) {
                computeFontMetrics();
            }

            Node overall = painter.paintOverall();
            nodes.add(overall);
            Text text = new Text("TEST");
            nodes.add(text);

            Rectangle hexRect = paintDataCache.codeSectionRectangle;
            if (showHeader) {
                headerArea.setLayoutX(hexRect.x);
                headerArea.setLayoutY(hexRect.y);
                headerArea.setMaxSize(hexRect.width, 20); // TODO
                nodes.add(headerArea);
            }

            Node background = painter.paintBackground();
            if (background != null) {
                nodes.add(background);
            }

            if (showLineNumbers) {
                lineNumbersArea.setLayoutX(hexRect.x);
                lineNumbersArea.setLayoutY(hexRect.y);
                lineNumbersArea.setMaxSize(hexRect.width, 20); // TODO
                nodes.add(lineNumbersArea);
            }
//
//        painter.paintMainArea(g);
//        painter.paintCursor(g);
//
//        g.setClip(clipBounds);
            this.rootNode.getChildren().setAll(nodes);
        }

        @Override
        public CodeAreaFX getSkinnable() {
            return CodeAreaFX.this;
        }

        @Override
        public Node getNode() {
            if (this.rootNode == null) {
                this.rootNode = new Group();
                redraw();
            }
            return this.rootNode;
        }

        @Override
        public void dispose() {
        }
    }

    /**
     * Hexadecimal viewer/editor JavaFX main area control.
     */
    public class MainAreaControl extends Control {

        public MainAreaControl() {
            super();
            setSkin(new MainAreaSkin());
        }

        public class MainAreaSkin implements Skin<MainAreaControl> {

            private Group rootNode;

            public MainAreaSkin() {
            }

            protected void redraw() {
                if (paintDataCache == null) {
                    return;
                }

                List<Node> nodes = new ArrayList<>();

                Insets insets = getInsets();
                double width = getWidth();
                double height = getHeight();
                Rectangle compRect = new Rectangle(
                        (int) insets.getLeft(),
                        (int) insets.getTop(),
                        (int) (width - insets.getLeft() - insets.getRight()),
                        (int) (height - insets.getTop() - insets.getBottom()));

                if (!paintDataCache.componentRectangle.equals(compRect)) {
                    computePaintData();
                }

//            text.setFontSmoothingType(FontSmoothingType.LCD);
                if (paintDataCache.fontMetrics == null) {
                    computeFontMetrics();
                }

                Node overall = painter.paintOverall();
                nodes.add(overall);
                Text text = new Text("TEST");
                nodes.add(text);

//        Rectangle hexRect = paintDataCache.codeSectionRectangle;
//        if (showHeader) {
//            g.setClip(clipBounds.createIntersection(new Rectangle(hexRect.x, 0, hexRect.width, hexRect.y)));
//            painter.paintHeader(g);
//        }
//
//        g.setClip(clipBounds.createIntersection(new Rectangle(0, hexRect.y, hexRect.x + hexRect.width, hexRect.height)));
//        painter.paintBackground(g);
//        if (showLineNumbers) {
//            painter.paintLineNumbers(g);
//            g.setClip(clipBounds.createIntersection(new Rectangle(hexRect.x, hexRect.y, hexRect.width, hexRect.height)));
//        }
//
//        painter.paintMainArea(g);
//        painter.paintCursor(g);
//
//        g.setClip(clipBounds);
                this.rootNode.getChildren().setAll(nodes);
            }

            @Override
            public MainAreaControl getSkinnable() {
                return MainAreaControl.this;
            }

            @Override
            public Node getNode() {
                if (this.rootNode == null) {
                    this.rootNode = new Group();
                    redraw();
                }
                return this.rootNode;
            }

            @Override
            public void dispose() {
            }
        }
    }

    /**
     * Header control.
     */
    public class HeaderControl extends Control {

        public HeaderControl() {
            super();
            setSkin(new HeaderSkin());
        }

        public class HeaderSkin implements Skin<HeaderControl> {

            private Group rootNode;

            public HeaderSkin() {
            }

            protected void redraw() {
                if (paintDataCache == null) {
                    return;
                }

                List<Node> nodes = new ArrayList<>();

                Node header = painter.paintHeader();
                if (header != null) {
                    nodes.add(header);
                }

                this.rootNode.getChildren().setAll(nodes);
            }

            @Override
            public HeaderControl getSkinnable() {
                return HeaderControl.this;
            }

            @Override
            public Node getNode() {
                if (this.rootNode == null) {
                    this.rootNode = new Group();
                    redraw();
                }
                return this.rootNode;
            }

            @Override
            public void dispose() {
            }
        }
    }

    /**
     * Line numbers control.
     */
    public class LineNumbersControl extends Control {

        public LineNumbersControl() {
            super();
            setSkin(new LineNumbersSkin());
        }

        public class LineNumbersSkin implements Skin<LineNumbersControl> {

            private Group rootNode;

            public LineNumbersSkin() {
            }

            protected void redraw() {
                if (paintDataCache == null) {
                    return;
                }

                List<Node> nodes = new ArrayList<>();

                Node lineNumbers = painter.paintLineNumbers();
                if (lineNumbers != null) {
                    nodes.add(lineNumbers);
                }
                this.rootNode.getChildren().setAll(nodes);
            }

            @Override
            public LineNumbersControl getSkinnable() {
                return LineNumbersControl.this;
            }

            @Override
            public Node getNode() {
                if (this.rootNode == null) {
                    this.rootNode = new Group();
                    redraw();
                }
                return this.rootNode;
            }

            @Override
            public void dispose() {
            }
        }
    }

    public void repaint() {
        ((CodeAreaFXSkin) getSkin()).redraw();
    }

    private Object getAntialiasingHint(Graphics2D g) {
        Object antialiasingHint;
        switch (charAntialiasingMode) {
            case AUTO: {
                // TODO detect if display is LCD?
                if (g.getDeviceConfiguration().getDevice().getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                    antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
                } else {
                    antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
                }
                break;
            }
            case BASIC: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
                break;
            }
            case GASP: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
                break;
            }
            case DEFAULT: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
                break;
            }
            case LCD_HRGB: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
                break;
            }
            case LCD_HBGR: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
                break;
            }
            case LCD_VRGB: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
                break;
            }
            case LCD_VBGR: {
                antialiasingHint = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected antialiasing type " + charAntialiasingMode.name());
            }
        }

        return antialiasingHint;
    }

    public CodeAreaCaret getCaret() {
        return caret;
    }

    private void moveCaret(MouseEvent me, int modifiers) {
        Rectangle hexRect = paintDataCache.codeSectionRectangle;
        int bytesPerLine = paintDataCache.bytesPerLine;
        int cursorCharX = (me.getX() - hexRect.x + scrollPosition.scrollCharOffset) / paintDataCache.charWidth + scrollPosition.scrollCharPosition;
        long cursorLineY = (me.getY() - hexRect.y + scrollPosition.scrollLineOffset) / paintDataCache.lineHeight + scrollPosition.scrollLinePosition;
        if (cursorLineY < 0) {
            cursorLineY = 0;
        }
        if (cursorCharX < 0) {
            cursorCharX = 0;
        }

        long dataPosition;
        int codeOffset = 0;
        int byteOnLine;
        if ((viewMode == ViewMode.DUAL && cursorCharX < paintDataCache.previewStartChar) || viewMode == ViewMode.CODE_MATRIX) {
            caret.setSection(Section.CODE_MATRIX);
            byteOnLine = computeByteOffsetPerCodeCharOffset(cursorCharX, false);
            if (byteOnLine >= bytesPerLine) {
                codeOffset = 0;
            } else {
                codeOffset = cursorCharX - computeByteCharPos(byteOnLine);
                if (codeOffset >= codeType.getMaxDigits()) {
                    codeOffset = codeType.getMaxDigits() - 1;
                }
            }
        } else {
            caret.setSection(Section.TEXT_PREVIEW);
            byteOnLine = cursorCharX;
            if (viewMode == ViewMode.DUAL) {
                byteOnLine -= paintDataCache.previewStartChar;
            }
        }

        if (byteOnLine >= bytesPerLine) {
            byteOnLine = bytesPerLine - 1;
        }

        dataPosition = byteOnLine + (cursorLineY * bytesPerLine) - scrollPosition.lineByteShift;
        if (dataPosition < 0) {
            dataPosition = 0;
            codeOffset = 0;
        }

        long dataSize = data.getDataSize();
        if (dataPosition >= dataSize) {
            dataPosition = dataSize;
            codeOffset = 0;
        }

        CaretPosition caretPosition = caret.getCaretPosition();
        caret.setCaretPosition(dataPosition, codeOffset);
        notifyCaretMoved();
        commandHandler.caretMoved();

        updateSelection(modifiers, caretPosition);
    }

    public void notifyCaretMoved() {
        caretMovedListeners.stream().forEach((caretMovedListener) -> {
            caretMovedListener.caretMoved(caret.getCaretPosition(), caret.getSection());
        });
    }

    public void notifyScrolled() {
        scrollingListeners.stream().forEach((scrollingListener) -> {
            scrollingListener.scrolled();
        });
    }

    public void notifyDataChanged() {
        if (caret.getDataPosition() > data.getDataSize()) {
            caret.setCaretPosition(0);
            notifyCaretMoved();
        }

        dataChangedListeners.stream().forEach((dataChangedListener) -> {
            dataChangedListener.dataChanged();
        });
    }

    public ScrollPosition getScrollPosition() {
        return scrollPosition;
    }

    public void revealCursor() {
        revealPosition(caret.getCaretPosition().getDataPosition(), caret.getSection());
    }

    public void revealPosition(long position, Section section) {
        if (paintDataCache.fontMetrics == null) {
            // Ignore if no font data is available
            return;
        }
        boolean scrolled = false;
        Rectangle hexRect = paintDataCache.codeSectionRectangle;
        long caretLine = position / paintDataCache.bytesPerLine;

        int positionByte;
        if (section == Section.CODE_MATRIX) {
            positionByte = computeByteCharPos((int) (position % paintDataCache.bytesPerLine)) + caret.getCodeOffset();
        } else {
            positionByte = (int) (position % paintDataCache.bytesPerLine);
            if (viewMode == ViewMode.DUAL) {
                positionByte += paintDataCache.previewStartChar;
            }
        }

        if (caretLine <= scrollPosition.scrollLinePosition) {
            scrollPosition.scrollLinePosition = caretLine;
            scrollPosition.scrollLineOffset = 0;
            scrolled = true;
        } else if (caretLine >= scrollPosition.scrollLinePosition + paintDataCache.linesPerRect) {
            scrollPosition.scrollLinePosition = caretLine - paintDataCache.linesPerRect;
            if (verticalScrollMode == VerticalScrollMode.PIXEL) {
                scrollPosition.scrollLineOffset = paintDataCache.lineHeight - (hexRect.height % paintDataCache.lineHeight);
            } else {
                scrollPosition.scrollLinePosition++;
            }
            scrolled = true;
        }
        if (positionByte <= scrollPosition.scrollCharPosition) {
            scrollPosition.scrollCharPosition = positionByte;
            scrollPosition.scrollCharOffset = 0;
            scrolled = true;
        } else if (positionByte >= scrollPosition.scrollCharPosition + paintDataCache.bytesPerRect) {
            scrollPosition.scrollCharPosition = positionByte - paintDataCache.bytesPerRect;
            if (horizontalScrollMode == HorizontalScrollMode.PIXEL) {
                scrollPosition.scrollCharOffset = paintDataCache.charWidth - (hexRect.width % paintDataCache.charWidth);
            } else {
                scrollPosition.scrollCharPosition++;
            }
            scrolled = true;
        }

        if (scrolled) {
            updateScrollBars();
            notifyScrolled();
        }
    }

    public void updateScrollBars() {
        if (scrollPosition.verticalMaxMode) {
            long lines = ((data.getDataSize() + scrollPosition.lineByteShift) / paintDataCache.bytesPerLine) + 1;
            int scrollValue;
            if (scrollPosition.scrollLinePosition < Long.MAX_VALUE / Integer.MAX_VALUE) {
                scrollValue = (int) ((scrollPosition.scrollLinePosition * Integer.MAX_VALUE) / lines);
            } else {
                scrollValue = (int) (scrollPosition.scrollLinePosition / (lines / Integer.MAX_VALUE));
            }
            verticalScrollBar.setValue(scrollValue);
        } else if (verticalScrollMode == VerticalScrollMode.PER_LINE) {
            verticalScrollBar.setValue((int) scrollPosition.scrollLinePosition);
        } else {
            verticalScrollBar.setValue((int) (scrollPosition.scrollLinePosition * paintDataCache.lineHeight + scrollPosition.scrollLineOffset));
        }

        if (horizontalScrollMode == HorizontalScrollMode.PER_CHAR) {
            horizontalScrollBar.setValue(scrollPosition.scrollCharPosition);
        } else {
            horizontalScrollBar.setValue(scrollPosition.scrollCharPosition * paintDataCache.charWidth + scrollPosition.scrollCharOffset);
        }
        repaint();
    }

    public void updateSelection(int modifiers, CaretPosition caretPosition) {
        if ((modifiers & KeyEvent.SHIFT_DOWN_MASK) > 0) {
            long currentPosition = caret.getDataPosition();
            long end = currentPosition;
            long start;
            if (selection != null) {
                start = selection.getStart();
                if (start == currentPosition) {
                    clearSelection();
                } else {
                    selection.setEnd(start < currentPosition ? end - 1 : end);
                }
            } else {
                start = caretPosition.getDataPosition();
                if (start == currentPosition) {
                    clearSelection();
                } else {
                    selection = new SelectionRange(start, start < currentPosition ? end - 1 : end);
                }
            }

            notifySelectionChanged();
        } else {
            clearSelection();
        }
        repaint();
    }

    public void moveRight(int modifiers) {
        CaretPosition caretPosition = caret.getCaretPosition();
        if (caretPosition.getDataPosition() < data.getDataSize()) {
            if (caret.getSection() == Section.CODE_MATRIX) {
                int codeOffset = caret.getCodeOffset();
                if (caretPosition.getDataPosition() < data.getDataSize()) {
                    if (codeOffset < codeType.getMaxDigits() - 1) {
                        caret.setCodeOffset(codeOffset + 1);
                    } else {
                        caret.setCaretPosition(caretPosition.getDataPosition() + 1, 0);
                    }
                    updateSelection(modifiers, caretPosition);
                    notifyCaretMoved();
                }
            } else {
                caret.setCaretPosition(caretPosition.getDataPosition() + 1);
                updateSelection(modifiers, caretPosition);
                notifyCaretMoved();
            }
        }
    }

    public void moveLeft(int modifiers) {
        CaretPosition caretPosition = caret.getCaretPosition();
        if (caret.getSection() == Section.CODE_MATRIX) {
            int codeOffset = caret.getCodeOffset();
            if (codeOffset > 0) {
                caret.setCodeOffset(codeOffset - 1);
                updateSelection(modifiers, caretPosition);
                notifyCaretMoved();
            } else if (caretPosition.getDataPosition() > 0) {
                caret.setCaretPosition(caretPosition.getDataPosition() - 1, codeType.getMaxDigits() - 1);
                updateSelection(modifiers, caretPosition);
                notifyCaretMoved();
            }
        } else if (caretPosition.getDataPosition() > 0) {
            caret.setCaretPosition(caretPosition.getDataPosition() - 1);
            updateSelection(modifiers, caretPosition);
            notifyCaretMoved();
        }
    }

    public SelectionRange getSelection() {
        return selection;
    }

    public void selectAll() {
        long dataSize = data.getDataSize();
        if (dataSize > 0) {
            selection = new SelectionRange(0, dataSize - 1);
            notifySelectionChanged();
            repaint();
        }
    }

    public void clearSelection() {
        selection = null;
        notifySelectionChanged();
        repaint();
    }

    private void notifySelectionChanged() {
        selectionChangedListeners.stream().forEach((selectionChangedListener) -> {
            selectionChangedListener.selectionChanged(selection);
        });
    }

    public boolean hasSelection() {
        return selection != null;
    }

    public void setSelection(SelectionRange selection) {
        this.selection = selection;
        notifySelectionChanged();
    }

    public void setCaretPosition(CaretPosition caretPosition) {
        caret.setCaretPosition(caretPosition);
        notifyCaretMoved();
    }

    public void setCaretPosition(long dataPosition) {
        caret.setCaretPosition(dataPosition);
        notifyCaretMoved();
    }

    public void setCaretPosition(long dataPosition, int codeOffset) {
        caret.setCaretPosition(dataPosition, codeOffset);
        notifyCaretMoved();
    }

    public void addSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.add(selectionChangedListener);
    }

    public void removeSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.remove(selectionChangedListener);
    }

    public void addCaretMovedListener(CaretMovedListener caretMovedListener) {
        caretMovedListeners.add(caretMovedListener);
    }

    public void removeCaretMovedListener(CaretMovedListener caretMovedListener) {
        caretMovedListeners.remove(caretMovedListener);
    }

    public void addEditationModeChangedListener(EditationModeChangedListener editationModeChangedListener) {
        editationModeChangedListeners.add(editationModeChangedListener);
    }

    public void removeEditationModeChangedListener(EditationModeChangedListener editationModeChangedListener) {
        editationModeChangedListeners.remove(editationModeChangedListener);
    }

    public void addDataChangedListener(DataChangedListener dataChangedListener) {
        dataChangedListeners.add(dataChangedListener);
    }

    public void removeDataChangedListener(DataChangedListener dataChangedListener) {
        dataChangedListeners.remove(dataChangedListener);
    }

    public void addScrollingListener(ScrollingListener scrollingListener) {
        scrollingListeners.add(scrollingListener);
    }

    public void removeScrollingListener(ScrollingListener scrollingListener) {
        scrollingListeners.remove(scrollingListener);
    }

    /**
     * Returns component area rectangle.
     *
     * Computed as component size minus border insets.
     *
     * @return rectangle of component area
     */
    public Rectangle getComponentRectangle() {
        return paintDataCache.componentRectangle;
    }

    /**
     * Returns main code area rectangle.
     *
     * @return rectangle of main hexadecimal area
     */
    public Rectangle getCodeSectionRectangle() {
        return paintDataCache.codeSectionRectangle;
    }

    /**
     * Returns X start position of the ascii preview area.
     *
     * @return X position or -1 if area not present
     */
    public int getPreviewX() {
        return paintDataCache.previewX;
    }

    public int getLineHeight() {
        return paintDataCache.lineHeight;
    }

    public int getBytesPerLine() {
        return paintDataCache.bytesPerLine;
    }

    public int getLinesPerRect() {
        return paintDataCache.linesPerRect;
    }

    public int getCharsPerLine() {
        return paintDataCache.charsPerLine;
    }

    public int getCharWidth() {
        return paintDataCache.charWidth;
    }

    public FontMetrics getFontMetrics() {
        return paintDataCache.fontMetrics;
    }

    /**
     * Returns header space size.
     *
     * @return header space size
     */
    public int getHeaderSpace() {
        return paintDataCache.headerSpace;
    }

    /**
     * Returns line number space size.
     *
     * @return line number space size
     */
    public int getLineNumberSpace() {
        return paintDataCache.lineNumberSpace;
    }

    /**
     * Returns current line number length in characters.
     *
     * @return line number length
     */
    public int getLineNumberLength() {
        return paintDataCache.lineNumbersLength;
    }

    public BinaryData getData() {
        return data;
    }

    public void setData(BinaryData data) {
        this.data = data;
        notifyDataChanged();
        computePaintData();
        repaint();
    }

    public long getDataSize() {
        return data == null ? 0 : data.getDataSize();
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
        repaint();
    }

    public CodeAreaPainter getPainter() {
        return painter;
    }

    public void setPainter(CodeAreaPainter painter) {
        if (painter == null) {
            throw new NullPointerException("Painter cannot be null");
        }

        this.painter = painter;
        repaint();
    }

    public boolean isValidChar(char value) {
        return charset.canEncode();
    }

    public byte[] charToBytes(char value) {
        ByteBuffer buffer = charset.encode(Character.toString(value));
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        computeFontMetrics();
    }

//    @Override
//    public void setBorder(Border border) {
//        super.setBorder(border);
//        computePaintData();
//    }
    private void computeFontMetrics() {
        paintDataCache.fontMetrics = Toolkit.getToolkit().getFontLoader().getFontMetrics(font);
        /**
         * Use small 'w' character to guess normal font width.
         */
        paintDataCache.charWidth = (int) paintDataCache.fontMetrics.computeStringWidth("w");
        /**
         * Compare it to small 'i' to detect if font is monospaced.
         *
         * TODO: Is there better way?
         */
        paintDataCache.monospaceFont = paintDataCache.charWidth == (int) paintDataCache.fontMetrics.computeStringWidth(" ") && paintDataCache.charWidth == (int) paintDataCache.fontMetrics.computeStringWidth("i");
        int fontHeight = (int) paintDataCache.fontMetrics.getLineHeight();
        if (paintDataCache.charWidth == 0) {
            paintDataCache.charWidth = fontHeight;
        }
        paintDataCache.lineHeight = fontHeight + subFontSpace;
        computePaintData();
    }

    public void computePaintData() {
        if (paintDataCache.fontMetrics == null) {
            return;
        }

        boolean verticalScrollBarVisible;
        boolean horizontalScrollBarVisible;

        Insets insets = getInsets();
        double width = getWidth();
        double height = getHeight();
        Rectangle compRect = paintDataCache.componentRectangle;
        compRect.x = (int) insets.getLeft();
        compRect.y = (int) insets.getTop();
        compRect.width = (int) (width - insets.getLeft() - insets.getRight());
        compRect.height = (int) (height - insets.getTop() - insets.getBottom());

        switch (lineNumberLength.getLineNumberType()) {
            case AUTO: {
                double natLog = Math.log(getDataSize());
                paintDataCache.lineNumbersLength = (int) Math.ceil(natLog / positionCodeType.getBaseLog());
                if (paintDataCache.lineNumbersLength == 0) {
                    paintDataCache.lineNumbersLength = 1;
                }
                break;
            }
            case SPECIFIED: {
                paintDataCache.lineNumbersLength = lineNumberLength.getLineNumberLength();
                break;
            }
        }

        int charsPerRect = computeCharsPerRect(compRect.width);
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
    }

    private int computeCharsPerRect(int width) {
        if (showLineNumbers) {
            width -= paintDataCache.charWidth * paintDataCache.lineNumbersLength + getLineNumberSpace();
        }

        return width / paintDataCache.charWidth;
    }

    public int computeFittingBytes(int charsPerRect) {
        if (viewMode == ViewMode.TEXT_PREVIEW) {
            return charsPerRect;
        }

        int fittingBytes = computeByteOffsetPerCodeCharOffset(charsPerRect, viewMode == ViewMode.DUAL);
        if (byteGroupSize != 0 || spaceGroupSize != 0) {
            if (computeCharsPerLine(fittingBytes + 1) <= charsPerRect) {
                fittingBytes++;
            }
        }

        return fittingBytes;
    }

    /**
     * Computes byte offset index for given code line offset.
     *
     * @param charOffset char offset position
     * @param includePreview flag if preview should be included
     * @return byte offset index
     */
    public int computeByteOffsetPerCodeCharOffset(int charOffset, boolean includePreview) {
        int byteOffset;
        if (byteGroupSize == 0) {
            if (spaceGroupSize == 0) {
                byteOffset = (charOffset - (includePreview ? 1 : 0))
                        / (codeType.getMaxDigits() + (includePreview ? 1 : 0));
            } else {
                byteOffset = (int) (((long) (charOffset - (includePreview ? 1 : 0)) * spaceGroupSize)
                        / ((long) (codeType.getMaxDigits() + (includePreview ? 1 : 0)) * spaceGroupSize + 2));
            }
        } else if (spaceGroupSize == 0) {
            byteOffset = (int) (((long) (charOffset - (includePreview ? 1 : 0)) * byteGroupSize)
                    / ((long) (codeType.getMaxDigits() + (includePreview ? 1 : 0)) * byteGroupSize + 1));
        } else {
            byteOffset = 0;
            int charsPerLine = includePreview ? 1 : 0;
            while (charsPerLine < charOffset) {
                charsPerLine += codeType.getMaxDigits() + (includePreview ? 1 : 0);
                byteOffset++;
                if ((byteOffset % byteGroupSize) == 0) {
                    if ((byteOffset % spaceGroupSize) == 0) {
                        charsPerLine += 2;
                    } else {
                        charsPerLine++;
                    }
                } else if ((byteOffset % spaceGroupSize) == 0) {
                    charsPerLine += 2;
                }
                if (charsPerLine > charOffset) {
                    return byteOffset - 1;
                }
            }
        }

        return byteOffset;
    }

    /**
     * Computes number of characters for given number of bytes / offset.
     *
     * @param bytesPerLine number of bytes per line
     * @return characters count
     */
    public int computeCharsPerLine(int bytesPerLine) {
        if (viewMode == ViewMode.TEXT_PREVIEW) {
            return bytesPerLine;
        }

        int charsPerLine = computeByteCharPos(bytesPerLine, false);

        if (viewMode == ViewMode.DUAL) {
            charsPerLine += bytesPerLine + 1;
        }

        return charsPerLine;
    }

    /**
     * Computes character position for byte code of given offset position
     *
     * @param byteOffset byte start offset
     * @return characters position
     */
    public int computeByteCharPos(int byteOffset) {
        return computeByteCharPos(byteOffset, true);
    }

    public int computeByteCharPos(int byteOffset, boolean includeTail) {
        int charsPerLine = codeType.getMaxDigits() * byteOffset;
        if (!includeTail) {
            byteOffset--;
        }
        if (byteGroupSize == 0) {
            if (spaceGroupSize != 0) {
                charsPerLine += (byteOffset / spaceGroupSize) * 2;
            }
        } else if (spaceGroupSize == 0) {
            charsPerLine += (byteOffset / byteGroupSize);
        } else {
            for (int index = 1; index <= byteOffset; index++) {
                if ((index % byteGroupSize) == 0) {
                    if ((index % spaceGroupSize) == 0) {
                        charsPerLine += 2;
                    } else {
                        charsPerLine++;
                    }
                } else if ((index % spaceGroupSize) == 0) {
                    charsPerLine += 2;
                }
            }
        }

        return charsPerLine;
    }

    public ColorsGroup getMainColors() {
        return new ColorsGroup(mainColors);
    }

    public ColorsGroup getAlternateColors() {
        return new ColorsGroup(alternateColors);
    }

    public ColorsGroup getSelectionColors() {
        return new ColorsGroup(selectionColors);
    }

    public ColorsGroup getMirrorSelectionColors() {
        return new ColorsGroup(mirrorSelectionColors);
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        repaint();
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    public void setMainColors(ColorsGroup colorsGroup) {
        mainColors.setColors(colorsGroup);
        repaint();
    }

    public void setAlternateColors(ColorsGroup colorsGroup) {
        alternateColors.setColors(colorsGroup);
        repaint();
    }

    public void setSelectionColors(ColorsGroup colorsGroup) {
        selectionColors.setColors(colorsGroup);
        repaint();
    }

    public void setMirrorSelectionColors(ColorsGroup colorsGroup) {
        mirrorSelectionColors.setColors(colorsGroup);
        repaint();
    }

    public Color getCursorColor() {
        return cursorColor;
    }

    public void setCursorColor(Color cursorColor) {
        this.cursorColor = cursorColor;
        negativeCursorColor = createNegativeColor(cursorColor);
        repaint();
    }

    public Color getNegativeCursorColor() {
        return negativeCursorColor;
    }

    public Color getDecorationLineColor() {
        return decorationLineColor;
    }

    public void setDecorationLineColor(Color decorationLineColor) {
        this.decorationLineColor = decorationLineColor;
        repaint();
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
        if (viewMode == ViewMode.CODE_MATRIX) {
            caret.setSection(Section.CODE_MATRIX);
            notifyCaretMoved();
        } else if (viewMode == ViewMode.TEXT_PREVIEW) {
            caret.setSection(Section.TEXT_PREVIEW);
            notifyCaretMoved();
        }
        computePaintData();
        repaint();
    }

    public CodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
        computePaintData();
        repaint();
    }

    public PositionCodeType getPositionCodeType() {
        return positionCodeType;
    }

    public void setPositionCodeType(PositionCodeType positionCodeType) {
        this.positionCodeType = positionCodeType;
        computePaintData();
        repaint();
    }

    public BackgroundMode getBackgroundMode() {
        return backgroundMode;
    }

    public void setBackgroundMode(BackgroundMode backgroundMode) {
        this.backgroundMode = backgroundMode;
        repaint();
    }

    public boolean isLineNumberBackground() {
        return lineNumberBackground;
    }

    public void setLineNumberBackground(boolean lineNumberBackground) {
        this.lineNumberBackground = lineNumberBackground;
        repaint();
    }

    public int getDecorationMode() {
        return decorationMode;
    }

    public void setDecorationMode(int decorationMode) {
        this.decorationMode = decorationMode;
        repaint();
    }

    public int getSubFontSpace() {
        return subFontSpace;
    }

    public void setSubFontSpace(int subFontSpace) {
        this.subFontSpace = subFontSpace;
    }

    public Section getActiveSection() {
        return caret.getSection();
    }

    public void setActiveSection(Section activeSection) {
        caret.setSection(activeSection);
        revealCursor();
        repaint();
    }

    public EditationAllowed getEditationAllowed() {
        return editationAllowed;
    }

    public void setEditationAllowed(EditationAllowed editationAllowed) {
        this.editationAllowed = editationAllowed;
        switch (editationAllowed) {
            case READ_ONLY: {
                editationMode = EditationMode.INSERT;
                break;
            }
            case OVERWRITE_ONLY: {
                editationMode = EditationMode.OVERWRITE;
                break;
            }
            default: // ignore
        }
        repaint();
    }

    public EditationMode getEditationMode() {
        return editationMode;
    }

    public void setEditationMode(EditationMode editationMode) {
        switch (editationAllowed) {
            case READ_ONLY: {
                editationMode = EditationMode.INSERT;
                break;
            }
            case OVERWRITE_ONLY: {
                editationMode = EditationMode.OVERWRITE;
                break;
            }
            default: // ignore
        }
        boolean chaged = editationMode != this.editationMode;
        this.editationMode = editationMode;
        if (chaged) {
            for (EditationModeChangedListener listener : editationModeChangedListeners) {
                listener.editationModeChanged(editationMode);
            }
            caret.resetBlink();
            repaint();
        }
    }

    public boolean isShowHeader() {
        return showHeader;
    }

    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
        computePaintData();
        repaint();
    }

    public boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
        computePaintData();
        repaint();
    }

    public boolean isEditable() {
        return editationAllowed != EditationAllowed.READ_ONLY;
    }

    public void setEditable(boolean editable) {
        setEditationAllowed(EditationAllowed.ALLOWED);
    }

    public boolean isWrapMode() {
        return wrapMode;
    }

    public void setWrapMode(boolean wrapMode) {
        this.wrapMode = wrapMode;
        computePaintData();
        repaint();
    }

    public boolean isHandleClipboard() {
        return handleClipboard;
    }

    public void setHandleClipboard(boolean handleClipboard) {
        this.handleClipboard = handleClipboard;
    }

    public boolean isShowUnprintableCharacters() {
        return showUnprintableCharacters;
    }

    public void setShowUnprintableCharacters(boolean showUnprintableCharacters) {
        this.showUnprintableCharacters = showUnprintableCharacters;
        repaint();
    }

    public boolean isShowShadowCursor() {
        return showShadowCursor;
    }

    public void setShowShadowCursor(boolean showShadowCursor) {
        this.showShadowCursor = showShadowCursor;
        repaint();
    }

    public int getLineLength() {
        return lineLength;
    }

    public void setLineLength(int lineLength) {
        if (lineLength < 1) {
            throw new IllegalStateException("Line length must be at least 1");
        }
        this.lineLength = lineLength;
        if (!wrapMode) {
            computePaintData();
            repaint();
        }
    }

    public int getByteGroupSize() {
        return byteGroupSize;
    }

    public void setByteGroupSize(int byteGroupSize) {
        if (byteGroupSize < 0) {
            throw new IllegalStateException("Negative group size is not valid");
        }
        this.byteGroupSize = byteGroupSize;
        computePaintData();
        repaint();
    }

    public int getSpaceGroupSize() {
        return spaceGroupSize;
    }

    public void setSpaceGroupSize(int spaceGroupSize) {
        if (spaceGroupSize < 0) {
            throw new IllegalStateException("Negative group size is not valid");
        }
        this.spaceGroupSize = spaceGroupSize;
        computePaintData();
        repaint();
    }

    public CharRenderingMode getCharRenderingMode() {
        return charRenderingMode;
    }

    public boolean isMonospaceFontDetected() {
        return paintDataCache.monospaceFont;
    }

    public void setCharRenderingMode(CharRenderingMode charRenderingMode) {
        this.charRenderingMode = charRenderingMode;
        computeFontMetrics();
        repaint();
    }

    public CharAntialiasingMode getCharAntialiasingMode() {
        return charAntialiasingMode;
    }

    public void setCharAntialiasingMode(CharAntialiasingMode charAntialiasingMode) {
        this.charAntialiasingMode = charAntialiasingMode;
        repaint();
    }

    public HexCharactersCase getHexCharactersCase() {
        return hexCharactersCase;
    }

    public void setHexCharactersCase(HexCharactersCase hexCharactersCase) {
        this.hexCharactersCase = hexCharactersCase;
        repaint();
    }

    public CodeAreaSpace.SpaceType getHeaderSpaceType() {
        return headerSpace.getSpaceType();
    }

    public void setHeaderSpaceType(CodeAreaSpace.SpaceType spaceType) {
        if (spaceType == null) {
            throw new NullPointerException();
        }
        headerSpace.setSpaceType(spaceType);
        computePaintData();
        repaint();
    }

    public int getHeaderSpaceSize() {
        return headerSpace.getSpaceSize();
    }

    public void setHeaderSpaceSize(int spaceSize) {
        if (spaceSize < 0) {
            throw new IllegalArgumentException("Negative space size is not valid");
        }
        headerSpace.setSpaceSize(spaceSize);
        computePaintData();
        repaint();
    }

    public CodeAreaSpace.SpaceType getLineNumberSpaceType() {
        return lineNumberSpace.getSpaceType();
    }

    public void setLineNumberSpaceType(CodeAreaSpace.SpaceType spaceType) {
        if (spaceType == null) {
            throw new NullPointerException();
        }
        lineNumberSpace.setSpaceType(spaceType);
        computePaintData();
        repaint();
    }

    public int getLineNumberSpaceSize() {
        return lineNumberSpace.getSpaceSize();
    }

    public void setLineNumberSpaceSize(int spaceSize) {
        if (spaceSize < 0) {
            throw new IllegalArgumentException("Negative space size is not valid");
        }
        lineNumberSpace.setSpaceSize(spaceSize);
        computePaintData();
        repaint();
    }

    public CodeAreaLineNumberLength.LineNumberType getLineNumberType() {
        return lineNumberLength.getLineNumberType();
    }

    public void setLineNumberType(CodeAreaLineNumberLength.LineNumberType lineNumberType) {
        if (lineNumberType == null) {
            throw new NullPointerException("Line number type cannot be null");
        }
        lineNumberLength.setLineNumberType(lineNumberType);
        computePaintData();
        repaint();
    }

    public int getLineNumberSpecifiedLength() {
        return lineNumberLength.getLineNumberLength();
    }

    public void setLineNumberSpecifiedLength(int lineNumberSize) {
        if (lineNumberSize < 1) {
            throw new IllegalArgumentException("Line number type cannot be less then 1");
        }
        lineNumberLength.setLineNumberLength(lineNumberSize);
        computePaintData();
        repaint();
    }

    public ScrollBarVisibility getVerticalScrollBarVisibility() {
        return verticalScrollBarVisibility;
    }

    public void setVerticalScrollBarVisibility(ScrollBarVisibility verticalScrollBarVisibility) {
        this.verticalScrollBarVisibility = verticalScrollBarVisibility;
        computePaintData();
        updateScrollBars();
    }

    public VerticalScrollMode getVerticalScrollMode() {
        return verticalScrollMode;
    }

    public void setVerticalScrollMode(VerticalScrollMode verticalScrollMode) {
        this.verticalScrollMode = verticalScrollMode;
        long linePosition = scrollPosition.scrollLinePosition;
        if (verticalScrollMode == VerticalScrollMode.PER_LINE) {
            scrollPosition.scrollLineOffset = 0;
        }
        computePaintData();
        scrollPosition.scrollLinePosition = linePosition;
        updateScrollBars();
        notifyScrolled();
    }

    public ScrollBarVisibility getHorizontalScrollBarVisibility() {
        return horizontalScrollBarVisibility;
    }

    public void setHorizontalScrollBarVisibility(ScrollBarVisibility horizontalScrollBarVisibility) {
        this.horizontalScrollBarVisibility = horizontalScrollBarVisibility;
        computePaintData();
        updateScrollBars();
    }

    public HorizontalScrollMode getHorizontalScrollMode() {
        return horizontalScrollMode;
    }

    public void setHorizontalScrollMode(HorizontalScrollMode horizontalScrollMode) {
        this.horizontalScrollMode = horizontalScrollMode;
        int bytePosition = scrollPosition.scrollCharPosition;
        if (horizontalScrollMode == HorizontalScrollMode.PER_CHAR) {
            scrollPosition.scrollCharOffset = 0;
        }
        computePaintData();
        scrollPosition.scrollCharPosition = bytePosition;
        updateScrollBars();
        notifyScrolled();
    }

    public long getDataPosition() {
        return caret.getDataPosition();
    }

    public int getCodeOffset() {
        return caret.getCodeOffset();
    }

    public CaretPosition getCaretPosition() {
        return caret.getCaretPosition();
    }

    public CodeAreaCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public void setCommandHandler(CodeAreaCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void copy() {
        commandHandler.copy();
    }

    public void copyAsCode() {
        commandHandler.copyAsCode();
    }

    public void cut() {
        commandHandler.cut();
    }

    public void paste() {
        commandHandler.paste();
    }

    public void pasteFromCode() {
        commandHandler.pasteFromCode();
    }

    public void delete() {
        commandHandler.delete();
    }

    public boolean canPaste() {
        return commandHandler.canPaste();
    }

    public void resetPosition() {
        getScrollPosition().reset();
        updateScrollBars();
        notifyScrolled();
        caret.setCaretPosition(0);
        notifyCaretMoved();
        commandHandler.caretMoved();
        computePaintData();
        clearSelection();
    }

    private static Color createOddColor(Color color) {
        return new Color(
                computeOddColorComponent(color.getRed()),
                computeOddColorComponent(color.getGreen()),
                computeOddColorComponent(color.getBlue()),
                color.getOpacity());
    }

    private static double computeOddColorComponent(double colorComponent) {
        return colorComponent + (colorComponent > 0.25 ? -0.0625 : 0.0625);
    }

    private static Color createNegativeColor(Color color) {
        return new Color(
                1 - color.getRed(),
                1 - color.getGreen(),
                1 - color.getBlue(),
                color.getOpacity());
    }

    public static enum BackgroundMode {
        NONE, PLAIN, STRIPPED, GRIDDED
    }

    public static enum VerticalScrollMode {
        PER_LINE, PIXEL
    }

    public static enum HorizontalScrollMode {
        PER_CHAR, PIXEL
    }

    /**
     * Character rendering mode.
     */
    public static enum CharRenderingMode {
        /**
         * Centers characters if width is not default and detects monospace
         * fonts to render characters as string if possible
         */
        AUTO,
        /**
         * Render sequence of characters from top left corner of the line
         * ignoring character width. It's fastest, but render correctly only for
         * monospaced fonts and charsets where all characters have same width
         */
        LINE_AT_ONCE,
        /**
         * Render each character from top left corner of it's position
         */
        TOP_LEFT,
        /**
         * Centers each character in it's area
         */
        CENTER
    }

    public static enum CharAntialiasingMode {
        OFF, AUTO, DEFAULT, BASIC, GASP, LCD_HRGB, LCD_HBGR, LCD_VRGB, LCD_VBGR
    }

    /**
     * Precomputed data for painting of the component.
     */
    private static class PaintDataCache {

        /**
         * Font related paint data.
         */
        FontMetrics fontMetrics = null;
        int charWidth;
        int lineHeight;
        boolean monospaceFont = false;

        int bytesPerLine;
        int charsPerLine;
        int lineNumbersLength;

        /**
         * Component area without border insets.
         */
        final Rectangle componentRectangle = new Rectangle();
        /**
         * Space between header and code area.
         */
        int headerSpace;
        /**
         * Space between line numbers and code area.
         */
        int lineNumberSpace;
        /**
         * Space between main code area and preview.
         */
        int previewSpace;

        /**
         * Main data area.
         *
         * Component area without header, line numbers and scrollbars.
         */
        final Rectangle codeSectionRectangle = new Rectangle();
        int previewX;
        int previewStartChar;
        int bytesPerRect;
        int linesPerRect;
        int scrollBarThickness = 17;
    }

    /**
     * Scrolling position.
     */
    public static class ScrollPosition {

        private long scrollLinePosition = 0;
        private int scrollLineOffset = 0;
        private int scrollCharPosition = 0;
        private int scrollCharOffset = 0;
        /**
         * How is start of the line scrolled compare it's normal position.
         */
        private int lineByteShift = 0;
        /**
         * Flag for scroll mode with huge data.
         */
        private boolean verticalMaxMode = false;

        public long getScrollLinePosition() {
            return scrollLinePosition;
        }

        public int getScrollLineOffset() {
            return scrollLineOffset;
        }

        public int getScrollCharPosition() {
            return scrollCharPosition;
        }

        public int getScrollCharOffset() {
            return scrollCharOffset;
        }

        public int getLineByteShift() {
            return lineByteShift;
        }

        public void setScrollLinePosition(long scrollLinePosition) {
            this.scrollLinePosition = scrollLinePosition;
        }

        public void setScrollLineOffset(int scrollLineOffset) {
            this.scrollLineOffset = scrollLineOffset;
        }

        public void setScrollCharPosition(int scrollCharPosition) {
            this.scrollCharPosition = scrollCharPosition;
        }

        public void setScrollCharOffset(int scrollCharOffset) {
            this.scrollCharOffset = scrollCharOffset;
        }

        public void setLineByteShift(int lineByteShift) {
            this.lineByteShift = lineByteShift;
        }

        public boolean isVerticalMaxMode() {
            return verticalMaxMode;
        }

        public void setVerticalMaxMode(boolean verticalMaxMode) {
            this.verticalMaxMode = verticalMaxMode;
        }

        private void reset() {
            scrollLinePosition = 0;
            scrollLineOffset = 0;
            scrollCharPosition = 0;
            scrollCharOffset = 0;
            lineByteShift = 0;
        }
    }

    private class CodeAreaMouseListener extends MouseAdapter implements MouseMotionListener, MouseWheelListener {

//        private Cursor currentCursor = getCursor();
//        private final Cursor defaultCursor = Cursor.getDefaultCursor();
//        private final Cursor textCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
//
//        @Override
//        public void mousePressed(MouseEvent me) {
//            requestFocus();
//            if (isEnabled() && me.getButton() == MouseEvent.BUTTON1) {
//                moveCaret(me, me.getModifiersEx());
//                revealCursor();
//                mouseDown = true;
//            }
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent me) {
//            mouseDown = false;
//        }
//
//        @Override
//        public void mouseExited(MouseEvent e) {
//            currentCursor = defaultCursor;
//            setCursor(defaultCursor);
//        }
//
//        @Override
//        public void mouseEntered(MouseEvent e) {
//            updateMouseCursor(e);
//        }
//
//        @Override
//        public void mouseMoved(MouseEvent e) {
//            updateMouseCursor(e);
//        }
//
//        private void updateMouseCursor(MouseEvent e) {
//            Cursor newCursor = defaultCursor;
//            Rectangle hexRect = paintDataCache.codeSectionRectangle;
//            if (e.getX() >= hexRect.x && e.getY() >= hexRect.y) {
//                newCursor = textCursor;
//            }
//
//            if (newCursor != currentCursor) {
//                currentCursor = newCursor;
//                setCursor(newCursor);
//            }
//        }
//
//        @Override
//        public void mouseDragged(MouseEvent me) {
//            updateMouseCursor(me);
//            if (isEnabled() && mouseDown) {
//                moveCaret(me, KeyEvent.SHIFT_DOWN_MASK);
//                revealCursor();
//            }
//        }
//
//        @Override
//        public void mouseWheelMoved(MouseWheelEvent e) {
//            if (!isEnabled()) {
//                return;
//            }
//
//            if (e.isShiftDown() && horizontalScrollBar.isVisible()) {
//                if (e.getWheelRotation() > 0) {
//                    // TODO
//                    int visibleChars = paintDataCache.codeSectionRectangle.width / paintDataCache.charWidth;
//                    int bytes = paintDataCache.bytesPerLine - visibleChars;
//                    if (scrollPosition.scrollCharPosition < bytes) {
//                        if (scrollPosition.scrollCharPosition < bytes - MOUSE_SCROLL_LINES) {
//                            scrollPosition.scrollCharPosition += MOUSE_SCROLL_LINES;
//                        } else {
//                            scrollPosition.scrollCharPosition = bytes;
//                        }
//                        updateScrollBars();
//                        notifyScrolled();
//                    }
//                } else if (scrollPosition.scrollCharPosition > 0) {
//                    if (scrollPosition.scrollCharPosition > MOUSE_SCROLL_LINES) {
//                        scrollPosition.scrollCharPosition -= MOUSE_SCROLL_LINES;
//                    } else {
//                        scrollPosition.scrollCharPosition = 0;
//                    }
//                    updateScrollBars();
//                    notifyScrolled();
//                }
//            } else if (e.getWheelRotation() > 0) {
//                long lines = (data.getDataSize() + scrollPosition.lineByteShift) / paintDataCache.bytesPerLine;
//                if (lines * paintDataCache.bytesPerLine < data.getDataSize()) {
//                    lines++;
//                }
//                lines -= paintDataCache.linesPerRect;
//                if (scrollPosition.scrollLinePosition < lines) {
//                    if (scrollPosition.scrollLinePosition < lines - MOUSE_SCROLL_LINES) {
//                        scrollPosition.scrollLinePosition += MOUSE_SCROLL_LINES;
//                    } else {
//                        scrollPosition.scrollLinePosition = lines;
//                    }
//                    updateScrollBars();
//                    notifyScrolled();
//                }
//            } else if (scrollPosition.scrollLinePosition > 0) {
//                if (scrollPosition.scrollLinePosition > MOUSE_SCROLL_LINES) {
//                    scrollPosition.scrollLinePosition -= MOUSE_SCROLL_LINES;
//                } else {
//                    scrollPosition.scrollLinePosition = 0;
//                }
//                updateScrollBars();
//                notifyScrolled();
//            }
//        }
    }

    private class CodeAreaKeyListener extends KeyAdapter {

        public CodeAreaKeyListener() {
        }

        @Override
        public void keyTyped(KeyEvent keyEvent) {
            commandHandler.keyTyped(keyEvent);
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            commandHandler.keyPressed(keyEvent);
        }
    }

    private class CodeAreaComponentListener implements ComponentListener {

        public CodeAreaComponentListener() {
        }

        @Override
        public void componentResized(ComponentEvent e) {
            computePaintData();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }

    private class VerticalAdjustmentListener implements AdjustmentListener {

        public VerticalAdjustmentListener() {
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            double scrollBarValue = verticalScrollBar.getValue();
//            if (scrollPosition.verticalMaxMode) {
//                int maxValue = Integer.MAX_VALUE - verticalScrollBar.getVisibleAmount();
//                long lines = ((data.getDataSize() + scrollPosition.lineByteShift) / paintDataCache.bytesPerLine) - paintDataCache.linesPerRect + 1;
//                long targetLine;
//                if (scrollBarValue > 0 && lines > maxValue / scrollBarValue) {
//                    targetLine = scrollBarValue * (lines / maxValue);
//                    long rest = lines % maxValue;
//                    targetLine += (rest * scrollBarValue) / maxValue;
//                } else {
//                    targetLine = (scrollBarValue * lines) / Integer.MAX_VALUE;
//                }
//                scrollPosition.scrollLinePosition = targetLine;
//                if (verticalScrollMode != VerticalScrollMode.PER_LINE) {
//                    scrollPosition.scrollLineOffset = 0;
//                }
//            } else if (verticalScrollMode == VerticalScrollMode.PER_LINE) {
//                scrollPosition.scrollLinePosition = scrollBarValue;
//            } else {
//                scrollPosition.scrollLinePosition = scrollBarValue / paintDataCache.lineHeight;
//                scrollPosition.scrollLineOffset = scrollBarValue % paintDataCache.lineHeight;
//            }
//
//            repaint();
//            notifyScrolled();
        }
    }

    private class HorizontalAdjustmentListener implements AdjustmentListener {

        public HorizontalAdjustmentListener() {
        }

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
//            if (horizontalScrollMode == HorizontalScrollMode.PER_CHAR) {
//                scrollPosition.scrollCharPosition = horizontalScrollBar.getValue();
//            } else {
//                scrollPosition.scrollCharPosition = horizontalScrollBar.getValue() / paintDataCache.charWidth;
//                scrollPosition.scrollCharOffset = horizontalScrollBar.getValue() % paintDataCache.charWidth;
//            }
//            repaint();
//            notifyScrolled();
        }
    }
}
