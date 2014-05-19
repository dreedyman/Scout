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

/**
 * A panel for service configuration.
 *
 * @author Dennis Reedy
 */
public class ServiceConfigurationPanel extends JPanel {
    private JTextField serviceName;
    private JTextField address;
    private JTextField frequency;
    private JComboBox timeUnits;

    public ServiceConfigurationPanel(Service service) {
        super(new BorderLayout(8, 8));
        String[] labels = new String[]{"Service Name", "Network Address", "Frequency", "Time Units"};
        String[] tips = new String[]{"The descriptive name of the service to monitor",
                                     "The network address (ip address and port) of the service to monitor",
                                     "The frequency upon which to poll the service for availability",
                                     "The time unit to poll",
                                     "Audio Alert"};
        JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
        JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
        serviceName = new JTextField();
        address = new JTextField();
        frequency = new JTextField();
        timeUnits = new JComboBox(new String[]{"MILLISECONDS", "SECONDS", "MINUTES"});
        timeUnits.setSelectedIndex(1);

        JComponent[] components = new JComponent[]{serviceName, address, frequency, timeUnits};
        for (int i = 0; i < labels.length; i += 1) {
            if (i < tips.length)
                components[i].setToolTipText(tips[i]);
            if (components[i] instanceof JTextField)
                ((JTextField) components[i]).setColumns(15);

            JLabel lab = new JLabel(labels[i], JLabel.RIGHT);
            lab.setLabelFor(components[i]);
            labelPanel.add(lab);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.add(components[i]);
            fieldPanel.add(p);
        }
        if(service!=null) {
            serviceName.setText(service.getName());
            address.setText(service.getAddress());
            frequency.setText(service.getFrequency());
            for(int i=0; i<timeUnits.getItemCount(); i++) {
                if(timeUnits.getItemAt(i).equals(service.getTimeUnit())) {
                    timeUnits.setSelectedIndex(i);
                    break;
                }
            }
        }
        add(labelPanel, BorderLayout.WEST);
        add(fieldPanel, BorderLayout.CENTER);
    }

    public String getServiceName() {
        return serviceName.getText();
    }

    public String getAddress() {
        return address.getText();
    }

    public String getFrequency() {
        return frequency.getText();
    }

    public String getTimeUnit() {
        return (String) timeUnits.getSelectedItem();
    }

}
