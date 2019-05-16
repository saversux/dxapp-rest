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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.NodeshutdownRequest;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Shutdown DXRAM nodes
 *
 * @author Julien Bernhart, 2018-11-26
 */
@Path("nodeshutdown")
public class Nodeshutdown extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("nodeshutdown", "nid, kill", "Shutdown DXRAM nodes, if kill is true, the node is killed, if kill is false the default shutdown is triggered");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String shutdownNode(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        NodeshutdownRequest nodeshutdownRequest;
        try {
            nodeshutdownRequest = gson.fromJson(body, NodeshutdownRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put nid and kill into body as json.");
        }
        String stringNid = nodeshutdownRequest.getNid();
        Boolean kill = Boolean.getBoolean(nodeshutdownRequest.getKill());

        if (stringNid == null || kill == null) {
            throw new BadRequestException("Please put nid and kill into body as json.");
        }

        if (!isNodeID(stringNid)) {
            throw new BadRequestException("Invalid NodeID");
        }

        short nid = NodeID.parse(stringNid);

        if (!ServiceHelper.getService(BootService.class).shutdownNode(nid, kill)) {
            return createMessage("Shutting down node "+NodeID.toHexString(nid)+" failed");
        } else {
            return createMessage("Shutting down node "+NodeID.toHexString(nid)+"...");
        }
    }
}
