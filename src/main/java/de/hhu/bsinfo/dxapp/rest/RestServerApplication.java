/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxapp.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import de.hhu.bsinfo.dxapp.rest.cmd.AppList;
import de.hhu.bsinfo.dxapp.rest.cmd.AppRun;
import de.hhu.bsinfo.dxapp.rest.cmd.AppStats;
import de.hhu.bsinfo.dxapp.rest.cmd.Barrieralloc;
import de.hhu.bsinfo.dxapp.rest.cmd.Barrierfree;
import de.hhu.bsinfo.dxapp.rest.cmd.Barriersignon;
import de.hhu.bsinfo.dxapp.rest.cmd.Barriersize;
import de.hhu.bsinfo.dxapp.rest.cmd.Barrierstatus;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkcreate;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkdump;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkget;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunklist;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkput;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkremove;
import de.hhu.bsinfo.dxapp.rest.cmd.Compgrpls;
import de.hhu.bsinfo.dxapp.rest.cmd.Compgrpstatus;
import de.hhu.bsinfo.dxapp.rest.cmd.LogInfo;
import de.hhu.bsinfo.dxapp.rest.cmd.Loggerlevel;
import de.hhu.bsinfo.dxapp.rest.cmd.Lookuptree;
import de.hhu.bsinfo.dxapp.rest.cmd.Metadata;
import de.hhu.bsinfo.dxapp.rest.cmd.Monitoring;
import de.hhu.bsinfo.dxapp.rest.cmd.Nameget;
import de.hhu.bsinfo.dxapp.rest.cmd.Namelist;
import de.hhu.bsinfo.dxapp.rest.cmd.Namereg;
import de.hhu.bsinfo.dxapp.rest.cmd.Nodeinfo;
import de.hhu.bsinfo.dxapp.rest.cmd.Nodelist;
import de.hhu.bsinfo.dxapp.rest.cmd.Nodeshutdown;
import de.hhu.bsinfo.dxapp.rest.cmd.Statsprint;
import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;

/**
 * DXRAM Rest Server Application
 *
 * @author Julien Bernhart, 2018-11-26
 */
public class RestServerApplication extends Application {
    private volatile boolean run;
    private Gson gson;
    private Server server;
    private static final Logger LOGGER = LogManager.getFormatterLogger(RestServerApplication.class.getSimpleName());

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "RestServer";
    }

    /**
     * initialize server and register all commands
     * @param args
     */
    @Override
    public void main(String[] args) {
        ServiceHelper services = new ServiceHelper(this);

        gson = new Gson();
        run = true;

        int port;
        int maxThreads;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                maxThreads = args.length > 1 ? Integer.parseInt(args[1]) : -1;
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid port argument. Running with standard port 8009.");
                port = 8009;
                maxThreads = -1;
            }
        } else {
            LOGGER.error("Invalid port argument. Running with standard port 8009.");
            port = 8009;
            maxThreads = -1;
        }

        startServer(maxThreads, port);

        /*List<Object> commandInfo = new ArrayList<>();
        List<AbstractRestCommand> restCommands = new ArrayList<>();
        restCommands.add(new Chunkget());
        restCommands.add(new Chunklist());
        restCommands.add(new Nodelist());
        restCommands.add(new Namereg());
        restCommands.add(new Namelist());
        restCommands.add(new Chunkcreate());
        restCommands.add(new Chunkput());
        restCommands.add(new Chunkdump());
        restCommands.add(new Statsprint());
        restCommands.add(new Monitoring());
        restCommands.add(new AppList());
        restCommands.add(new AppRun());
        restCommands.add(new Chunkremove());
        restCommands.add(new Nodeinfo());
        restCommands.add(new Nameget());
        restCommands.add(new LogInfo());
        restCommands.add(new Lookuptree());
        restCommands.add(new Metadata());
        restCommands.add(new Nodeshutdown());
        restCommands.add(new Barrierstatus());
        restCommands.add(new Barrieralloc());
        restCommands.add(new Barriersignon());
        restCommands.add(new Barrierfree());
        restCommands.add(new Barriersize());
        restCommands.add(new Loggerlevel());
        restCommands.add(new AppStats());
        restCommands.add(new Compgrpls());
        restCommands.add(new Compgrpstatus());

        for (AbstractRestCommand c : restCommands) {
            commandInfo.add(c.getInfo());
        }

        server.get("/","application/json", (req, res) -> gson.toJson(commandInfo));
        server.get("/api", (request, response) -> getClass().getClassLoader().getResourceAsStream("api.json"));*/
        LOGGER.info("DXRest server started on port %d", port);

        while (run) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }

    protected <T extends de.hhu.bsinfo.dxram.engine.Service> T getService(final Class<T> p_class) {
        return super.getService(p_class);
    }

    /**
     * Start server on specific port
     * @param maxThreads
     * @param port
     */
    private void startServer(int maxThreads, int port) {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.packages("de.hhu.bsinfo.dxapp.rest");

        server = JettyHttpContainerFactory.createServer(baseUri, config);

        try {
            server.start();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Shuts the server down
     */
    @Override
    public void signalShutdown() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        run = false;
    }

}
