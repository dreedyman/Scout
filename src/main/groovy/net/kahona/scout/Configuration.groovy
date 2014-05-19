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
package net.kahona.scout

import groovy.xml.MarkupBuilder

/**
 * Scout configuration
 *
 * @author Dennis Reedy
 */
class Configuration {

    static Service[] read() {
        def configuredServices = []
        File scoutConf = getConfigurationFile()
        if (scoutConf.exists()) {
            def services = new XmlSlurper().parse(scoutConf)
            services.service.each { s ->
                Service service = new Service()
                service.name = s.'@name'
                service.address = s.'@address'
                service.frequency = s.'@frequency'
                service.timeUnit = s.'@timeUnit'
                configuredServices << service
            }
        }
        return configuredServices as Service[]
    }

    static void append(Service newService) {
        def configuredServices = []
        for(Service s : read())
            configuredServices << s
        configuredServices << newService
        write(configuredServices)
    }

    static void replace(Service serviceToReplace) {
        def configuredServices = []
        for(Service s : read()) {
            if(!s.name.equals(serviceToReplace.name))
                configuredServices << s
        }
        configuredServices << serviceToReplace
        write(configuredServices)
    }

    static void remove(Service serviceToRemove) {
        def configuredServices = []
        for(Service s : read()) {
            if(!s.name.equals(serviceToRemove.name))
                configuredServices << s
        }
        write(configuredServices)
    }

    private static void write(configuredServices) {
        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        builder.services() {
            configuredServices.each { s ->
                service(name:s.name, address:s.address, frequency:s.frequency, timeUnit:s.timeUnit)
            }
        }
        configurationFile.write(writer.toString())
    }

    private static File getKahonaHome() {
        File kahonaHomeDir = new File(System.getProperty("user.home") + File.separator + ".kahona")
        if (!kahonaHomeDir.exists()) {
            if (kahonaHomeDir.mkdir())
                System.out.println("Created " + kahonaHomeDir.getPath())
        }
        return kahonaHomeDir;
    }

    private static File getConfigurationFile() {
        return new File(getKahonaHome(), "scout.xml")
    }

}