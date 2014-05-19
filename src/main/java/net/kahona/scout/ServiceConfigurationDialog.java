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
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A panel for configuring a service to monitor.
 *
 * @author Dennis Reedy
 */
public class ServiceConfigurationDialog extends JDialog {
    private JComponent component;
    enum Action {DELETED, MODIFIED, CANCELLED, ADDED, VIEWED}
    private Action action = Action.VIEWED;

    public ServiceConfigurationDialog() {
        this(new ServiceConfigurationPanel(null), true);
    }

    public ServiceConfigurationDialog(JComponent component, boolean addService) {
        super((JFrame) null, true);
        setResizable(false);
        if(addService)
            setTitle("Add service to monitor");
        else
            setTitle("Configured Services");
        this.component = component;
        //panel = new ServiceConfigurationPanel(null);

        JButton add = new JButton("Add");
        JButton close = new JButton("Close");
        add.setToolTipText("Add the service");
        add.addActionListener(new AddServiceActionListener(this));
        close.setToolTipText("Close the dialog");
        close.addActionListener(new DisposeActionListener(this));
        JPanel buttonPanel = new JPanel();
        if(addService) {
            buttonPanel.add(add);
        } else {
            JButton modify = new JButton("Modify");
            buttonPanel.add(modify);
            modify.addActionListener(new ModifyActionListener(this));
            JButton delete = new JButton("Delete");
            buttonPanel.add(delete);
            if(component instanceof JTabbedPane) {
                if(((JTabbedPane)component).getTabCount()==0) {
                    modify.setEnabled(false);
                    delete.setEnabled(false);
                }
            }
            delete.addActionListener(new DeleteActionListener(this));
        }
        buttonPanel.add(close);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(component, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(panel);
    }

    public String getServiceName() {
        if(getServiceConfigurationPanel()==null)
            return null;
        return getServiceConfigurationPanel().getServiceName();
    }

    public String getAddress() {
        if(getServiceConfigurationPanel()==null)
            return null;
        return getServiceConfigurationPanel().getAddress();
    }

    public String getFrequency() {
        if(getServiceConfigurationPanel()==null)
            return null;
        return getServiceConfigurationPanel().getFrequency();
    }

    public String getTimeUnit() {
        if(getServiceConfigurationPanel()==null)
            return null;
        return getServiceConfigurationPanel().getTimeUnit();
    }

    public Service getService() {
        if(getServiceConfigurationPanel()==null)
            return null;
        String serviceName = getServiceName();
        String address = getAddress();
        String frequency = getFrequency();
        String timeUnit = getTimeUnit();
        return new Service(serviceName, address, frequency, timeUnit);
    }

    private ServiceConfigurationPanel getServiceConfigurationPanel() {
        ServiceConfigurationPanel panel = null;
        if(component instanceof ServiceConfigurationPanel) {
            panel = (ServiceConfigurationPanel)component;
        }
        if(component instanceof JTabbedPane) {
            JTabbedPane tabs = (JTabbedPane)component;
            panel = (ServiceConfigurationPanel)tabs.getSelectedComponent();
        }
        return panel;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Field Entry Error", JOptionPane.ERROR_MESSAGE);
    }

    public Action getAction() {
        return action;
    }

    public void setVisible(boolean visible) {
        if (visible) {
            int width = 355;
            int height = 206;
            if(component instanceof JTabbedPane) {
                width = 445;
                height = 250;
            }
            pack();
            setSize(width, height);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int widthLoc = screenSize.width / 2 - width / 2;
            int heightLoc = screenSize.height / 2 - height / 2;
            setLocation(widthLoc, heightLoc);
        }
        super.setVisible(visible);
    }

    class AddServiceActionListener implements ActionListener {
        JDialog dialog;

        public AddServiceActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent ae) {
            if (getServiceName().length() == 0) {
                showError("You must provide a service name");
                return;
            }
            String address = getAddress();
            if (address.length() == 0) {
                showError("You must provide a network address");
                return;
            }
            try {
                String[] parts = address.split(":");
                if(parts.length!=2) {
                    showError("The address [" + address + "] is invalid, a host (or ip address) with a port is " +
                              "required. The entry must be in the form of host:port");
                    return;
                }
                InetAddress.getByName(parts[0]);
            } catch (UnknownHostException e) {
                showError("The address [" + address + "] is invalid: " + e.getMessage());
                return;
            }
            String frequency = getFrequency();
            if (frequency.length() == 0) {
                showError("You must provide a frequency to poll the service");
                return;
            }

            try {
                new Long(frequency);
            } catch (NumberFormatException e) {
                showError("The value " + frequency + " is not a number");
                return;
            }
            action = Action.ADDED;
            dialog.dispose();
        }
    }

    class DisposeActionListener implements ActionListener {
        JDialog dialog;

        public DisposeActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent ae) {
            action = Action.CANCELLED;
            dialog.dispose();
        }
    }

    class DeleteActionListener implements ActionListener {
        JDialog dialog;

        DeleteActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            action = Action.DELETED;
            dialog.dispose();
        }
    }

    class ModifyActionListener implements ActionListener {
        JDialog dialog;

        ModifyActionListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            action = Action.MODIFIED;
            dialog.dispose();
        }
    }
}
