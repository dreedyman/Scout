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
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Uses system tray to provide notification for network service availability.
 *
 * @author Dennis Reedy
 */
public class Scout {
    private SystemTray tray;
    private TrayIcon trayIcon;
    private final PopupMenu popup = new PopupMenu();
    private final Collection<ServiceMonitor> serviceMonitors = new ArrayList<ServiceMonitor>();
    private final NotificationTable notificationTable = new NotificationTable();

    /* Suppress PMD warning, we want to throw a RuntimeException if the system tray is not supported */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private Scout() {
        if (!SystemTray.isSupported())
            throw new RuntimeException("SystemTray is not supported");
        tray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(createImage("images/bulb.gif", "tray icon"));
    }

    private void create() {
        /* Create popup menu components */
        MenuItem aboutItem = new MenuItem("About");
        MenuItem addService = new MenuItem("Add service");
        MenuItem showServices = new MenuItem("Show configured services");
        MenuItem showMessages = new MenuItem("Show notifications");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(addService);
        popup.add(showServices);
        popup.add(showMessages);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Scout Notification System");

        addService.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ServiceConfigurationDialog dialog = new ServiceConfigurationDialog();
                dialog.setAlwaysOnTop(true);
                /*dialog.toFront();*/
                dialog.requestFocusInWindow();
                dialog.setVisible(true);
                if(dialog.getAction().equals(ServiceConfigurationDialog.Action.ADDED)) {
                    Service s = dialog.getService();
                    Configuration.append(s);
                    serviceMonitors.add(new ServiceMonitor(s, new ServiceNotificationListener()));
                }
            }
        });

        showServices.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JTabbedPane tabs = new JTabbedPane();
                for(ServiceMonitor monitor : serviceMonitors) {
                    tabs.add(monitor.getService().getName(), new ServiceConfigurationPanel(monitor.getService()));
                }
                ServiceConfigurationDialog dialog = new ServiceConfigurationDialog(tabs, false);
                dialog.setAlwaysOnTop(true);
                dialog.requestFocus();
                dialog.setVisible(true);
                Service service = dialog.getService();
                if(dialog.getAction()==ServiceConfigurationDialog.Action.DELETED) {
                    for(ServiceMonitor s : getServiceMonitors()) {
                        if(s.getService().getName().equals(service.getName())) {
                            s.terminate();
                            serviceMonitors.remove(s);
                            Configuration.remove(service);
                            break;
                        }
                    }
                } else if(dialog.getAction()==ServiceConfigurationDialog.Action.MODIFIED) {
                    for(ServiceMonitor s : getServiceMonitors()) {
                        if(s.getService().getName().equals(service.getName())) {
                            s.terminate();
                            serviceMonitors.remove(s);
                            Configuration.remove(service);
                            Configuration.append(service);
                            serviceMonitors.add(new ServiceMonitor(service, new ServiceNotificationListener()));
                            break;
                        }
                    }
                }
            }
        });

        showMessages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                NotificationTableDialog dialog = new NotificationTableDialog(notificationTable);
                dialog.setVisible(true);
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Image scoutIcon = createImage("images/scout.png", "Scout");
                JOptionPane.showMessageDialog(null,
                                              "Scout Notification System",
                                              "About Scout",
                                              JOptionPane.INFORMATION_MESSAGE,
                                              new ImageIcon(scoutIcon));
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(ServiceMonitor s : serviceMonitors)
                    s.terminate();
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    private ServiceMonitor[] getServiceMonitors() {
        return serviceMonitors.toArray(new ServiceMonitor[serviceMonitors.size()]);
    }

    private void show() throws AWTException {
        tray.add(trayIcon);
    }

    private void loadAndStart() throws IOException {
        for(Service s : Configuration.read()) {
            serviceMonitors.add(new ServiceMonitor(s, new ServiceNotificationListener()));
        }
    }

    private Image createImage(String path, String description) {
        URL imageURL = Thread.currentThread().getContextClassLoader().getResource(path);
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

    class ServiceNotificationListener implements NotificationListener {

        public void serviceIsNowAvailable(Service service) {
            String dateStamp = getDateStamp();
            notificationTable.addServiceNowAvailable(service, dateStamp);
            new AlertWindow("The ["+service.getName()+"] service is now available",
                            service.getAddress(),
                            dateStamp,
                            UIManager.getIcon("OptionPane.informationIcon")).setVisible(true);
        }

        public void serviceNotAvailable(Service service) {
            String dateStamp = getDateStamp();
            notificationTable.addFailedService(service, dateStamp);
            new AlertWindow("The ["+service.getName()+"] service is not reachable",
                            service.getAddress(),
                            dateStamp,
                            UIManager.getIcon("OptionPane.errorIcon")).setVisible(true);
        }

        String getDateStamp() {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss aa");
            Date date = new Date(System.currentTimeMillis());
            return dateFormat.format(date);
        }
    }

   public static void main(String[] args) {
        /* Turn off metal's use of bold fonts */
        //UIManager.put("swing.boldMetal", Boolean.FALSE);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Scout scout = new Scout();
                scout.create();
                try {
                    scout.loadAndStart();
                    scout.show();
                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
