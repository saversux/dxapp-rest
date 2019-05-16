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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.LogInfoRequest;
import de.hhu.bsinfo.dxram.log.LogService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Prints the log utilization of a given peer
 *
 * @author Julien Bernhart, 2018-11-26
 */
@Path("loginfo")
public class LogInfo extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("loginfo", "nid", "Prints the log utilization of given peer");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String logInfo(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        LogInfoRequest logInfoRequest;
        try {
            logInfoRequest = gson.fromJson(body, LogInfoRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put nid into body as json.");
        }
        String stringNid = logInfoRequest.getNid();

        if (stringNid == null) {
            throw new BadRequestException("Please put nid into body as json.");
        }

        if (!isNodeID(stringNid)) {
            throw new BadRequestException("Invalid NodeID");
        }

        short nid = NodeID.parse(stringNid);
        if (nid == NodeID.INVALID_ID) {
            throw new BadRequestException("NID invalid");
        }

        String utilization = ServiceHelper.getService(LogService.class).getCurrentUtilization(nid);

        return gson.toJson(utilization);
    }
}
