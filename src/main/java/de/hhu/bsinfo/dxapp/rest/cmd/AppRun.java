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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Start app on remote node
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 * Modifications:
 * - in the case of a successful response, no response body is sent
 */
public class AppRun extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("apprun", "nid, app", "Start app on remote node");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String runApp(String body) {
            if (body.equals("")) {
                throw new BadRequestException("No body in request.");
            }
            AppRunRequest apprunRequest;
            try {
                apprunRequest = gson.fromJson(body, AppRunRequest.class);
            } catch (JsonSyntaxException e) {
                throw new BadRequestException("Please put nid and name into body as json.");
            }
            String stringNid = apprunRequest.getNid();
            String appname = apprunRequest.getName();

            ApplicationService appService = ServiceHelper.getService(ApplicationService.class);

            if (stringNid == null || appname == null) {
                throw new BadRequestException("Please put nid and name into body as json.");

            }

            if (!isNodeID(stringNid)) {
                throw new BadRequestException("Invalid NodeID");
            }

            short nid = NodeID.parse(stringNid);

            if (nid == NodeID.INVALID_ID) {
                throw new BadRequestException("Invalid NodeID");
            }

            if (!appService.startApplication(nid, appname, null)) {
                throw new BadRequestException("Starting " + appname + " failed...");
            }

            return "";
    }
}
