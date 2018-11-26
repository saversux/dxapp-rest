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

package de.hhu.bsinfo.dxapp.rest.cmd;

import spark.Service;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkputRequest;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Start app on remote node
 *
 * @author Julien Bernhart, 2018-11-26
 */
public class AppRun extends AbstractRestCommand {
    public AppRun() {
        setInfo("apprun", "nid, app", "Start app on remote node");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/apprun", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            AppRunRequest apprunRequest;
            try {
                apprunRequest = gson.fromJson(request.body(), AppRunRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid and name into body as json.", response);
            }
            String stringNid = apprunRequest.getNid();
            String appname = apprunRequest.getName();

            ApplicationService appService = services.getService(ApplicationService.class);

            if (stringNid == null || appname == null) {
                return createError("Please put nid and name into body as json.", response);
            }

            if (!isNodeID(stringNid)) {
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);

            if (nid == NodeID.INVALID_ID) {
                return createError("NodeID invalid", response);
            }

            if (!appService.startApplication(nid, appname, null)) {
                return createError("Starting " + appname + " failed...", response);
            }

            return createMessage(appname + " started successful");
        });
    }
}
