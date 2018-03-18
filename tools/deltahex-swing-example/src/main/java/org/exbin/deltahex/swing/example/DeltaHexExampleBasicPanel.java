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
package org.exbin.deltahex.swing.example;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.deltahex.swing.basic.DefaultCodeAreaWorker;
import org.exbin.deltahex.swing.example.panel.CursorPanel;
import org.exbin.deltahex.swing.example.panel.DecorationPanel;
import org.exbin.deltahex.swing.example.panel.LayoutPanel;
import org.exbin.deltahex.swing.example.panel.ModePanel;
import org.exbin.deltahex.swing.example.panel.ScrollingPanel;
import org.exbin.deltahex.swing.example.panel.StatePanel;

/**
 * Hexadecimal editor example panel.
 *
 * @version 0.2.0 2018/03/17
 * @author ExBin Project (http://exbin.org)
 */
public class DeltaHexExampleBasicPanel extends javax.swing.JPanel {

    private CodeArea codeArea;
    private final Map<JPanel, JPanel> tabMap = new HashMap<>();
    private JPanel activeTab;

    public DeltaHexExampleBasicPanel() {
        initComponents();
    }

    public void setCodeArea(final CodeArea codeArea) {
        this.codeArea = codeArea;

        DefaultCodeAreaWorker worker = (DefaultCodeAreaWorker) codeArea.getWorker();
        splitPane.setRightComponent(codeArea);
//        charAntialiasingComboBox.setSelectedIndex(codeArea.getCharAntialiasingMode().ordinal());
//        hexCharactersModeComboBox.setSelectedIndex(codeArea.getHexCharactersCase().ordinal());
//        showHeaderCheckBox.setSelected(codeArea.isShowHeader());

        ModePanel modePanel = new ModePanel(codeArea);
        StatePanel statePanel = new StatePanel(codeArea);
        LayoutPanel layoutPanel = new LayoutPanel(codeArea);
        DecorationPanel decorationPanel = new DecorationPanel(codeArea);
        ScrollingPanel scrollingPanel = new ScrollingPanel(codeArea);
        CursorPanel cursorPanel = new CursorPanel(codeArea);

        tabMap.put(modeTab, modePanel);
        tabMap.put(stateTab, statePanel);
        tabMap.put(layoutTab, layoutPanel);
        tabMap.put(decorationTab, decorationPanel);
        tabMap.put(scrollingTab, scrollingPanel);
        tabMap.put(cursorTab, cursorPanel);

        activeTab = modeTab;
        modeTab.add(modePanel, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();
        modeTab = new javax.swing.JPanel();
        stateTab = new javax.swing.JPanel();
        layoutTab = new javax.swing.JPanel();
        decorationTab = new javax.swing.JPanel();
        scrollingTab = new javax.swing.JPanel();
        cursorTab = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        tabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPaneStateChanged(evt);
            }
        });

        modeTab.setLayout(new java.awt.BorderLayout());
        tabbedPane.addTab("Mode", modeTab);

        stateTab.setLayout(new java.awt.BorderLayout());
        tabbedPane.addTab("State", stateTab);

        layoutTab.setLayout(new java.awt.BorderLayout());
        tabbedPane.addTab("Layout", layoutTab);

        decorationTab.setLayout(new java.awt.BorderLayout());
        tabbedPane.addTab("Decoration", decorationTab);

        scrollingTab.setLayout(new java.awt.BorderLayout());
        tabbedPane.addTab("Scrolling", scrollingTab);

        cursorTab.setLayout(new java.awt.BorderLayout());
        tabbedPane.addTab("Cursor", cursorTab);

        splitPane.setLeftComponent(tabbedPane);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPaneStateChanged
        Component tab = tabbedPane.getSelectedComponent();
        if (tab != null && tab != activeTab && !tabMap.isEmpty()) {
            if (activeTab != null) {
                ((JPanel) activeTab).remove(tabMap.get(activeTab));
            }

            ((JPanel) tab).add(tabMap.get((JPanel) tab), BorderLayout.CENTER);
            activeTab = (JPanel) tab;
        }
    }//GEN-LAST:event_tabbedPaneStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cursorTab;
    private javax.swing.JPanel decorationTab;
    private javax.swing.JPanel layoutTab;
    private javax.swing.JPanel modeTab;
    private javax.swing.JPanel scrollingTab;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JPanel stateTab;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
