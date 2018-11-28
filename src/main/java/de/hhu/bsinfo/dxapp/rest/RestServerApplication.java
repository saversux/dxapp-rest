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

import spark.Service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hhu.bsinfo.dxapp.rest.cmd.AppList;
import de.hhu.bsinfo.dxapp.rest.cmd.AppRun;
import de.hhu.bsinfo.dxapp.rest.cmd.Barrieralloc;
import de.hhu.bsinfo.dxapp.rest.cmd.Barrierstatus;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkcreate;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkdump;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkget;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunklist;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkput;
import de.hhu.bsinfo.dxapp.rest.cmd.Chunkremove;
import de.hhu.bsinfo.dxapp.rest.cmd.LogInfo;
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
import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.engine.AbstractDXRAMService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;

/**
 * DXRAM Rest Server Application
 *
 * @author Julien Bernhart, 2018-11-26
 */
public class RestServerApplication extends AbstractApplication {
    private static Service server;
    private volatile boolean run;
    private Gson gson;
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
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid port argument. Running with standard port 8009.");
                port = 8009;
            }
        } else {
            LOGGER.error("Invalid port argument. Running with standard port 8009.");
            port = 8009;
        }

        startServer(2, port);

        List<Object> commandInfo = new ArrayList<>();
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

        for (AbstractRestCommand c : restCommands) {
            c.register(server, services);
            commandInfo.add(c.getInfo());
        }

        server.get("/", (req, res) -> gson.toJson(commandInfo));
        LOGGER.debug("REST SERVER started");

        while (run) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected <T extends AbstractDXRAMService> T getService(final Class<T> p_class) {
        return super.getService(p_class);
    }

    /**
     * Start server on specific port
     * @param maxThreads
     * @param port
     */
    private static void startServer(int maxThreads, int port) {
        server = Service.ignite().port(port);
    }

    /**
     * Shuts the server down
     */
    @Override
    public void signalShutdown() {
        server.stop();
        run = false;
    }

}
