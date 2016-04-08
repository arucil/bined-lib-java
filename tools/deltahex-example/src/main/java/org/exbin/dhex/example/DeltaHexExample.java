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
package org.exbin.dhex.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import org.exbin.dhex.deltahex.component.Hexadecimal;
import org.exbin.dhex.framework.deltahex.XBHexadecimalData;
import org.exbin.xbup.core.type.XBData;

/**
 * Hexadecimal editor examples.
 *
 * @version 0.1.0 2016/04/08
 * @author ExBin Project (http://exbin.org)
 */
public class DeltaHexExample {

    /**
     * Main method launching the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        final DeltaHexExampleDialog dialog = new DeltaHexExampleDialog(new javax.swing.JFrame(), true);
        Hexadecimal hexPanel = new Hexadecimal();
        XBData data = new XBData();
        data.loadFromStream(hexPanel.getClass().getResourceAsStream("/org/exbin/dhex/example/resources/lorem_1.txt"));
        hexPanel.setData(new XBHexadecimalData(data));
        dialog.setHexadecimal(hexPanel);
        hexPanel.requestFocus();

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (dialog instanceof JDialog) {
                    dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                }

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
}
