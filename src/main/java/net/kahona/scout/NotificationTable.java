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
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * A table that shows all notifications that Scout has received. If scout exits, the history is lost.
 *
 * @author Dennis Reedy
 */
public class NotificationTable extends JPanel {
    private NotificationTableModel tableModel;
    JTable table;

    public NotificationTable() {
        super(new BorderLayout(8,8));
        tableModel = new NotificationTableModel();
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(135);
        table.getColumnModel().getColumn(2).setPreferredWidth(255);
        table.getColumnModel().getColumn(3).setPreferredWidth(155);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void addFailedService(Service s, String dateStamp) {
        tableModel.add(s, dateStamp, "The service is not reachable");
        tableModel.fireTableDataChanged();
    }

    public void addServiceNowAvailable(Service s, String dateStamp) {
        tableModel.add(s, dateStamp, "The service is now available");
        tableModel.fireTableDataChanged();
    }

    class NotificationTableModel extends AbstractTableModel {
        final List<ServiceDateTuple> items = Collections.synchronizedList(new ArrayList<ServiceDateTuple>());
        final String[] columnNames = new String[]{"Service", "Address", "Event", "Time"};

        void add(Service service, String dateStamp, String event) {
            items.add(new ServiceDateTuple(service, dateStamp, event));
        }

        public int getRowCount() {
            return items.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int column) {
            ServiceDateTuple s = items.get(row);
            String value = null;
            switch (column) {
                case 0:
                    value = s.service.getName();
                    break;
                case 1:
                    value = s.service.getAddress();
                    break;
                case 2:
                    value = s.event;
                    break;
                case 3:
                    value = s.dateStamp;
            }
            return value;
        }
    }

    private class ServiceDateTuple {
        Service service;
        String dateStamp;
        String event;

        private ServiceDateTuple(Service service, String dateStamp, String event) {
            this.service = service;
            this.dateStamp = dateStamp;
            this.event = event;
        }
    }
}
