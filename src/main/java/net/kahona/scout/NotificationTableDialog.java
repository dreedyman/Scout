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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A dialog for the {@link NotificationTable}.
 *
 * @author Dennis Reedy
 */
public class NotificationTableDialog extends JDialog {

    public NotificationTableDialog(NotificationTable notificationTable) {
        super((JFrame)null, "Service Notifications");
        JButton close = new JButton("Close");
        close.setToolTipText("Close the dialog");
        close.addActionListener(new DisposeActionListener(this));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(close);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(notificationTable, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(panel);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            int width = 630;
            int height = 200;
            pack();
            setSize(width, height);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int widthLoc = screenSize.width / 2 - width / 2;
            int heightLoc = screenSize.height / 2 - height / 2;
            setLocation(widthLoc, heightLoc);
        }
        super.setVisible(visible);
    }

    class DisposeActionListener implements ActionListener {
        JDialog dialog;

        DisposeActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            dialog.dispose();
        }
    }
}
