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
package org.exbin.deltahex.swing.extended;

import java.awt.Font;
import java.awt.Graphics;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.exbin.deltahex.CaretMovedListener;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.CodeAreaCaretPosition;
import org.exbin.deltahex.BasicCodeAreaSection;
import org.exbin.deltahex.BasicCodeAreaZone;
import org.exbin.deltahex.CodeCharactersCase;
import org.exbin.deltahex.CodeAreaViewMode;
import org.exbin.deltahex.CodeType;
import org.exbin.deltahex.EditationMode;
import org.exbin.deltahex.EditationModeChangedListener;
import org.exbin.deltahex.ScrollBarVisibility;
import org.exbin.deltahex.ScrollingListener;
import org.exbin.deltahex.SelectionChangedListener;
import org.exbin.deltahex.SelectionRange;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.deltahex.swing.CodeAreaPainter;
import org.exbin.deltahex.swing.CodeAreaWorker;
import org.exbin.deltahex.capability.CaretCapable;
import org.exbin.deltahex.capability.CharsetCapable;
import org.exbin.deltahex.capability.ClipboardCapable;
import org.exbin.deltahex.capability.CodeTypeCapable;
import org.exbin.deltahex.capability.EditationModeCapable;
import org.exbin.deltahex.capability.ViewModeCapable;
import org.exbin.deltahex.swing.capability.ScrollingCapable;
import org.exbin.deltahex.capability.SelectionCapable;
import org.exbin.deltahex.capability.CodeCharactersCaseCapable;
import org.exbin.deltahex.swing.MovementDirection;
import org.exbin.deltahex.swing.ScrollingDirection;
import org.exbin.deltahex.swing.basic.BasicBackgroundPaintMode;
import org.exbin.deltahex.swing.basic.CodeAreaScrollPosition;
import org.exbin.deltahex.swing.basic.DefaultCodeAreaCaret;
import org.exbin.deltahex.swing.basic.DefaultCodeAreaPainter;
import org.exbin.deltahex.swing.basic.HorizontalScrollUnit;
import org.exbin.deltahex.swing.basic.VerticalScrollUnit;
import org.exbin.deltahex.swing.capability.FontCapable;
import org.exbin.deltahex.swing.capability.BackgroundPaintCapable;
import org.exbin.deltahex.swing.extended.capability.AntialiasingCapable;
import org.exbin.deltahex.capability.RowWrappingCapable;

/**
 * Code area component extended worker.
 *
 * @version 0.2.0 2018/03/23
 * @author ExBin Project (http://exbin.org)
 */
public class ExtCodeAreaWorker implements CodeAreaWorker, SelectionCapable, CaretCapable, ScrollingCapable, ViewModeCapable,
        CodeTypeCapable, EditationModeCapable, CharsetCapable, CodeCharactersCaseCapable, FontCapable, AntialiasingCapable,
        BackgroundPaintCapable, RowWrappingCapable, ClipboardCapable {

    @Nonnull
    protected final CodeArea codeArea;

    @Nonnull
    private CodeAreaPainter painter;

    @Nonnull
    private final DefaultCodeAreaCaret caret;
    @Nonnull
    private final SelectionRange selection = new SelectionRange();
    @Nonnull
    private final CodeAreaScrollPosition scrollPosition = new CodeAreaScrollPosition();

    @Nonnull
    private Charset charset = Charset.defaultCharset();
    private boolean handleClipboard = true;

    @Nonnull
    private EditationMode editationMode = EditationMode.OVERWRITE;
    @Nonnull
    private CodeAreaViewMode viewMode = CodeAreaViewMode.DUAL;
    @Nullable
    private Font font;
    @Nonnull
    private BasicBackgroundPaintMode borderPaintMode = BasicBackgroundPaintMode.STRIPED;
    @Nonnull
    private CharacterRenderingMode characterRenderingMode = CharacterRenderingMode.AUTO;
    @Nonnull
    private AntialiasingMode antialiasingMode = AntialiasingMode.AUTO;
    @Nonnull
    private CodeType codeType = CodeType.HEXADECIMAL;
    @Nonnull
    private CodeCharactersCase codeCharactersCase = CodeCharactersCase.UPPER;
    private boolean showMirrorCursor = true;
    private boolean lineWrapping = false;
    private int maxBytesPerLine = 16;

    @Nonnull
    private ScrollBarVisibility verticalScrollBarVisibility = ScrollBarVisibility.IF_NEEDED;
    @Nonnull
    private VerticalScrollUnit verticalScrollUnit = VerticalScrollUnit.ROW;
    @Nonnull
    private ScrollBarVisibility horizontalScrollBarVisibility = ScrollBarVisibility.IF_NEEDED;
    @Nonnull
    private HorizontalScrollUnit horizontalScrollUnit = HorizontalScrollUnit.PIXEL;

    private final List<CaretMovedListener> caretMovedListeners = new ArrayList<>();
    private final List<ScrollingListener> scrollingListeners = new ArrayList<>();
    private final List<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();
    private final List<EditationModeChangedListener> editationModeChangedListeners = new ArrayList<>();

    public ExtCodeAreaWorker(@Nonnull CodeArea codeArea) {
        this.codeArea = codeArea;

        caret = new DefaultCodeAreaCaret(codeArea);
        painter = new DefaultCodeAreaPainter(this);
    }

    @Nonnull
    @Override
    public CodeArea getCodeArea() {
        return codeArea;
    }

    @Nonnull
    public CodeAreaPainter getPainter() {
        return painter;
    }

    public void setPainter(@Nonnull CodeAreaPainter painter) {
        Objects.requireNonNull(painter, "Painter cannot be null");

        this.painter = painter;
        repaint();
    }

    @Override
    public boolean isInitialized() {
        return painter.isInitialized();
    }

    @Override
    public void paintComponent(@Nonnull Graphics g) {
        painter.paintComponent(g);
    }

    @Nonnull
    @Override
    public DefaultCodeAreaCaret getCaret() {
        return caret;
    }

    @Override
    public boolean isShowMirrorCursor() {
        return showMirrorCursor;
    }

    @Override
    public void setShowMirrorCursor(boolean showMirrorCursor) {
        this.showMirrorCursor = showMirrorCursor;
        repaint();
    }

    public long getDataPosition() {
        return caret.getDataPosition();
    }

    public int getCodeOffset() {
        return caret.getCodeOffset();
    }

    public int getActiveSection() {
        return caret.getSection();
    }

    @Nonnull
    public CaretPosition getCaretPosition() {
        return caret.getCaretPosition();
    }

    public void setCaretPosition(@Nonnull CaretPosition caretPosition) {
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

    @Override
    public int getMouseCursorShape(int positionX, int positionY) {
        return painter.getMouseCursorShape(positionX, positionY);
    }

    @Override
    public BasicCodeAreaZone getPositionZone(int positionX, int positionY) {
        return painter.getPositionZone(positionX, positionY);
    }

    @Nonnull
    @Override
    public CodeCharactersCase getCodeCharactersCase() {
        return codeCharactersCase;
    }

    @Override
    public void setCodeCharactersCase(@Nonnull CodeCharactersCase codeCharactersCase) {
        this.codeCharactersCase = codeCharactersCase;
        repaint();
    }

    @Override
    public void rebuildColors() {
    }

    @Nonnull
    @Override
    public CodeAreaViewMode getViewMode() {
        return viewMode;
    }

    @Override
    public void setViewMode(@Nonnull CodeAreaViewMode viewMode) {
        this.viewMode = viewMode;
        if (viewMode == CodeAreaViewMode.CODE_MATRIX) {
            getCaret().setSection(BasicCodeAreaSection.CODE_MATRIX.getSection());
            notifyCaretMoved();
        } else if (viewMode == CodeAreaViewMode.TEXT_PREVIEW) {
            getCaret().setSection(BasicCodeAreaSection.TEXT_PREVIEW.getSection());
            notifyCaretMoved();
        }
        repaint();
    }

    @Override
    @Nonnull
    public CodeType getCodeType() {
        return codeType;
    }

    @Override
    public void setCodeType(@Nonnull CodeType codeType) {
        this.codeType = codeType;
        painter.reset();
        repaint();
    }

    @Override
    public void revealCursor() {
        revealPosition(caret.getCaretPosition());
    }

    @Override
    public void revealPosition(@Nonnull CaretPosition caretPosition) {
        if (!isInitialized()) {
            // Silently ignore if painter is not yet initialized
            return;
        }

        CodeAreaScrollPosition revealScrollPosition = painter.computeRevealScrollPosition(caretPosition);
        if (revealScrollPosition != null) {
            setScrollPosition(revealScrollPosition);
            codeArea.resetPainter();
            updateScrollBars();
            notifyScrolled();
        }
    }

    public void revealPosition(long dataPosition, int dataOffset, int section) {
        revealPosition(new CodeAreaCaretPosition(dataPosition, dataOffset, section));
    }

    @Override
    public void centerOnCursor() {
        centerOnPosition(caret.getCaretPosition());
    }

    @Override
    public void centerOnPosition(@Nonnull CaretPosition caretPosition) {
        if (!isInitialized()) {
            // Silently ignore if painter is not yet initialized
            return;
        }

        CodeAreaScrollPosition centerOnScrollPosition = painter.computeCenterOnScrollPosition(caretPosition);
        if (centerOnScrollPosition != null) {
            setScrollPosition(centerOnScrollPosition);
            codeArea.resetPainter();
            updateScrollBars();
            notifyScrolled();
        }
    }

    public void centerOnPosition(long dataPosition, int dataOffset, int section) {
        centerOnPosition(new CodeAreaCaretPosition(dataPosition, dataOffset, section));
    }

    @Nullable
    @Override
    public CaretPosition mousePositionToClosestCaretPosition(int positionX, int positionY) {
        return painter.mousePositionToClosestCaretPosition(positionX, positionY);
    }

    @Nonnull
    @Override
    public CaretPosition computeMovePosition(@Nonnull CaretPosition position, @Nonnull MovementDirection direction) {
        return painter.computeMovePosition(position, direction);
    }

    @Nonnull
    @Override
    public CodeAreaScrollPosition computeScrolling(@Nonnull CodeAreaScrollPosition startPosition, @Nonnull ScrollingDirection scrollingShift) {
        return painter.computeScrolling(startPosition, scrollingShift);
    }

    @Override
    public void updateScrollBars() {
        painter.updateScrollBars();
        repaint();
    }

    @Nonnull
    @Override
    public CodeAreaScrollPosition getScrollPosition() {
        return scrollPosition;
    }

    @Override
    public void setScrollPosition(@Nonnull CodeAreaScrollPosition scrollPosition) {
        this.scrollPosition.setScrollPosition(scrollPosition);
    }

    @Nonnull
    @Override
    public ScrollBarVisibility getVerticalScrollBarVisibility() {
        return verticalScrollBarVisibility;
    }

    @Override
    public void setVerticalScrollBarVisibility(ScrollBarVisibility verticalScrollBarVisibility) {
        this.verticalScrollBarVisibility = verticalScrollBarVisibility;
        codeArea.resetPainter();
        updateScrollBars();
    }

    @Nonnull
    @Override
    public VerticalScrollUnit getVerticalScrollUnit() {
        return verticalScrollUnit;
    }

    @Override
    public void setVerticalScrollUnit(@Nonnull VerticalScrollUnit verticalScrollUnit) {
        this.verticalScrollUnit = verticalScrollUnit;
        long linePosition = scrollPosition.getScrollRowPosition();
        if (verticalScrollUnit == VerticalScrollUnit.ROW) {
            scrollPosition.setScrollRowOffset(0);
        }
        codeArea.resetPainter();
        scrollPosition.setScrollRowPosition(linePosition);
        updateScrollBars();
        notifyScrolled();
    }

    @Nonnull
    @Override
    public ScrollBarVisibility getHorizontalScrollBarVisibility() {
        return horizontalScrollBarVisibility;
    }

    @Override
    public void setHorizontalScrollBarVisibility(@Nonnull ScrollBarVisibility horizontalScrollBarVisibility) {
        this.horizontalScrollBarVisibility = horizontalScrollBarVisibility;
        codeArea.resetPainter();
        updateScrollBars();
    }

    @Nonnull
    @Override
    public HorizontalScrollUnit getHorizontalScrollUnit() {
        return horizontalScrollUnit;
    }

    @Override
    public void setHorizontalScrollUnit(@Nonnull HorizontalScrollUnit horizontalScrollUnit) {
        this.horizontalScrollUnit = horizontalScrollUnit;
        int bytePosition = scrollPosition.getScrollCharPosition();
        if (horizontalScrollUnit == HorizontalScrollUnit.CHARACTER) {
            scrollPosition.setScrollCharOffset(0);
        }
        codeArea.resetPainter();
        scrollPosition.setScrollCharPosition(bytePosition);
        updateScrollBars();
        notifyScrolled();
    }

    @Override
    public void reset() {
        painter.reset();
    }

    private void repaint() {
        codeArea.resetPainter();
        codeArea.repaint();
    }

    @Override
    public void notifyCaretChanged() {
        codeArea.repaint();
    }

    @Nonnull
    @Override
    public CharacterRenderingMode getCharacterRenderingMode() {
        return characterRenderingMode;
    }

    @Override
    public void setCharacterRenderingMode(@Nonnull CharacterRenderingMode characterRenderingMode) {
        this.characterRenderingMode = characterRenderingMode;
        painter.reset();
        repaint();
    }

    @Override
    public AntialiasingMode getAntialiasingMode() {
        return antialiasingMode;
    }

    @Override
    public void setAntialiasingMode(AntialiasingMode antialiasingMode) {
        this.antialiasingMode = antialiasingMode;
        reset();
        repaint();
    }

    @Nonnull
    @Override
    public SelectionRange getSelection() {
        return selection;
    }

    @Override
    public void setSelection(@Nonnull SelectionRange selection) {
        Objects.requireNonNull(painter, "Selection cannot be null");

        this.selection.setSelection(selection);
        notifySelectionChanged();
        repaint();
    }

    @Override
    public void setSelection(long start, long end) {
        this.selection.setSelection(start, end);
        notifySelectionChanged();
        repaint();
    }

    @Override
    public boolean hasSelection() {
        return !selection.isEmpty();
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(@Nonnull Charset charset) {
        Objects.requireNonNull(charset, "Charset cannot be null");

        this.charset = charset;
        painter.reset();
        repaint();
    }

    @Nonnull
    @Override
    public EditationMode getEditationMode() {
        return editationMode;
    }

    @Override
    public boolean isEditable() {
        return editationMode != EditationMode.READ_ONLY;
    }

    @Override
    public void setEditationMode(@Nonnull EditationMode editationMode) {
        boolean changed = editationMode != this.editationMode;
        this.editationMode = editationMode;
        if (changed) {
            editationModeChangedListeners.forEach((listener) -> {
                listener.editationModeChanged(editationMode);
            });
            caret.resetBlink();
            repaint();
        }
    }

    @Override
    public boolean isHandleClipboard() {
        return handleClipboard;
    }

    @Override
    public void setHandleClipboard(boolean handleClipboard) {
        this.handleClipboard = handleClipboard;
    }

    @Nonnull
    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(@Nonnull Font font) {
        this.font = font;
        painter.reset();
        repaint();
    }

    @Nonnull
    @Override
    public BasicBackgroundPaintMode getBackgroundPaintMode() {
        return borderPaintMode;
    }

    @Override
    public void setBackgroundPaintMode(@Nonnull BasicBackgroundPaintMode borderPaintMode) {
        this.borderPaintMode = borderPaintMode;
        repaint();
    }

    @Override
    public boolean isRowWrapping() {
        return lineWrapping;
    }

    @Override
    public void setLineWrapping(boolean lineWrapping) {
        this.lineWrapping = lineWrapping;
    }

    @Override
    public int getMaxBytesPerRow() {
        return maxBytesPerLine;
    }

    @Override
    public void setMaxBytesPerLine(int maxBytesPerLine) {
        this.maxBytesPerLine = maxBytesPerLine;
    }

    public void notifySelectionChanged() {
        selectionChangedListeners.forEach((selectionChangedListener) -> {
            selectionChangedListener.selectionChanged(selection);
        });
    }

    @Override
    public void notifyCaretMoved() {
        caretMovedListeners.forEach((caretMovedListener) -> {
            caretMovedListener.caretMoved(caret.getCaretPosition());
        });
    }

    @Override
    public void notifyScrolled() {
        scrollingListeners.forEach((scrollingListener) -> {
            scrollingListener.scrolled();
        });
    }

    @Override
    public void addSelectionChangedListener(@Nullable SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.add(selectionChangedListener);
    }

    @Override
    public void removeSelectionChangedListener(@Nullable SelectionChangedListener selectionChangedListener) {
        selectionChangedListeners.remove(selectionChangedListener);
    }

    @Override
    public void addCaretMovedListener(@Nullable CaretMovedListener caretMovedListener) {
        caretMovedListeners.add(caretMovedListener);
    }

    @Override
    public void removeCaretMovedListener(@Nullable CaretMovedListener caretMovedListener) {
        caretMovedListeners.remove(caretMovedListener);
    }

    @Override
    public void addScrollingListener(@Nullable ScrollingListener scrollingListener) {
        scrollingListeners.add(scrollingListener);
    }

    @Override
    public void removeScrollingListener(@Nullable ScrollingListener scrollingListener) {
        scrollingListeners.remove(scrollingListener);
    }

    @Override
    public void addEditationModeChangedListener(@Nullable EditationModeChangedListener editationModeChangedListener) {
        editationModeChangedListeners.add(editationModeChangedListener);
    }

    @Override
    public void removeEditationModeChangedListener(@Nullable EditationModeChangedListener editationModeChangedListener) {
        editationModeChangedListeners.remove(editationModeChangedListener);
    }
}