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

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 * Monitors a network service by pinging it.
 *
 * @author Dennis Reedy
 */
public class ServiceMonitor {
    private Service service;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private NotificationListener notificationListener;
    private boolean hadFailed;

    public ServiceMonitor(Service service, NotificationListener notificationListener) {
        this.service = service;
        this.notificationListener = notificationListener;
        executorService.scheduleAtFixedRate(new Monitor(),
                                            0,
                                            new Long(service.getFrequency()),
                                            TimeUnit.valueOf(service.getTimeUnit()));
    }

    void terminate() {
        executorService.shutdownNow();
    }

    public Service getService() {
        return service;
    }

    private void notifyOnFailure() {
        if(!hadFailed) {
            hadFailed = true;
            notificationListener.serviceNotAvailable(service);
        }
    }

    class Monitor implements Runnable {
        public void run() {
            Socket sock = null;
            try {
                String address = service.getAddress();
                String[] addressParts = address.split(":");
                int port = new Integer(addressParts[1]);
                InetAddress serviceAddress = InetAddress.getByName(addressParts[0]);
                SocketAddress socketAddress = new InetSocketAddress(serviceAddress, port);
                // Create an unbound socket
                sock = new Socket();
                // This method will block no more than timeoutMs.
                // If the timeout occurs, SocketTimeoutException is thrown.
                int timeoutMs = 2000;   // 2 seconds
                sock.connect(socketAddress, timeoutMs);
                if(hadFailed) {
                    notificationListener.serviceIsNowAvailable(service);
                }
                hadFailed = false;
            } catch (UnknownHostException e) {
                notifyOnFailure();
            } catch (SocketTimeoutException e) {
                notifyOnFailure();
            } catch (IOException e) {
                notifyOnFailure();
            } finally {
                if(sock!=null) {
                    try {
                        sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
