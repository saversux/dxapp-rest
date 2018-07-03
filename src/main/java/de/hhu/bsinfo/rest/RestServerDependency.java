package de.hhu.bsinfo.rest;

import de.hhu.bsinfo.dxram.app.AbstractApplicationDependency;

public class RestServerDependency extends AbstractApplicationDependency {
    @Override
    public String[] getDependency() {
        //return new String[] {"lib/spark-core-2.7.2.jar", "lib/javax.servlet-api-3.1.0.jar", "lib/jetty-server-9.4.8.v20171121.jar", "lib/jetty-webapp-9.4.8.v20171121.jar"};
        return new String[] {"lib/spark-combined.jar"};
    }
}
