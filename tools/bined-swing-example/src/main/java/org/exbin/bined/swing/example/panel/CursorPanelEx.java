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
package org.exbin.bined.swing.example.panel;

import javax.annotation.Nonnull;
import org.exbin.bined.capability.CaretCapable;
import org.exbin.bined.swing.CodeArea;
import org.exbin.bined.swing.basic.DefaultCodeAreaCaret;
import org.exbin.bined.swing.extended.ExtCodeAreaWorker;

/**
 * Hexadecimal editor example panel.
 *
 * @version 0.2.0 2018/03/18
 * @author ExBin Project (http://exbin.org)
 */
public class CursorPanelEx extends javax.swing.JPanel {

    private final CodeArea codeArea;
    private final ExtCodeAreaWorker worker;

    public CursorPanelEx(@Nonnull CodeArea codeArea) {
        this.codeArea = codeArea;
        worker = (ExtCodeAreaWorker) codeArea.getWorker();

        initComponents();

        cursorRenderingModeComboBox.setSelectedIndex(worker.getCaret().getRenderingMode().ordinal());
        showShadowCursorCheckBox.setSelected(worker.isShowMirrorCursor());

//        cursorInsertShapeComboBox.setSelectedIndex(((CaretCapable) codeArea.getWorker()).getCaret().getInsertCursorShape().ordinal());
//        cursorOverwriteShapeComboBox.setSelectedIndex(((CaretCapable) codeArea.getWorker()).getCaret().getOverwriteCursorShape().ordinal());
//        cursorBlinkingRateSpinner.setValue(((CaretCapable) codeArea.getWorker()).getCaret().getBlinkRate());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        showShadowCursorCheckBox = new javax.swing.JCheckBox();
        cursorRenderingModeLabel = new javax.swing.JLabel();
        cursorRenderingModeComboBox = new javax.swing.JComboBox<>();
        cursorInsertShapeModeLabel = new javax.swing.JLabel();
        cursorInsertShapeComboBox = new javax.swing.JComboBox<>();
        cursorOverwriteShapeModeLabel = new javax.swing.JLabel();
        cursorOverwriteShapeComboBox = new javax.swing.JComboBox<>();
        cursorBlinkingRateLabel = new javax.swing.JLabel();
        cursorBlinkingRateSpinner = new javax.swing.JSpinner();

        showShadowCursorCheckBox.setText("Show Shadow Cursor");
        showShadowCursorCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                showShadowCursorCheckBoxItemStateChanged(evt);
            }
        });

        cursorRenderingModeLabel.setText("Cursor Rendering Mode");

        cursorRenderingModeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PAINT", "XOR", "NEGATIVE" }));
        cursorRenderingModeComboBox.setSelectedIndex(1);
        cursorRenderingModeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cursorRenderingModeComboBoxActionPerformed(evt);
            }
        });

        cursorInsertShapeModeLabel.setText("Insert Cursor Shape");

        cursorInsertShapeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LINE_BOTTOM", "LINE_TOP", "LINE_LEFT", "LINE_RIGHT", "DOUBLE_BOTTOM", "DOUBLE_TOP", "DOUBLE_LEFT", "DOUBLE_RIGHT", "QUARTER_BOTTOM", "QUARTER_TOP", "QUARTER_LEFT", "QUARTER_RIGHT", "HALF_BOTTOM", "HALF_TOP", "HALF_LEFT", "HALF_RIGHT", "BOX", "FRAME", "CORNERS", "BOTTOM_CORNERS" }));
        cursorInsertShapeComboBox.setSelectedIndex(6);
        cursorInsertShapeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cursorInsertShapeComboBoxActionPerformed(evt);
            }
        });

        cursorOverwriteShapeModeLabel.setText("Overwrite Cursor Shape");

        cursorOverwriteShapeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "LINE_BOTTOM", "LINE_TOP", "LINE_LEFT", "LINE_RIGHT", "DOUBLE_BOTTOM", "DOUBLE_TOP", "DOUBLE_LEFT", "DOUBLE_RIGHT", "QUARTER_BOTTOM", "QUARTER_TOP", "QUARTER_LEFT", "QUARTER_RIGHT", "HALF_BOTTOM", "HALF_TOP", "HALF_LEFT", "HALF_RIGHT", "BOX", "FRAME", "CORNERS", "BOTTOM_CORNERS" }));
        cursorOverwriteShapeComboBox.setSelectedIndex(16);
        cursorOverwriteShapeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cursorOverwriteShapeComboBoxActionPerformed(evt);
            }
        });

        cursorBlinkingRateLabel.setText("Cursor Blinking Rate");

        cursorBlinkingRateSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cursorBlinkingRateSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cursorRenderingModeComboBox, 0, 253, Short.MAX_VALUE)
                    .addComponent(cursorInsertShapeComboBox, 0, 253, Short.MAX_VALUE)
                    .addComponent(cursorOverwriteShapeComboBox, 0, 253, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cursorRenderingModeLabel)
                            .addComponent(cursorInsertShapeModeLabel)
                            .addComponent(cursorOverwriteShapeModeLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(showShadowCursorCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cursorBlinkingRateLabel)
                        .addGap(63, 63, 63))
                    .addComponent(cursorBlinkingRateSpinner, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cursorBlinkingRateLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorBlinkingRateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorRenderingModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorRenderingModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorInsertShapeModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorInsertShapeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorOverwriteShapeModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cursorOverwriteShapeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showShadowCursorCheckBox)
                .addContainerGap(143, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showShadowCursorCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_showShadowCursorCheckBoxItemStateChanged
        ((CaretCapable) codeArea.getWorker()).setShowMirrorCursor(showShadowCursorCheckBox.isSelected());
    }//GEN-LAST:event_showShadowCursorCheckBoxItemStateChanged

    private void cursorRenderingModeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cursorRenderingModeComboBoxActionPerformed
        ((DefaultCodeAreaCaret) ((CaretCapable) codeArea.getWorker()).getCaret()).setRenderingMode(DefaultCodeAreaCaret.CursorRenderingMode.values()[cursorRenderingModeComboBox.getSelectedIndex()]);
    }//GEN-LAST:event_cursorRenderingModeComboBoxActionPerformed

    private void cursorInsertShapeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cursorInsertShapeComboBoxActionPerformed
        //        ((CaretCapable) codeArea.getWorker()).getCaret().setInsertCursorShape(DefaultCodeAreaCaret.CursorShape.values()[cursorInsertShapeComboBox.getSelectedIndex()]);
    }//GEN-LAST:event_cursorInsertShapeComboBoxActionPerformed

    private void cursorOverwriteShapeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cursorOverwriteShapeComboBoxActionPerformed
        //        ((CaretCapable) codeArea.getWorker()).getCaret().setOverwriteCursorShape(DefaultCodeAreaCaret.CursorShape.values()[cursorOverwriteShapeComboBox.getSelectedIndex()]);
    }//GEN-LAST:event_cursorOverwriteShapeComboBoxActionPerformed

    private void cursorBlinkingRateSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cursorBlinkingRateSpinnerStateChanged
        //        ((CaretCapable) codeArea.getWorker()).getCaret().setBlinkRate((Integer) cursorBlinkingRateSpinner.getValue());
    }//GEN-LAST:event_cursorBlinkingRateSpinnerStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cursorBlinkingRateLabel;
    private javax.swing.JSpinner cursorBlinkingRateSpinner;
    private javax.swing.JComboBox<String> cursorInsertShapeComboBox;
    private javax.swing.JLabel cursorInsertShapeModeLabel;
    private javax.swing.JComboBox<String> cursorOverwriteShapeComboBox;
    private javax.swing.JLabel cursorOverwriteShapeModeLabel;
    private javax.swing.JComboBox<String> cursorRenderingModeComboBox;
    private javax.swing.JLabel cursorRenderingModeLabel;
    private javax.swing.JCheckBox showShadowCursorCheckBox;
    // End of variables declaration//GEN-END:variables
}
