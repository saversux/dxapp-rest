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

import de.hhu.bsinfo.dxapp.rest.cmd.responses.NodeInfoResponse;
import spark.Service;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.NodeinfoRequest;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.util.NodeCapabilities;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Get information about a node in the network
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 *  Modifications:
 *  - response body is sent with createMessageOfJavaObject method
 */
public class Nodeinfo extends AbstractRestCommand {
    public Nodeinfo() {
        setInfo("nodeinfo", "nid", "Get information about a node in the network");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/nodeinfo", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            NodeinfoRequest nodeinfoRequest;
            try {
                nodeinfoRequest = gson.fromJson(request.body(), NodeinfoRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid into body as json.", response);
            }
            String stringNid = nodeinfoRequest.getNid();

            BootService bootService = services.getService(BootService.class);

            if (stringNid == null) {
                return createError("Please put nid into body as json.", response);
            }

            if (!isNodeID(stringNid)) {
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);

            if (nid != NodeID.INVALID_ID) {
                if (!bootService.isNodeOnline(nid)) {
                    return createError("Node not available.", response);
                } else {
                    NodeInfoResponse nodeInfoResponse = new NodeInfoResponse(
                            NodeID.toHexString(nid),
                            bootService.getNodeRole(nid).toString(),
                            bootService.getNodeAddress(nid).toString(),
                            NodeCapabilities.toString(bootService.getNodeCapabilities(nid))
                    );
                    return createMessageOfJavaObject(nodeInfoResponse);
                }
            } else {
                return createError("NID invalid", response);
            }

        });
    }



}
