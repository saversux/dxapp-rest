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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.NodeshutdownRequest;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Shutdown DXRAM nodes
 *
 * @author Julien Bernhart, 2018-11-26
 */
public class Nodeshutdown extends AbstractRestCommand {
    public Nodeshutdown() {
        setInfo("nodeshutdown", "nid, kill", "Shutdown DXRAM nodes, if kill is true, the node is killed, if kill is false the default shutdown is triggered");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/nodeshutdown", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            NodeshutdownRequest nodeshutdownRequest;
            try {
                nodeshutdownRequest = gson.fromJson(request.body(), NodeshutdownRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid and kill into body as json.", response);
            }
            String stringNid = nodeshutdownRequest.getNid();
            Boolean kill = Boolean.getBoolean(nodeshutdownRequest.getKill());

            if (stringNid == null || kill == null) {
                return createError("Please put nid and kill into body as json.", response);
            }

            if (!isNodeID(stringNid)) {
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);

            if (!services.getService(BootService.class).shutdownNode(nid, kill)) {
                return createMessage("Shutting down node "+NodeID.toHexString(nid)+" failed");
            } else {
                return createMessage("Shutting down node "+NodeID.toHexString(nid)+"...");
            }

        });
    }
}
