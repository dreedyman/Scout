/*
 * Copyright to the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.kahona.scout;

import com.sun.awt.AWTUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;

/**
 * An alert window that is always on top
 *
 * @author Dennis Reedy
 */
/* Suppress PMD warning, we want to call setOpaque() in the constructor */
@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
public class AlertWindow extends JFrame implements MouseListener {

    public AlertWindow(String message, String address, String dateStamp, Icon icon) {
        setAlwaysOnTop(true);
        setFocusable(false);
        setUndecorated(true);
        /*setSize(500, 150);
        int yPosition = System.getProperty("os.name").startsWith("Windows")?0:30;
        setLocation(500, yPosition);*/
        setBackground(Color.BLACK);
        addMouseListener(this);
        //setOpaque();
        AWTUtilities.setWindowOpacity(this, 0.70f);

        Color colorBack = new Color(88, 107, 132);
        Color colorFront = new Color(149, 179, 215);
        JPanel outer = new JPanel(new BorderLayout(8, 8));
        outer.setBackground(colorBack);
        outer.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel warningLabel;
        if(icon!=null)
            warningLabel = new JLabel(message, icon, SwingConstants.CENTER);
        else
            warningLabel = new JLabel(message, SwingConstants.CENTER);
        warningLabel.setForeground(Color.WHITE);
        Font font = new Font("Lucida Grande", 0, 18);
        warningLabel.setFont(font);

        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><p><center>");
        builder.append("Address: ").append(address);
        builder.append("<br>");
        builder.append(dateStamp);
        builder.append("</center></p></body></html>");
        JLabel dateTimeAddressLabel = new JLabel(builder.toString(), SwingConstants.CENTER);
        dateTimeAddressLabel.setForeground(Color.WHITE);

        JPanel pane = new JPanel(new BorderLayout(8, 8));
        //pane.setBackground(Color.GRAY);
        pane.setBackground(colorFront);
        pane.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        pane.add(warningLabel, BorderLayout.CENTER);
        pane.add(dateTimeAddressLabel, BorderLayout.SOUTH);

        outer.add(pane, BorderLayout.CENTER);
        getContentPane().add(outer);
    }

    public void mouseClicked(MouseEvent e) {
        this.setVisible(false);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    void setOpaque() {
        try {
            Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
            Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
            mSetWindowOpacity.invoke(null, this, 0.90f);
        } catch (Exception e) {
            /* Silently accept that we cannot set opaqueness */
        }
    }



    public void setVisible(boolean visible) {
        if (visible) {
            int width = 500;
            int height = 150;
            pack();
            setSize(width, height);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int xPosition = screenSize.width / 2 - width / 2;
            int yPosition = System.getProperty("os.name").startsWith("Windows")?0:30;
            setLocation(xPosition, yPosition);
        }
        super.setVisible(visible);
    }
}
